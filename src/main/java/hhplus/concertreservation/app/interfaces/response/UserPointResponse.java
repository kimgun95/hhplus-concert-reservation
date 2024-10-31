package hhplus.concertreservation.app.interfaces.response;

import hhplus.concertreservation.app.domain.entity.User;

public record UserPointResponse(
    Long userPoint
) {
    public static UserPointResponse from(User user) {
        return new UserPointResponse(
                user.getUserPoint()
        );
    }
}
