package hhplus.concertreservation.app.domain.entity;

import hhplus.concertreservation.app.domain.constant.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class Ledger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LEDGER_ID")
    private Long id;

    Long userId;
    Long amount;
    @Enumerated(EnumType.STRING) TransactionType transactionType;
    LocalDateTime updateMillis;

    public Ledger(Long userId, Long amount, TransactionType transactionType) {
        this.userId = userId;
        this.amount = amount;
        this.transactionType = transactionType;
        this.updateMillis = LocalDateTime.now();
    }

    public static Ledger create(Long userId, Long amount, TransactionType transactionType) {
        return new Ledger(
                userId, amount, transactionType
        );
    }
}
