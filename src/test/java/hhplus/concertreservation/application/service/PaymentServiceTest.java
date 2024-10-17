package hhplus.concertreservation.application.service;

import hhplus.concertreservation.domain.constant.PaymentStatus;
import hhplus.concertreservation.domain.constant.ReservationStatus;
import hhplus.concertreservation.domain.constant.SeatStatus;
import hhplus.concertreservation.domain.entity.*;
import hhplus.concertreservation.domain.repository.LedgerRepository;
import hhplus.concertreservation.domain.repository.PaymentRepository;
import hhplus.concertreservation.domain.repository.ReservationRepository;
import hhplus.concertreservation.domain.repository.SeatRepository;
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

class PaymentServiceTest {
    @InjectMocks
    private PaymentService sut;

    @Mock private PaymentRepository paymentRepository;
    @Mock private LedgerRepository ledgerRepository;
    @Mock private ReservationRepository reservationRepository;
    @Mock private SeatRepository seatRepository;
    @Mock private QueueService queueService;
    @Mock private UsersService usersService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 포인트충전성공시_원장생성() {
        String token = "some-token";
        Long amount = 100L;
        Long userId = 1L;

        Queue queue = Queue.create(userId);
        Users user = Users.builder()
                .id(userId)
                .userName("Hong")
                .userPoint(0L)
                .build();
        when(queueService.getQueueByToken(token)).thenReturn(queue);
        when(usersService.getUserByUserId(userId)).thenReturn(user);
        when(ledgerRepository.save(any(Ledger.class))).thenAnswer(invocation -> invocation.getArgument(0));

        sut.chargeUserPoint(token, amount);

        assertEquals(amount, user.getUserPoint());
        verify(ledgerRepository).save(any(Ledger.class));
    }

    @Test
    void 유저포인트조회하기() {
        String token = "some-token";
        Long userId = 1L;

        Queue queue = Queue.create(userId);
        Users user = Users.builder()
                .id(userId)
                .userName("Hong")
                .userPoint(50L)
                .build();
        when(queueService.getQueueByToken(token)).thenReturn(queue);
        when(usersService.getUserByUserId(userId)).thenReturn(user);

        Users result = sut.getUserPoint(token);

        assertEquals(50L, result.getUserPoint());
    }

    @Test
    void 결제성공시_결제내역생성() {
        String token = "some-token";
        Long reservationId = 1L;
        Long amount = 50L;
        Long userId = 1L;
        Long seatId = 1L;

        Queue queue = Queue.create(userId);
        Users user = Users.builder()
                .id(userId)
                .userName("Hong")
                .userPoint(100L)
                .build();
        Reservation reservation = Reservation.create(userId, seatId);
        reservation.changeStatus(ReservationStatus.PENDING);

        when(queueService.getQueueByToken(token)).thenReturn(queue);
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(usersService.getUserByUserId(userId)).thenReturn(user);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(ledgerRepository.save(any(Ledger.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payment result = sut.useUserPoint(token, reservationId, amount);

        assertNotNull(result);
        assertEquals(amount, result.getAmount());
        assertEquals(PaymentStatus.SUCCESS, result.getPaymentStatus());
        assertEquals(50L, user.getUserPoint());
        assertEquals(ReservationStatus.SUCCESS, reservation.getReservationStatus());
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void 결제실패시_좌석및예약상태변경() {
        String token = "some-token";
        Long reservationId = 1L;
        Long amount = 50L;
        Long userId = 1L;
        Long seatId = 1L;

        Reservation reservation = Reservation.create(userId, seatId);
        reservation.changeStatus(ReservationStatus.PENDING);
        Seat seat = new Seat();
        seat.changeStatus(SeatStatus.RESERVED);


        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(seatRepository.findById(reservation.getSeatId())).thenReturn(Optional.of(seat));
        when(queueService.getQueueByToken(token)).thenThrow(new RuntimeException("대기열이 만료되었습니다"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            sut.useUserPoint(token, reservationId, amount);
        });

        assertEquals("결제 중 오류가 발생했습니다", exception.getMessage());
        assertEquals(ReservationStatus.FAILED, reservation.getReservationStatus());
    }
}