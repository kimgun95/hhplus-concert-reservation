package hhplus.concertreservation.domain.repository;

import hhplus.concertreservation.domain.entity.Ledger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LedgerRepository extends JpaRepository<Ledger, Long> {

}
