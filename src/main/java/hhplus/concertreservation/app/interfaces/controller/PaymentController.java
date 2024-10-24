package hhplus.concertreservation.app.interfaces.controller;

import hhplus.concertreservation.app.application.service.PaymentService;
import hhplus.concertreservation.app.interfaces.request.UsePointRequest;
import hhplus.concertreservation.app.interfaces.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/payment")
    public ResponseEntity<PaymentResponse> payment(
            @RequestBody UsePointRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(PaymentResponse.from(paymentService.payment(request.userId(), request.reservationId(), request.amount())));
    }
}
