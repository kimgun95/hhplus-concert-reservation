package hhplus.concertreservation.app.domain.entity;

import hhplus.concertreservation.app.domain.constant.QueueStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Builder
public class Queue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "QUEUE_ID")
    private Long id;

    private Long userId;
    private String token;
    @Enumerated(EnumType.STRING) private QueueStatus userQueueStatus;
    private LocalDateTime expiredAt;

    public Queue(Long userId, String token, QueueStatus queueStatus) {
        this.userId = userId;
        this.token = token;
        this.userQueueStatus = queueStatus;
        this.expiredAt = null;
    }

    public static Queue create(Long userId) {
        return new Queue(
                userId, UUID.randomUUID().toString(), QueueStatus.READY
        );
    }

    public void changeStatus(QueueStatus queueStatus) {
        this.userQueueStatus = queueStatus;
    }

    public void changeExpiredAt(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
    }
}
