package hhplus.concertreservation.app.interfaces.response;

import hhplus.concertreservation.app.domain.entity.Seat;

public record ConcertSeatResponse(
        Long seatId,
        Long seatNumber
) {
    public static ConcertSeatResponse from(Seat seat) {
        return new ConcertSeatResponse(
                seat.getId(),
                seat.getSeatNumber()
        );
    }
}
