package hhplus.concertreservation.domain.dto;

import hhplus.concertreservation.domain.entity.Queue;

public record QueryQueueDto(
        Queue queue,
        Long queueCount
) {
    public static QueryQueueDto of(Queue queue, Long queueCount) {
        return new QueryQueueDto(queue, queueCount);
    }
}
