package hhplus.concertreservation.app.interfaces.response;

import hhplus.concertreservation.app.domain.entity.Concert;

import java.time.LocalDateTime;

public record ConcertDateTimeResponse (
        Long concertId,
        LocalDateTime concertDate
) {
    public static ConcertDateTimeResponse from(Concert concert) {
        return new ConcertDateTimeResponse(
                concert.getId(), concert.getConcertDate()
        );
    }
}

