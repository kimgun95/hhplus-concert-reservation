package hhplus.concertreservation.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hhplus.concertreservation.app.application.service.QueueService;
import hhplus.concertreservation.app.domain.constant.QueueStatus;
import hhplus.concertreservation.app.domain.dto.QueueRank;
import hhplus.concertreservation.app.domain.entity.QueueToken;
import hhplus.concertreservation.config.exception.ErrorCode;
import hhplus.concertreservation.config.exception.FailException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QueueServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ZSetOperations<String, String> zSetOperations;

    @Mock
    private SetOperations<String, String> setOperations;

    @InjectMocks
    private QueueService queueService;

//    @BeforeEach
//    void setUp() {
//        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
//        when(redisTemplate.opsForSet()).thenReturn(setOperations);
//    }

    @Test
    @DisplayName("새로운 토큰 발급 성공")
    void issueToken_Success() throws JsonProcessingException {
        // given
        Long userId = 1L;
        String tokenJson = "{\"token\":\"test-token\",\"userId\":1,\"timestamp\":123456789}";

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        given(zSetOperations.range(eq("waiting:queue"), eq(0L), eq(-1L)))
                .willReturn(new HashSet<>());
        given(objectMapper.writeValueAsString(any(QueueToken.class)))
                .willReturn(tokenJson);
        given(zSetOperations.add(eq("waiting:queue"), eq(tokenJson), anyDouble()))
                .willReturn(true);

        // when
        QueueToken result = queueService.issueToken(userId);

        // then
        assertThat(result).isNotNull();
        verify(zSetOperations).add(eq("waiting:queue"), any(String.class), anyDouble());
    }

    @Test
    @DisplayName("기존 사용자의 토큰 조회 성공")
    void issueToken_ExistingUser_ReturnsExistingToken() throws JsonProcessingException {
        // given
        Long userId = 1L;
        QueueToken existingToken = QueueToken.create(userId);
        String tokenJson = "{\"token\":\"existing-token\",\"userId\":1,\"timestamp\":123456789}";
        Set<String> tokens = new HashSet<>();
        tokens.add(tokenJson);

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        given(zSetOperations.range(eq("waiting:queue"), eq(0L), eq(-1L)))
                .willReturn(tokens);
        given(objectMapper.readValue(tokenJson, QueueToken.class))
                .willReturn(existingToken);

        // when
        QueueToken result = queueService.issueToken(userId);

        // then
        assertThat(result).isEqualTo(existingToken);
    }

    @Test
    @DisplayName("활성화된 토큰 조회 성공")
    void queryToken_ActiveToken_Success() {
        // given
        String token = "test-token";
        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        given(setOperations.isMember(eq("active:queue"), eq(token)))
                .willReturn(true);

        // when
        QueueRank result = queueService.queryToken(token);

        // then
        assertThat(result.getRank()).isEqualTo(0L);
        assertThat(result.getStatus()).isEqualTo(QueueStatus.ACTIVE);
    }

    @Test
    @DisplayName("대기 중인 토큰 조회 성공")
    void queryToken_WaitingToken_Success() {
        // given
        String token = "test-token";
        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        given(setOperations.isMember(eq("active:queue"), eq(token)))
                .willReturn(false);
        given(zSetOperations.rank(eq("waiting:queue"), eq(token)))
                .willReturn(5L);

        // when
        QueueRank result = queueService.queryToken(token);

        // then
        assertThat(result.getRank()).isEqualTo(6L); // rank + 1
        assertThat(result.getStatus()).isEqualTo(QueueStatus.WAITING);
    }

    @Test
    @DisplayName("존재하지 않는 토큰 조회 시 예외 발생")
    void queryToken_NonExistentToken_ThrowsException() {
        // given
        String token = "non-existent-token";
        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        given(setOperations.isMember(eq("active:queue"), eq(token)))
                .willReturn(false);
        given(zSetOperations.rank(eq("waiting:queue"), eq(token)))
                .willReturn(null);

        // when & then
        FailException exception = assertThrows(FailException.class,
                () -> queueService.queryToken(token));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.EXPIRED_QUEUE_TOKEN);
    }

    @Test
    @DisplayName("토큰 발급 시 JSON 처리 실패하면 예외 발생")
    void issueToken_JsonProcessingError_ThrowsException() throws JsonProcessingException {
        // given
        Long userId = 1L;
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        given(zSetOperations.range(eq("waiting:queue"), eq(0L), eq(-1L)))
                .willReturn(new HashSet<>());
        given(objectMapper.writeValueAsString(any(QueueToken.class)))
                .willThrow(new JsonProcessingException("JSON 처리 실패") {});

        // when & then
        FailException exception = assertThrows(FailException.class,
                () -> queueService.issueToken(userId));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.CONFLICT_TOKEN_ERROR);
    }

    @Test
    @DisplayName("대기열 처리 성공")
    void processWaitingQueue_Success() throws JsonProcessingException {
        // given
        Set<String> waitingTokens = new HashSet<>();
        String tokenJson = "{\"token\":\"test-token\",\"userId\":1,\"timestamp\":123456789}";
        waitingTokens.add(tokenJson);
        QueueToken queueToken = QueueToken.create(1L);

        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        given(setOperations.size(eq("active:queue")))
                .willReturn(0L);
        given(zSetOperations.range(eq("waiting:queue"), eq(0L), eq(99L)))
                .willReturn(waitingTokens);
        given(objectMapper.readValue(eq(tokenJson), eq(QueueToken.class)))
                .willReturn(queueToken);

        // when
        queueService.processWaitingQueue();

        // then
        verify(zSetOperations).remove(eq("waiting:queue"), eq(tokenJson));
        verify(setOperations).add(eq("active:queue"), eq(queueToken.getToken()));
    }
}