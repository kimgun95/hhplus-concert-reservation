package hhplus.concertreservation.app.domain.entity;

import hhplus.concertreservation.app.domain.constant.ReservationStatus;
import hhplus.concertreservation.config.exception.ErrorCode;
import hhplus.concertreservation.config.exception.FailException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RESERVATION_ID")
    private Long id;

    private Long userId;
    private Long seatId;
    @Enumerated(EnumType.STRING) private ReservationStatus reservationStatus;

    public Reservation(Long userId, Long seatId) {
        this.userId = userId;
        this.seatId = seatId;
        this.reservationStatus = ReservationStatus.PENDING;
    }

    public static Reservation create(Long userId, Long seatId) {
        return new Reservation(
                userId, seatId
        );
    }

    public void changeStatus(ReservationStatus status) {
        this.reservationStatus = status;
    }

    public static Reservation getOrThrowIfNotFound(Optional<Reservation> optionalReservation) {
        return optionalReservation.orElseThrow(
                () -> new FailException(ErrorCode.RESERVATION_NOT_FOUND));
    }
}
