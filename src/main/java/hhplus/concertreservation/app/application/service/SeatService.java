package hhplus.concertreservation.app.application.service;

import hhplus.concertreservation.app.domain.constant.SeatStatus;
import hhplus.concertreservation.app.domain.entity.Seat;
import hhplus.concertreservation.app.domain.repository.SeatRepository;
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
}
