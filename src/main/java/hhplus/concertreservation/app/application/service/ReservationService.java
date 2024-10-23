package hhplus.concertreservation.app.application.service;

import hhplus.concertreservation.app.domain.constant.SeatStatus;
import hhplus.concertreservation.app.domain.entity.Queue;
import hhplus.concertreservation.app.domain.entity.Reservation;
import hhplus.concertreservation.app.domain.entity.Seat;
import hhplus.concertreservation.app.domain.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final SeatService seatService;
    private final QueueService queueService;

    @Transactional
    public Reservation reserveSeat(String token, Long concertId, Long seatNumber) {
        // 비관적 락
        Seat seat = seatService.getSeat(concertId, seatNumber);
        // AVAILABLE이 아니라면 예외 throw
        if (seat.getSeatStatus() == SeatStatus.RESERVED) throw new RuntimeException("이미 예약된 좌석입니다");
        // 대기열 5분 설정
        Queue queue = queueService.getQueueByToken(token);
        queue.changeExpiredAt(LocalDateTime.now().plusMinutes(5));
        // 좌석 예약 상태로 변환
        seat.changeStatus(SeatStatus.RESERVED);
        return reservationRepository.save(
                Reservation.create(queue.getUserId(), seat.getId())
        );
    }
}
