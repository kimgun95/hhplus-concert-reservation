package hhplus.concertreservation.app.domain.checker;

import hhplus.concertreservation.app.domain.constant.SeatStatus;
import hhplus.concertreservation.app.domain.entity.Seat;
import hhplus.concertreservation.config.exception.ErrorCode;
import hhplus.concertreservation.config.exception.FailException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SeatChecker {
    public Seat getOrThrowIfNotFound(Optional<Seat> optionalSeat) {
        return optionalSeat.orElseThrow(
                () -> new FailException(ErrorCode.SEAT_NOT_FOUND, FailException.LogLevel.ERROR));
    }

    public void validateIfAvailable(Seat seat) {
        if (seat.getSeatStatus() != SeatStatus.AVAILABLE) {
            throw new FailException(ErrorCode.RESERVED_SEAT, FailException.LogLevel.WARN);
        }
    }
}
