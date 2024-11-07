package hhplus.concertreservation.app.interfaces.response;

import hhplus.concertreservation.app.domain.entity.QueueToken;

public record IssueQueueResponse(
        String token
) {
    public static IssueQueueResponse from(QueueToken queueToken) {
        return new IssueQueueResponse(
                queueToken.getToken()
        );
    }
}
