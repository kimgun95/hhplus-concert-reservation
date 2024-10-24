package hhplus.concertreservation.app.application.service;

import hhplus.concertreservation.app.domain.constant.SeatStatus;
import hhplus.concertreservation.app.domain.entity.Queue;
import hhplus.concertreservation.app.domain.entity.Reservation;
import hhplus.concertreservation.app.domain.entity.Seat;
import hhplus.concertreservation.app.domain.repository.QueueRepository;
import hhplus.concertreservation.app.domain.repository.ReservationRepository;
import hhplus.concertreservation.app.domain.repository.SeatRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final QueueRepository queueRepository;

    @Transactional
    public Reservation reserveSeat(String token, Long concertId, Long seatNumber) {
        // 비관적 락
        Seat seat = Seat.getOrThrowIfNotFound(seatRepository.findByConcertIdAndSeatNumber(concertId, seatNumber));
        // AVAILABLE이 아니라면 예외
        Seat.validateIfAvailable(seat);
        // 대기열 5분 설정
        Queue queue = Queue.getOrThrowIfNotFound(queueRepository.findByToken(token));
        queue.changeExpiredAt(LocalDateTime.now().plusMinutes(5));
        log.info("좌석 예약 요청 유저의 ID : {}", queue.getUserId());
        // 좌석 예약 상태로 변환
        seat.changeStatus(SeatStatus.RESERVED);
        // 예약 생성
        return reservationRepository.save(Reservation.create(queue.getUserId(), seat.getId()));
    }
}
