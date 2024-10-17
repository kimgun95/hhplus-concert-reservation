package hhplus.concertreservation.interfaces.response;

import hhplus.concertreservation.domain.entity.Users;

public record UserPointResponse(
    Long userPoint
) {
    public static UserPointResponse from(Users users) {
        return new UserPointResponse(
                users.getUserPoint()
        );
    }
}
