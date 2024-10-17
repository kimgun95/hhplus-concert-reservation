package hhplus.concertreservation.application.service;

import hhplus.concertreservation.domain.constant.SeatStatus;
import hhplus.concertreservation.domain.entity.Seat;
import hhplus.concertreservation.domain.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SeatService {

    private final SeatRepository seatRepository;

    public List<Seat> getSeats(Long concertId) {
        return seatRepository.findByConcertIdAndSeatStatus(concertId, SeatStatus.AVAILABLE);
    }

    public Seat getSeat(Long concertId, Long seatNumber) {
        return seatRepository.findByConcertIdAndSeatNumber(concertId, seatNumber).orElseThrow(
                () -> new RuntimeException("존재하지 않는 좌석입니다")
        );
    }
}
