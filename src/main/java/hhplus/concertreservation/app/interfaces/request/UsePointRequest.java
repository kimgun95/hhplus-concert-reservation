package hhplus.concertreservation.app.interfaces.request;

public record UsePointRequest(
        Long userId,
        Long reservationId,
        Long amount
) {
}
