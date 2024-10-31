package hhplus.concertreservation.app.interfaces.controller;

import hhplus.concertreservation.app.application.service.UserService;
import hhplus.concertreservation.app.interfaces.request.ChargePointRequest;
import hhplus.concertreservation.app.interfaces.request.QueryPointRequest;
import hhplus.concertreservation.app.interfaces.response.UserPointResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UsersController {

    private final UserService userService;

    @PostMapping("/user/point/charge")
    public ResponseEntity<String> chargePoint(
            @RequestBody ChargePointRequest request
    ) {
        userService.chargeUserPoint(request.userId(), request.amount());

        return ResponseEntity.status(HttpStatus.OK)
                .body("성공적으로 충전되었습니다.");
    }

    @GetMapping("/user/point")
    public ResponseEntity<UserPointResponse> queryPoint(
            @RequestBody QueryPointRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(UserPointResponse.from(userService.getUserPoint(request.userId())));
    }
}
