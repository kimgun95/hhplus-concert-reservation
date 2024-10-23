package hhplus.concertreservation.app.interfaces.response;

import hhplus.concertreservation.app.domain.constant.QueueStatus;
import hhplus.concertreservation.app.domain.dto.QueryQueueDto;

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
