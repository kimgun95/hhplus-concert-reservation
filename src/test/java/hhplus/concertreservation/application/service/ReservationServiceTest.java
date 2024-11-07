package hhplus.concertreservation.application.service;

import hhplus.concertreservation.app.application.service.ReservationService;
import hhplus.concertreservation.app.domain.constant.ReservationStatus;
import hhplus.concertreservation.app.domain.constant.SeatStatus;
import hhplus.concertreservation.app.domain.entity.QueueToken;
import hhplus.concertreservation.app.domain.entity.Reservation;
import hhplus.concertreservation.app.domain.entity.Seat;
import hhplus.concertreservation.app.domain.repository.ReservationRepository;
import hhplus.concertreservation.app.domain.repository.SeatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReservationServiceTest {

    @InjectMocks
    private ReservationService sut;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private SeatRepository seatRepository;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 좌석예약_성공() {
        Long concertId = 1L;
        Long seatNumber = 1L;
        Long userId = 1L;

        QueueToken queueToken = QueueToken.create(userId);
        Seat seat = Seat.builder()
                .id(1L)
                .concertId(concertId)
                .seatNumber(seatNumber)
                .seatStatus(SeatStatus.AVAILABLE)
                .build();

        when(seatRepository.findByConcertIdAndSeatNumber(concertId, seatNumber)).thenReturn(Optional.of(seat));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Reservation result = sut.reserveSeat(userId, concertId, seatNumber);

        assertNotNull(result);
        assertEquals(ReservationStatus.PENDING, result.getReservationStatus());
        assertEquals(SeatStatus.RESERVED, seat.getSeatStatus());
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    void 좌석이예매된상태라면_예외발생() {
        Long concertId = 1L;
        Long seatNumber = 1L;
        Long userId = 1L;

        Seat seat = new Seat();
        seat.changeStatus(SeatStatus.RESERVED);

        when(seatRepository.findByConcertIdAndSeatNumber(concertId, seatNumber)).thenReturn(Optional.of(seat));

        assertThrows(RuntimeException.class, () -> {
            sut.reserveSeat(userId, concertId, seatNumber);
        });
    }

    @Test
    void 대기열이만료되었다면_좌석예약실패() {
        Long concertId = 1L;
        Long seatNumber = 1L;
        Long userId = 1L;

        Seat seat = new Seat();

        when(seatRepository.findByConcertIdAndSeatNumber(concertId, seatNumber)).thenReturn(Optional.of(seat));

        assertThrows(RuntimeException.class, () -> {
            sut.reserveSeat(userId, concertId, seatNumber);
        });
    }

}