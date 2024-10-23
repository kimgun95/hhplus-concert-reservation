package hhplus.concertreservation.application.service;

import hhplus.concertreservation.app.application.service.ReservationService;
import hhplus.concertreservation.app.domain.constant.ReservationStatus;
import hhplus.concertreservation.app.domain.constant.SeatStatus;
import hhplus.concertreservation.app.domain.entity.Queue;
import hhplus.concertreservation.app.domain.entity.Reservation;
import hhplus.concertreservation.app.domain.entity.Seat;
import hhplus.concertreservation.app.domain.repository.QueueRepository;
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

    @Mock
    private QueueRepository queueRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 좌석예약_성공() {
        String token = "some-token";
        Long concertId = 1L;
        Long seatNumber = 1L;
        Long userId = 1L;

        Queue queue = Queue.create(userId);
        Seat seat = Seat.builder()
                .id(1L)
                .concertId(concertId)
                .seatNumber(seatNumber)
                .seatStatus(SeatStatus.AVAILABLE)
                .build();

        when(seatRepository.findByConcertIdAndSeatNumber(concertId, seatNumber)).thenReturn(Optional.of(seat));
        when(queueRepository.findByToken(token)).thenReturn(Optional.of(queue));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Reservation result = sut.reserveSeat(token, concertId, seatNumber);

        assertNotNull(result);
        assertEquals(ReservationStatus.PENDING, result.getReservationStatus());
        assertEquals(SeatStatus.RESERVED, seat.getSeatStatus());
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    void 좌석이예매된상태라면_예외발생() {
        String token = "some-token";
        Long concertId = 1L;
        Long seatNumber = 1L;

        Seat seat = new Seat();
        seat.changeStatus(SeatStatus.RESERVED);

        when(seatRepository.findByConcertIdAndSeatNumber(concertId, seatNumber)).thenReturn(Optional.of(seat));

        assertThrows(RuntimeException.class, () -> {
            sut.reserveSeat(token, concertId, seatNumber);
        });
    }

    @Test
    void 대기열이만료되었다면_좌석예약실패() {
        String token = "invalid-token";
        Long concertId = 1L;
        Long seatNumber = 1L;

        Seat seat = new Seat();

        when(seatRepository.findByConcertIdAndSeatNumber(concertId, seatNumber)).thenReturn(Optional.of(seat));
        when(queueRepository.findByToken(token)).thenThrow(new RuntimeException("대기열을 찾을 수 없습니다"));

        assertThrows(RuntimeException.class, () -> {
            sut.reserveSeat(token, concertId, seatNumber);
        });
    }

}