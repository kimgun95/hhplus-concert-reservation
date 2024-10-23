package hhplus.concertreservation.app.interfaces.response;

import hhplus.concertreservation.app.domain.entity.Reservation;

public record ReservationResponse(
        Long reservationId
) {
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId()
        );
    }
}
