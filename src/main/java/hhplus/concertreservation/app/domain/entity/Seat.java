package hhplus.concertreservation.app.domain.entity;

import hhplus.concertreservation.app.domain.constant.SeatStatus;
import hhplus.concertreservation.config.exception.ErrorCode;
import hhplus.concertreservation.config.exception.FailException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Builder
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEAT_ID")
    private Long id;

    private Long concertId;
    private Long seatNumber;
    @Enumerated(EnumType.STRING) private SeatStatus seatStatus;

    public void changeStatus(SeatStatus seatStatus) {
        this.seatStatus = seatStatus;
    }

    public static Seat getOrThrowIfNotFound(Optional<Seat> optionalSeat) {
        return optionalSeat.orElseThrow(
                () -> new FailException(ErrorCode.SEAT_NOT_FOUND));
    }

    public static void validateIfAvailable(Seat seat) {
        if (seat.getSeatStatus() != SeatStatus.AVAILABLE) {
            throw new FailException(ErrorCode.RESERVED_SEAT);
        }
    }
}
