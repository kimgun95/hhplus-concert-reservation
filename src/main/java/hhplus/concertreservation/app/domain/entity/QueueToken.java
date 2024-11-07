package hhplus.concertreservation.app.domain.entity;

import hhplus.concertreservation.app.domain.constant.QueueStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class QueueToken {
    private String token;
    private Long userId;
    private Long timestamp;
    private QueueStatus status;

    public static QueueToken create(Long userId) {
        return new QueueToken(
                UUID.randomUUID().toString(),
                userId,
                System.currentTimeMillis(),
                QueueStatus.WAITING
        );
    }
}
