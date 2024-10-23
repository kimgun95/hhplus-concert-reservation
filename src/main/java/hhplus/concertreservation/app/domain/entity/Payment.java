package hhplus.concertreservation.app.domain.entity;

import hhplus.concertreservation.app.domain.constant.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PAYMENT_ID")
    private Long id;

    private Long userId;
    private Long reservationId;
    private Long amount;
    @Enumerated(EnumType.STRING) private PaymentStatus paymentStatus;

    public Payment(Long userId, Long reservationId, Long amount, PaymentStatus paymentStatus) {
        this.userId = userId;
        this.reservationId = reservationId;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
    }

    public static Payment create(Long userId, Long reservationId, Long amount, PaymentStatus paymentStatus) {
        return new Payment(
                userId, reservationId, amount, paymentStatus
        );
    }
}
