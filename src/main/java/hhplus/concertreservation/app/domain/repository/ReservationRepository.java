package hhplus.concertreservation.app.domain.repository;

import hhplus.concertreservation.app.domain.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

}
