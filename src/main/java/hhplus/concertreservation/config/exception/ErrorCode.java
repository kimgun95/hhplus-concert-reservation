package hhplus.concertreservation.config.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    EXPIRED_QUEUE_TOKEN("E100", "만료된 토큰. 대기열 토큰을 반급받아 주세요."),
    USER_NOT_FOUND("E101", "존재하지 않는 유저입니다."),
    SEAT_NOT_FOUND("E102", "존재하지 않는 좌석입니다."),
    RESERVED_SEAT("E103", "이미 예약된 좌석입니다."),
    USER_POINT_NOT_ENOUGH("E104", "사용하려는 포인트가 부족합니다."),
    RESERVATION_NOT_FOUND("E105", "존재하지 않는 예약입니다."),
    PAYMENT_FAILED("E106", "결제가 실패했습니다."),
    INVALID_QUEUE_STATUS("E107", "API 요청이 가능한 대기열 상태가이 아닙니다"),
    ;

    private final String code;
    private final String message;
}
