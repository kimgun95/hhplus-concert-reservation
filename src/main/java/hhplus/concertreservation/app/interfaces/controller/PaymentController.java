package hhplus.concertreservation.app.interfaces.controller;

import hhplus.concertreservation.app.application.service.PaymentService;
import hhplus.concertreservation.app.interfaces.request.ChargePointRequest;
import hhplus.concertreservation.app.interfaces.request.UsePointRequest;
import hhplus.concertreservation.app.interfaces.response.UserPointResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/payment/point/charge")
    public ResponseEntity<String> chargePoint(
            @RequestHeader String token,
            @RequestBody ChargePointRequest request
            ) {
        paymentService.chargeUserPoint(token, request.amount());

        return ResponseEntity.status(HttpStatus.OK)
                .body("성공적으로 충전되었습니다.");
    }

    @GetMapping("/payment/point")
    public ResponseEntity<UserPointResponse> queryPoint(
            @RequestHeader String token
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(UserPointResponse.from(paymentService.getUserPoint(token)));
    }

    @PostMapping("/payment/point/use")
    public ResponseEntity<String> usePoint(
            @RequestHeader String token,
            @RequestBody UsePointRequest request
            ) {
        paymentService.useUserPoint(token, request.reservationId(), request.amount());

        return ResponseEntity.status(HttpStatus.OK)
                .body("성공적으로 결제되었습니다.");
    }
}
