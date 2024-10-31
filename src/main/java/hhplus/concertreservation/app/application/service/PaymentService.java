package hhplus.concertreservation.app.application.service;

import hhplus.concertreservation.app.domain.checker.QueueChecker;
import hhplus.concertreservation.app.domain.checker.ReservationChecker;
import hhplus.concertreservation.app.domain.checker.SeatChecker;
import hhplus.concertreservation.app.domain.checker.UserChecker;
import hhplus.concertreservation.app.domain.constant.PaymentStatus;
import hhplus.concertreservation.app.domain.constant.ReservationStatus;
import hhplus.concertreservation.app.domain.constant.SeatStatus;
import hhplus.concertreservation.app.domain.constant.TransactionType;
import hhplus.concertreservation.app.domain.entity.*;
import hhplus.concertreservation.app.domain.repository.*;
import hhplus.concertreservation.config.exception.FailException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final LedgerRepository ledgerRepository;
    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final QueueRepository queueRepository;
    private final UserRepository userRepository;
    private final QueueChecker queueChecker;
    private final ReservationChecker reservationChecker;
    private final SeatChecker seatChecker;
    private final UserChecker userChecker;

    @Transactional
    public Payment payment(Long userId, Long reservationId, Long amount) {

        Reservation reservation = reservationChecker.getOrThrowIfNotFound(reservationRepository.findById(reservationId));
        User user = userChecker.getOrThrowIfNotFound(userRepository.findById(userId));
        log.info("결제 요청 유저의 ID : {}", reservation.getUserId());

        try {
            // 대기열 만료 확인 (만료된 토큰은 이미 스케줄러에 의해 삭제됨)
            Queue queue = queueChecker.getOrThrowIfNotFound(queueRepository.findByUserId(userId));
            // 유저 포인트 사용 (유저의 남은 잔액부터 확인)
            user.usePoints(amount);
            // 결제 내역 생성
            Payment payment = paymentRepository.save(Payment.create(user.getId(), reservationId, amount, PaymentStatus.SUCCESS));
            // 원장 생성
            ledgerRepository.save(Ledger.create(userId, amount, TransactionType.USE));
            // 예약 상태 성공으로 변환
            reservation.changeStatus(ReservationStatus.SUCCESS);
            // 만료 시키기 위해 만료 시간 변경
            queue.changeExpiredAt(LocalDateTime.now());
            log.info("결제 성공한 유저의 ID : {}", payment.getUserId());
            return payment;

        } catch (FailException e) {
            log.error("결제가 실패했습니다. reservation ID : {}", reservation.getId());
            // 예약 상태 실패로 변환
            reservation.changeStatus(ReservationStatus.FAILED);
            // 좌석 예약 가능 상태로 변환
            Seat seat = seatChecker.getOrThrowIfNotFound(seatRepository.findById(reservation.getSeatId()));
            seat.changeStatus(SeatStatus.AVAILABLE);
            // 결제 내역 생성
            return paymentRepository.save(Payment.create(user.getId(), reservationId, amount, PaymentStatus.FAILED));
        }
    }
}
