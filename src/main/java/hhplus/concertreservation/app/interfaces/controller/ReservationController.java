package hhplus.concertreservation.app.interfaces.controller;

import hhplus.concertreservation.app.application.service.ReservationService;
import hhplus.concertreservation.app.interfaces.request.ReservationRequest;
import hhplus.concertreservation.app.interfaces.response.ReservationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/reservation")
    public ResponseEntity<ReservationResponse> reservation(
            @RequestBody ReservationRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ReservationResponse.from(
                        reservationService.reserveSeat(request.userId(), request.concertId(), request.seatNumber())));
    }
}
