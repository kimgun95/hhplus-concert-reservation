package hhplus.concertreservation.app.domain.entity;

import hhplus.concertreservation.app.domain.constant.SeatStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
