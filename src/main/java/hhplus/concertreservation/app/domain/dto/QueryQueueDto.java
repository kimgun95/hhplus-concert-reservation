package hhplus.concertreservation.app.domain.dto;

import hhplus.concertreservation.app.domain.entity.QueueToken;

public record QueryQueueDto(
        QueueToken queueToken,
        Long queueCount
) {
    public static QueryQueueDto of(QueueToken queueToken, Long queueCount) {
        return new QueryQueueDto(queueToken, queueCount);
    }
}
