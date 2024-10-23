package hhplus.concertreservation.app.interfaces.response;

import hhplus.concertreservation.app.domain.entity.Users;

public record UserPointResponse(
    Long userPoint
) {
    public static UserPointResponse from(Users users) {
        return new UserPointResponse(
                users.getUserPoint()
        );
    }
}
