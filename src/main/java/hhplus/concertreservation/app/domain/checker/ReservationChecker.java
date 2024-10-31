package hhplus.concertreservation.app.domain.checker;

import hhplus.concertreservation.app.domain.entity.Reservation;
import hhplus.concertreservation.config.exception.ErrorCode;
import hhplus.concertreservation.config.exception.FailException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ReservationChecker {
    public Reservation getOrThrowIfNotFound(Optional<Reservation> optionalReservation) {
        return optionalReservation.orElseThrow(
                () -> new FailException(ErrorCode.RESERVATION_NOT_FOUND, FailException.LogLevel.ERROR));
    }
}
