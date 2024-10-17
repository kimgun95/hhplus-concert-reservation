package hhplus.concertreservation.domain.entity;

import hhplus.concertreservation.domain.constant.ReservationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
