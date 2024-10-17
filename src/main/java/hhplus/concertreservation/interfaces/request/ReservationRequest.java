package hhplus.concertreservation.interfaces.request;

public record ReservationRequest(
        Long concertId,
        Long seatNumber
) {
}
