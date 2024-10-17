package hhplus.concertreservation.interfaces.response;

import hhplus.concertreservation.domain.entity.Queue;

public record IssueQueueResponse(
        String token
) {
    public static IssueQueueResponse from(Queue queue) {
        return new IssueQueueResponse(
                queue.getToken()
        );
    }
}
