package hhplus.concertreservation.app.application.service;

import hhplus.concertreservation.app.domain.constant.QueueStatus;
import hhplus.concertreservation.app.domain.dto.QueryQueueDto;
import hhplus.concertreservation.app.domain.entity.Queue;
import hhplus.concertreservation.app.domain.repository.QueueRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class QueueService {

    private final QueueRepository queueRepository;

    @Transactional
    public Queue getQueue(Long userId) {
        return queueRepository.findByUserId(userId)
                .orElseGet(() -> queueRepository.save(Queue.create(userId)));
    } 

    public QueryQueueDto queryQueue(String token) {
        Queue searchedQueue = Queue.getOrThrowIfNotFound(queueRepository.findByToken(token));
        Long queueCount = queueRepository.countByUserQueueStatusAndUserIdLessThan(QueueStatus.READY, searchedQueue.getId());
        return QueryQueueDto.of(searchedQueue, queueCount);
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
