package hhplus.concertreservation.app.interfaces.controller;

import hhplus.concertreservation.app.application.service.PaymentService;
import hhplus.concertreservation.app.interfaces.request.UsePointRequest;
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
    public ResponseEntity<String> payment(
            @RequestBody UsePointRequest request
    ) {
        paymentService.useUserPoint(request.userId(), request.reservationId(), request.amount());

        return ResponseEntity.status(HttpStatus.OK)
                .body("성공적으로 결제되었습니다.");
    }
}
