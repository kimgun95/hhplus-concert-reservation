package hhplus.concertreservation.app.application.service;

import hhplus.concertreservation.app.domain.constant.PaymentStatus;
import hhplus.concertreservation.app.domain.constant.ReservationStatus;
import hhplus.concertreservation.app.domain.constant.SeatStatus;
import hhplus.concertreservation.app.domain.constant.TransactionType;
import hhplus.concertreservation.app.domain.entity.*;
import hhplus.concertreservation.app.domain.repository.LedgerRepository;
import hhplus.concertreservation.app.domain.repository.PaymentRepository;
import hhplus.concertreservation.app.domain.repository.ReservationRepository;
import hhplus.concertreservation.app.domain.repository.SeatRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final LedgerRepository ledgerRepository;
    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final QueueService queueService;
    private final UsersService usersService;

    @Transactional
    public void chargeUserPoint(String token, Long amount) {
        Queue queue = queueService.getQueueByToken(token);
        // 유저의 포인트 충전
        Users user = usersService.getUserByUserId(queue.getUserId());
        user.calculateUserPoint(amount);
        // 원장 생성
        ledgerRepository.save(Ledger.create(user.getId(), amount, TransactionType.CHARGE));
    }

    public Users getUserPoint(String token) {
        Queue queue = queueService.getQueueByToken(token);
        return usersService.getUserByUserId(queue.getUserId());
    }

    @Transactional
    public Payment useUserPoint(String token, Long reservationId, Long amount) {

        try {
            Queue queue = queueService.getQueueByToken(token);
            // 예약 상태 성공으로 변환
            Reservation reservation = reservationRepository.findById(reservationId).orElse(null);
            reservation.changeStatus(ReservationStatus.SUCCESS);
            // 유저 포인트 사용
            Users user = usersService.getUserByUserId(queue.getUserId());
            user.calculateUserPoint(amount * -1);
            // 원장 생성
            ledgerRepository.save(Ledger.create(user.getId(), amount, TransactionType.USE));
            // 만료 시키기 위해 만료 시간 변경
            queue.changeExpiredAt(LocalDateTime.now());
            // 결제 내역 생성
            return paymentRepository.save(Payment.create(user.getId(), reservationId, amount, PaymentStatus.SUCCESS));

        } catch (RuntimeException e) {
            // 예약 상태 실패로 변환
            Reservation reservation = reservationRepository.findById(reservationId).orElse(null);
            reservation.changeStatus(ReservationStatus.FAILED);
            // 좌석 예약 가능 상태로 변환
            Seat seat = seatRepository.findById(reservation.getSeatId()).orElse(null);
            seat.changeStatus(SeatStatus.AVAILABLE);

            throw new RuntimeException("결제 중 오류가 발생했습니다");
        }
    }
}
