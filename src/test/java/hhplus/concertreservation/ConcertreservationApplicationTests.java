package hhplus.concertreservation;

import hhplus.concertreservation.app.application.service.*;
import hhplus.concertreservation.app.domain.constant.SeatStatus;
import hhplus.concertreservation.app.domain.entity.Concert;
import hhplus.concertreservation.app.domain.entity.Reservation;
import hhplus.concertreservation.app.domain.entity.Seat;
import hhplus.concertreservation.app.domain.repository.SeatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ConcertreservationApplicationTests {

	@Autowired private ConcertService concertService;
	@Autowired private QueueService queueService;
	@Autowired private SeatService seatService;
	@Autowired private ReservationService reservationService;
	@Autowired private UsersService usersService;
	@Autowired private PaymentService paymentService;
	@Autowired private SeatRepository seatRepository;

	private Long concertId;
	private Long seatNumber;
	private Long seatId;

	@BeforeEach
	public void setUp() {
		List<Concert> concerts = concertService.getConcerts("LOA ON WINTER");
		concertId = concerts.get(0).getId();
		List<Seat> availableSeats = seatService.getSeats(concertId);
		seatNumber = availableSeats.get(0).getSeatNumber();
		seatId = availableSeats.get(0).getId();
		// 유저 포인트 충전
		for (int i = 1; i <= 5; i++) {
			usersService.chargeUserPoint((long) i, 10000L);
		}
	}

	@Test
	public void testSeatReservationConcurrency() throws InterruptedException {
		// 5명의 유저가 동시에 같은 좌석을 예약 시도하는 상황
		ExecutorService executorService = Executors.newFixedThreadPool(5);
		AtomicInteger successCnt = new AtomicInteger(0);
		AtomicInteger failCnt = new AtomicInteger(0);

		for (int i = 1; i <= 5; i++) {
			Long userId = (long) i;
			executorService.submit(() -> {
				try {
					// 유저 대기열 토큰 발급
					queueService.getQueue(userId);
					// 좌석 예약 시도
					Reservation reservation = reservationService.reserveSeat(userId, concertId, seatNumber);
					// 결제 시도
					paymentService.payment(userId, reservation.getId(), 5000L);

					successCnt.getAndIncrement();
					System.out.println("User " + userId + " 예약 성공");
				} catch (Exception e) {
					failCnt.getAndIncrement();
					System.out.println("User " + userId + " 예약 실패: " + e.getMessage());
				}
			});
		}

		// 모든 스레드 완료 대기
		executorService.shutdown();
		executorService.awaitTermination(10, TimeUnit.SECONDS);

		Seat seat = seatRepository.findById(seatId).get();  // 좌석 조회
		assertEquals(SeatStatus.RESERVED, seat.getSeatStatus(), "최종적으로 좌석은 예약 상태여야 합니다.");

		assertEquals(1, successCnt.get(), "예약 성공은 한 명이어야 합니다.");
		assertEquals(4, failCnt.get(), "나머지 4명은 예약 실패해야 합니다.");
	}
}
