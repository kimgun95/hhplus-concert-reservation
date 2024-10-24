package hhplus.concertreservation.config.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
class RestApiControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(FailException.class)
    public ResponseEntity<ErrorRes> handleFailException(FailException e) {
        switch (e.getLogLevel()) {
            case ERROR -> log.error("치명적인 에러입니다. 빠르게 확인 바랍니다. : {} - ErrorCode: {}", e.getMessage(), e.getErrorCode().name(), e);
            case WARN -> log.warn("잠재적 에러입니다. 주의 깊게 확인 바랍니다. : {} - ErrorCode: {}", e.getMessage(), e.getErrorCode().name(), e);
            default -> log.info("클라이언트 에러입니다. : {} - ErrorCode: {}", e.getMessage(), e.getErrorCode().name(), e);
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ErrorRes(e.getErrorCode().name(), e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorRes> handleException(Exception e) {
        log.error("UnhandledException : {}", e.getMessage(), e);
        return ResponseEntity.status(500)
                .body(new ErrorRes("500", "서버 에러가 발생했습니다."));
    }
}
