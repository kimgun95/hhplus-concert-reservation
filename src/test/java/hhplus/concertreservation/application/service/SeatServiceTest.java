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

import static org.junit.jupiter.api.Assertions.assertEquals;
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
}