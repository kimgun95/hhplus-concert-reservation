package hhplus.concertreservation.interfaces;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
public class ConcertController {

    @GetMapping("/concert")
    public List<Map<String, Object>> getConcerts() {
        List<Map<String, Object>> concerts = new ArrayList<>();

        Map<String, Object> concert1 = new HashMap<>();
        concert1.put("id", 1);
        concert1.put("name", "volunteers");
        concert1.put("date", "2024-07-15");

        Map<String, Object> concert2 = new HashMap<>();
        concert2.put("id", 2);
        concert2.put("name", "psy");
        concert2.put("date", "2024-10-20");

        concerts.add(concert1);
        concerts.add(concert2);

        return concerts;
    }

    @PostMapping("/queue")
    public Map<String, Object> getToken(@RequestParam Long userId) {
        Map<String, Object> userQueueToken = new HashMap<>();
        userQueueToken.put("user_queue_id", 1L);
        userQueueToken.put("user_id", userId);
        userQueueToken.put("token", UUID.randomUUID().toString());
        userQueueToken.put("user_queue_status", "READY");
        userQueueToken.put("expired_at", null);

        return userQueueToken;
    }

    @GetMapping("/seats")
    public List<Map<String, Object>> getSeats(@RequestParam Long userId, @RequestParam Long concertId) {
        // userId는 대기열 조회 때 사용된다.

        List<Map<String, Object>> seats = new ArrayList<>();

        for (int i = 1; i <= 3; i++) {
            Map<String, Object> seat = new HashMap<>();
            seat.put("concert_seat_id", i);
            seat.put("concert_id", concertId);
            seat.put("seat_number", i);
            seat.put("seat_status", "AVAILABLE");
            seats.add(seat);
        }

        return seats;
    }


    @PostMapping("/reservation")
    public Map<String, Object> reserveSeat(@RequestParam Long userId, @RequestParam Long seatNumber) {
        // 좌석이 비어있다면 해당 좌석의 상태를 변환할 것이다.

        Map<String, Object> reservation = new HashMap<>();
        reservation.put("reservation_id", 1L);
        reservation.put("user_id", userId);
        reservation.put("concert_seat_id", seatNumber);
        reservation.put("reservation_status", "RESERVED");

        return reservation;
    }

    @PostMapping("/point/charge")
    public Map<String, Object> chargePoint(@RequestParam Long userId, @RequestParam Long amount) {
        Map<String, Object> response = new HashMap<>();
        response.put("user_id", userId);
        response.put("user_point", amount);

        return response;
    }

    @GetMapping("/point")
    public Map<String, Object> getPoint(@RequestParam Long userId) {
        Map<String, Object> response = new HashMap<>();
        response.put("user_id", userId);
        response.put("user_point", 10000L);

        return response;
    }

    @PostMapping("/payment")
    public Map<String, Object> processPayment(@RequestParam Long userId, @RequestParam Long reservationId, @RequestParam Long amount) {
        Map<String, Object> payment = new HashMap<>();
        payment.put("payment_id", 1L);
        payment.put("user_id", userId);
        payment.put("reservation_id", reservationId);
        payment.put("payment_status", "SUCCESS");
        payment.put("amount", amount);

        return payment;
    }
}
