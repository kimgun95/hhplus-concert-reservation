package hhplus.concertreservation.interfaces.response;

import hhplus.concertreservation.domain.constant.QueueStatus;
import hhplus.concertreservation.domain.dto.QueryQueueDto;

public record QueryQueueResponse(
    QueueStatus queueStatus,
    Long queueCount
) {
    public static QueryQueueResponse from(QueryQueueDto queryQueueDto) {
        return new QueryQueueResponse(
                queryQueueDto.queue().getUserQueueStatus(),
                queryQueueDto.queueCount()
        );
    }
}
