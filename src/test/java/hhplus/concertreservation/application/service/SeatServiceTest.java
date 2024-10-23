package hhplus.concertreservation.application.service;

import hhplus.concertreservation.app.application.service.SeatService;
import hhplus.concertreservation.app.domain.constant.SeatStatus;
import hhplus.concertreservation.app.domain.entity.Seat;
import hhplus.concertreservation.app.domain.repository.SeatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SeatServiceTest {

    @Mock
    private SeatRepository seatRepository;

    @InjectMocks
    private SeatService sut;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 상태가AVAILABLE인좌석들조회하기() {
        Long concertId = 1L;
        Seat seat1 = new Seat();
        seat1.changeStatus(SeatStatus.AVAILABLE);
        Seat seat2 = new Seat();
        seat2.changeStatus(SeatStatus.AVAILABLE);

        when(seatRepository.findByConcertIdAndSeatStatus(concertId, SeatStatus.AVAILABLE))
                .thenReturn(Arrays.asList(seat1, seat2));

        List<Seat> result = sut.getSeats(concertId);

        assertEquals(2, result.size());
        verify(seatRepository).findByConcertIdAndSeatStatus(concertId, SeatStatus.AVAILABLE);
    }

    @Test
    void 좌석조회_예외발생() {
        Long concertId = 1L;
        Long seatNumber = 10L;

        when(seatRepository.findByConcertIdAndSeatNumber(concertId, seatNumber))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            sut.getSeat(concertId, seatNumber);
        });
    }

    @Test
    void 좌석조회_성공() {
        Long concertId = 1L;
        Long seatNumber = 1L;
        Seat seat = new Seat();
        seat.changeStatus(SeatStatus.AVAILABLE);

        when(seatRepository.findByConcertIdAndSeatNumber(concertId, seatNumber))
                .thenReturn(Optional.of(seat));

        Seat result = sut.getSeat(concertId, seatNumber);

        assertNotNull(result);
        assertEquals(seat.getSeatStatus(), result.getSeatStatus());
    }
}