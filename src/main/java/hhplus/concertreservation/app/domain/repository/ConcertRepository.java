package hhplus.concertreservation.app.domain.repository;

import hhplus.concertreservation.app.domain.entity.Concert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConcertRepository extends JpaRepository<Concert, Long> {

    List<Concert> findByConcertName(String concertName);
}
