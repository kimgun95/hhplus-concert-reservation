package hhplus.concertreservation.app.application.service;

import hhplus.concertreservation.app.domain.checker.SeatChecker;
import hhplus.concertreservation.app.domain.constant.SeatStatus;
import hhplus.concertreservation.app.domain.entity.Reservation;
import hhplus.concertreservation.app.domain.entity.Seat;
import hhplus.concertreservation.app.domain.repository.ReservationRepository;
import hhplus.concertreservation.app.domain.repository.SeatRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final SeatChecker seatChecker;

    @Transactional
    public Reservation reserveSeat(Long userId, Long concertId, Long seatNumber) {
        Seat seat = seatChecker.getOrThrowIfNotFound(seatRepository.findByConcertIdAndSeatNumber(concertId, seatNumber));
        // AVAILABLE이 아니라면 예외
        seatChecker.validateIfAvailable(seat);
        log.info("좌석 예약 요청 유저의 ID : {}", userId);
        // 좌석 예약 상태로 변환
        seat.changeStatus(SeatStatus.RESERVED);
        // 예약 생성
        return reservationRepository.save(Reservation.create(userId, seat.getId()));
    }
}
