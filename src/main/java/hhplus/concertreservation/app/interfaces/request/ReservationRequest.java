package hhplus.concertreservation.app.interfaces.request;

public record ReservationRequest(
        Long concertId,
        Long seatNumber
) {
}
