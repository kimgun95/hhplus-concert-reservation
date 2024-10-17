package hhplus.concertreservation.domain.repository;

import hhplus.concertreservation.domain.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
