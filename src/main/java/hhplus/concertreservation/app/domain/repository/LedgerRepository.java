package hhplus.concertreservation.app.domain.repository;

import hhplus.concertreservation.app.domain.entity.Ledger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LedgerRepository extends JpaRepository<Ledger, Long> {

}
