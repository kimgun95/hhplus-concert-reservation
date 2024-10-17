package hhplus.concertreservation.domain.repository;

import hhplus.concertreservation.domain.constant.QueueStatus;
import hhplus.concertreservation.domain.entity.Queue;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface QueueRepository extends JpaRepository<Queue, Integer> {

    Optional<Queue> findByUserId(Long userId);

    Optional<Queue> findByToken(String token);

    Long countByUserQueueStatusAndUserIdLessThan(QueueStatus userQueueStatus, Long userId);

    Long countByUserQueueStatus(QueueStatus userQueueStatus);

    List<Queue> findTopByUserQueueStatusOrderByUserIdAsc(QueueStatus status, Pageable pageable);

    @Modifying
    void deleteByExpiredAtBefore(LocalDateTime now);
}
