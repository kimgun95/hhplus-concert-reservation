package hhplus.concertreservation.app.interfaces.request;

public record UsePointRequest(
        Long reservationId,
        Long amount
) {
}
