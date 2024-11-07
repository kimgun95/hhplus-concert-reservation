package hhplus.concertreservation.app.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hhplus.concertreservation.app.domain.constant.QueueStatus;
import hhplus.concertreservation.app.domain.dto.QueueRank;
import hhplus.concertreservation.app.domain.entity.QueueToken;
import hhplus.concertreservation.config.exception.ErrorCode;
import hhplus.concertreservation.config.exception.FailException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class QueueService {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String WAITING_QUEUE_KEY = "waiting:queue";  // ZSet
    private static final String ACTIVE_QUEUE_KEY = "active:queue";    // Set
    private static final int BATCH_SIZE = 100;  // 한 번에 활성화할 토큰 수
    private static final long TOKEN_TTL = 60L; // 활성화된 토큰 유효시간 30분

    /**
     * 대기열 토큰 발급
     */
    public QueueToken issueToken(Long userId) {
        // 기존 토큰이 있는지 확인
        String existingToken = findTokenByUserId(userId);
        if (existingToken != null) {
            return getTokenData(existingToken);
        }

        // 새 토큰 생성
        QueueToken token = QueueToken.create(userId);
        try {
            // Waiting Queue에 추가 (ZSet)
            redisTemplate.opsForZSet().add(
                    WAITING_QUEUE_KEY,
                    objectMapper.writeValueAsString(token),
                    token.getTimestamp()
            );
            log.info("Token issued for user {}: {}", userId, token.getToken());
            return token;
        } catch (JsonProcessingException e) {
            throw new FailException(ErrorCode.CONFLICT_TOKEN_ERROR);
        }
    }

    /**
     * 대기열 상태 조회
     */
    public QueueRank queryToken(String token) {
        // Active Set에서 먼저 확인
        Boolean isActive = redisTemplate.opsForSet().isMember(
                ACTIVE_QUEUE_KEY,
                token
        );

        if (Boolean.TRUE.equals(isActive)) {
            return new QueueRank(0L, QueueStatus.ACTIVE);
        }

        // Waiting Queue에서 순위 확인
        Long rank = redisTemplate.opsForZSet().rank(WAITING_QUEUE_KEY, token);
        if (rank != null) {
            return new QueueRank(rank + 1, QueueStatus.WAITING);
        }

        throw new FailException(ErrorCode.EXPIRED_QUEUE_TOKEN);
    }

    /**
     * 대기열 -> 활성 상태로 전환 (스케줄러)
     */
    @Scheduled(fixedRate = 10000) // 10초마다 실행
    public void processWaitingQueue() {
        // 현재 활성 토큰 수 확인
        Long activeCount = redisTemplate.opsForSet().size(ACTIVE_QUEUE_KEY);
        int neededCount = BATCH_SIZE - (activeCount != null ? activeCount.intValue() : 0);

        if (neededCount > 0) {
            // 가장 오래 기다린 토큰들 선택
            Set<String> tokenSet = redisTemplate.opsForZSet()
                    .range(WAITING_QUEUE_KEY, 0, neededCount - 1);

            if (tokenSet != null && !tokenSet.isEmpty()) {
                for (String tokenJson : tokenSet) {
                    try {
                        QueueToken token = objectMapper.readValue(tokenJson, QueueToken.class);

                        // Waiting Queue에서 제거
                        redisTemplate.opsForZSet().remove(
                                WAITING_QUEUE_KEY,
                                tokenJson
                        );

                        // Active Set에 추가
                        redisTemplate.opsForSet().add(
                                ACTIVE_QUEUE_KEY,
                                token.getToken()
                        );

                        // TTL 설정
                        redisTemplate.expire(
                                ACTIVE_QUEUE_KEY + ":" + token.getToken(),
                                Duration.ofSeconds(TOKEN_TTL)
                        );

                        log.info("Token activated: {}", token.getToken());
                    } catch (JsonProcessingException e) {
                        log.error("Token 처리 중 오류 발생", e);
                    }
                }
            }
        }
    }

    /**
     * UserId로 토큰 찾기
     */
    private String findTokenByUserId(Long userId) {
        Set<String> tokens = redisTemplate.opsForZSet().range(
                WAITING_QUEUE_KEY,
                0, -1
        );

        if (tokens != null) {
            return tokens.stream()
                    .filter(tokenJson -> {
                        try {
                            QueueToken token = objectMapper.readValue(tokenJson, QueueToken.class);
                            return token.getUserId().equals(userId);
                        } catch (JsonProcessingException e) {
                            return false;
                        }
                    })
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    private QueueToken getTokenData(String tokenJson) {
        try {
            return objectMapper.readValue(tokenJson, QueueToken.class);
        } catch (JsonProcessingException e) {
            throw new FailException(ErrorCode.CONFLICT_TOKEN_ERROR);
        }
    }
}
