package hhplus.concertreservation.application.service;

import hhplus.concertreservation.app.application.service.PaymentService;
import hhplus.concertreservation.app.domain.constant.PaymentStatus;
import hhplus.concertreservation.app.domain.constant.ReservationStatus;
import hhplus.concertreservation.app.domain.constant.SeatStatus;
import hhplus.concertreservation.app.domain.entity.*;
import hhplus.concertreservation.app.domain.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PaymentServiceTest {
    @InjectMocks
    private PaymentService sut;

    @Mock private PaymentRepository paymentRepository;
    @Mock private LedgerRepository ledgerRepository;
    @Mock private ReservationRepository reservationRepository;
    @Mock private SeatRepository seatRepository;
    @Mock private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 결제성공시_결제내역생성() {
        Long reservationId = 1L;
        Long amount = 50L;
        Long userId = 1L;
        Long seatId = 1L;

        QueueToken queueToken = QueueToken.create(userId);
        User user = User.builder()
                .id(userId)
                .userName("Hong")
                .userPoint(100L)
                .build();
        Reservation reservation = Reservation.create(userId, seatId);
        reservation.changeStatus(ReservationStatus.PENDING);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(ledgerRepository.save(any(Ledger.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payment result = sut.payment(userId, reservationId, amount);

        assertNotNull(result);
        assertEquals(amount, result.getAmount());
        assertEquals(PaymentStatus.SUCCESS, result.getPaymentStatus());
        assertEquals(50L, user.getUserPoint());
        assertEquals(ReservationStatus.SUCCESS, reservation.getReservationStatus());
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void 결제실패시_좌석및예약상태변경() {
        Long reservationId = 1L;
        Long amount = 50L;
        Long userId = 1L;
        Long seatId = 1L;

        User user = User.builder()
                .id(userId)
                .userName("Hong")
                .userPoint(100L)
                .build();
        Reservation reservation = Reservation.create(userId, seatId);
        Seat seat = new Seat();
        seat.changeStatus(SeatStatus.RESERVED);


        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(seatRepository.findById(seatId)).thenReturn(Optional.of(seat));

        sut.payment(userId, reservationId, amount);

        assertEquals(ReservationStatus.FAILED, reservation.getReservationStatus());
    }
}