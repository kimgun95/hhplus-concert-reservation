package hhplus.concertreservation.app.interfaces.request;

public record ReservationRequest(
        Long userId,
        Long concertId,
        Long seatNumber
) {
}
