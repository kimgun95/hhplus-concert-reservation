package hhplus.concertreservation.config.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    EXPIRED_QUEUE_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰. 대기열 토큰을 반급받아 주세요."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."),
    SEAT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 좌석입니다."),
    RESERVED_SEAT(HttpStatus.CONFLICT, "이미 예약된 좌석입니다."),
    USER_POINT_NOT_ENOUGH(HttpStatus.CONFLICT, "사용하려는 포인트가 부족합니다."),
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 예약입니다."),
    PAYMENT_FAILED(HttpStatus.CONFLICT, "결제가 실패했습니다."),
    INVALID_QUEUE_STATUS(HttpStatus.UNAUTHORIZED, "API 요청이 가능한 대기열 상태가 아닙니다."),
    CONFLICT_TOKEN_ERROR(HttpStatus.CONFLICT, "토큰 에러가 발생했습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
