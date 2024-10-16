package hhplus.concertreservation.interfaces.controller;

import hhplus.concertreservation.application.service.SeatService;
import hhplus.concertreservation.interfaces.response.ConcertSeatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class SeatController {

    private final SeatService seatService;

    @GetMapping("/seat/{concert-id}")
    public ResponseEntity<List<ConcertSeatResponse>> concertSeat(
            @RequestHeader String token,
            @PathVariable("concert-id") Long concertId
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(seatService.getSeats(concertId)
                        .stream().map(ConcertSeatResponse::from)
                        .toList());
    }
}
