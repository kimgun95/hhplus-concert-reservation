package hhplus.concertreservation.interfaces.response;

import hhplus.concertreservation.domain.entity.Reservation;

public record ReservationResponse(
        Long reservationId
) {
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId()
        );
    }
}
