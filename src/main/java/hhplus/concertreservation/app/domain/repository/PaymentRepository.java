package hhplus.concertreservation.app.domain.repository;

import hhplus.concertreservation.app.domain.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
