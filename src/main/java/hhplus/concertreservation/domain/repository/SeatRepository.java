package hhplus.concertreservation.domain.repository;

import hhplus.concertreservation.domain.constant.SeatStatus;
import hhplus.concertreservation.domain.entity.Seat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByConcertIdAndSeatStatus(Long concertId, SeatStatus seatStatus);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Seat> findByConcertIdAndSeatNumber(Long concertId, Long seatNumber);
}
