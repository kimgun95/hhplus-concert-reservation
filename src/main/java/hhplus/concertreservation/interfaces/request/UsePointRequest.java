package hhplus.concertreservation.interfaces.request;

public record UsePointRequest(
        Long reservationId,
        Long amount
) {
}
