package hhplus.concertreservation.app.interfaces.response;

import hhplus.concertreservation.app.domain.entity.Queue;

public record IssueQueueResponse(
        String token
) {
    public static IssueQueueResponse from(Queue queue) {
        return new IssueQueueResponse(
                queue.getToken()
        );
    }
}
