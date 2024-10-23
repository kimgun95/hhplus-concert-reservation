package hhplus.concertreservation;

import hhplus.concertreservation.app.application.service.*;
import hhplus.concertreservation.app.domain.constant.PaymentStatus;
import hhplus.concertreservation.app.domain.constant.ReservationStatus;
import hhplus.concertreservation.app.domain.dto.QueryQueueDto;
import hhplus.concertreservation.app.domain.entity.*;
import hhplus.concertreservation.app.domain.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ConcertreservationApplicationTests {

	@Autowired private QueueService queueService;
	@Autowired private ConcertService concertService;
	@Autowired private SeatService seatService;
	@Autowired private ReservationService reservationService;
	@Autowired private PaymentService paymentService;
	@Autowired private UsersService usersService;

	private List<Long> userIds;
	private List<String> tokens;
	private Long concertId;
	private Long seatId;
    @Autowired
    private ReservationRepository reservationRepository;

	@BeforeEach
	void setUp() {
		userIds = new ArrayList<>();
		tokens = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			Long userId = (long) (i + 1);
			userIds.add(userId);
			Queue queue = queueService.getQueue(userId);
			tokens.add(queue.getToken());
		}

		List<Concert> concerts = concertService.getConcerts("LOA ON WINTER");
		concertId = concerts.get(0).getId();
		List<Seat> seats = seatService.getSeats(concertId);
		seatId = seats.get(0).getId();
	}

	@Test
	void testConcertReservationProcess() throws Exception {
		// 1. 대기열 상태 조회
		for (String token : tokens) {
			QueryQueueDto queryQueueDto = queueService.queryQueue(token);
		}

		// 2. 콘서트 날짜 조회
		List<Concert> concerts = concertService.getConcerts("LOA ON WINTER");
		assertFalse(concerts.isEmpty());

		// 3. 좌석 조회
		List<Seat> availableSeats = seatService.getSeats(concertId);
		assertFalse(availableSeats.isEmpty());

		// 4. 5명이 동시에 같은 좌석 예약 시도
		ExecutorService executorService = Executors.newFixedThreadPool(5);
		List<CompletableFuture<Reservation>> reservationFutures = new ArrayList<>();

		for (String token : tokens) {
			CompletableFuture<Reservation> future = CompletableFuture.supplyAsync(() -> {
				try {
					return reservationService.reserveSeat(token, concertId, seatId);
				} catch (RuntimeException e) {
					return null;
				}
			}, executorService);
			reservationFutures.add(future);
		}

		CompletableFuture.allOf(reservationFutures.toArray(new CompletableFuture[0])).join();

		// 5. 결과 확인
		List<Reservation> reservations = reservationFutures.stream()
				.map(CompletableFuture::join)
				.filter(r -> r != null)
				.toList();

		assertEquals(1, reservations.size());
		Reservation successfulReservation = reservations.get(0);

		// 6. 성공한 예약에 대해 결제 진행
		Long userId = successfulReservation.getUserId();
		String userToken = tokens.get(userIds.indexOf(userId));

		// 포인트 충전
		paymentService.chargeUserPoint(userToken, 100000L);

		// 결제
		Payment payment = paymentService.useUserPoint(userToken, successfulReservation.getId(), 50000L);

		// 7. 최종 상태 확인
		assertEquals(PaymentStatus.SUCCESS, payment.getPaymentStatus());

		Reservation finalReservation = reservationRepository.findById(successfulReservation.getId()).orElse(null);
		assertEquals(ReservationStatus.SUCCESS, finalReservation.getReservationStatus());

		Users user = usersService.getUserByUserId(userId);
		assertEquals(50000L, user.getUserPoint());
	}
}
