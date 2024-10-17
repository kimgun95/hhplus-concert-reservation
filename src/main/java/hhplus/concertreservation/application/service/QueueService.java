package hhplus.concertreservation.application.service;

import hhplus.concertreservation.domain.constant.QueueStatus;
import hhplus.concertreservation.domain.dto.QueryQueueDto;
import hhplus.concertreservation.domain.entity.Queue;
import hhplus.concertreservation.domain.repository.QueueRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class QueueService {

    private final QueueRepository queueRepository;

    @Transactional
    public Queue getQueue(Long userId) {
        Queue searchedQueue = searchQueueByUserId(userId);
        if (searchedQueue != null) return searchedQueue;

        Queue queue = Queue.create(userId);
        return queueRepository.save(queue);
    } 

    public QueryQueueDto queryQueue(String token) {
        Queue searchedQueue = getQueueByToken(token);
        Long queueCount = queueRepository.countByUserQueueStatusAndUserIdLessThan(QueueStatus.READY, searchedQueue.getId());
        return QueryQueueDto.of(searchedQueue, queueCount);
    }

    public Queue getQueueByToken(String token) {
        return queueRepository.findByToken(token).orElseThrow(
                () -> new RuntimeException("대기열이 만료되었습니다")
        );
    }

    private Queue searchQueueByUserId(Long userId) {
        Optional<Queue> queue = queueRepository.findByUserId(userId);
        return queue.orElse(null);
    }

    @Scheduled(fixedRate = 10000)
    @Transactional
    public void checkActiveQueues() {
        int maxQueueCount = 5;

        Long activeCount = queueRepository.countByUserQueueStatus(QueueStatus.ACTIVE);
        int neededCount = maxQueueCount - activeCount.intValue();

        if (neededCount > 0) {
            List<Queue> readyUsers = queueRepository.findTopByUserQueueStatusOrderByUserIdAsc(
                    QueueStatus.READY, PageRequest.of(0, neededCount));

            readyUsers.forEach(queue -> queue.changeStatus(QueueStatus.ACTIVE));

            queueRepository.saveAll(readyUsers);
        }
    }

    @Scheduled(fixedRate = 10000)
    @Transactional
    public void deleteExpiredQueue() {
        LocalDateTime now = LocalDateTime.now();
        queueRepository.deleteByExpiredAtBefore(now);
    }
}
