package hhplus.concertreservation.config.exception;

import lombok.Getter;

@Getter
public class FailException extends RuntimeException {

    private final ErrorCode errorCode;
    private final LogLevel logLevel;

    public FailException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.logLevel = LogLevel.INFO;
    }

    public FailException(ErrorCode errorCode, LogLevel logLevel) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.logLevel = logLevel;
    }

    @Override
    public String toString() {
        return String.format("ErrorCode: %s, Message: %s", errorCode.getCode(), errorCode.getMessage());
    }

    public enum LogLevel {
        DEBUG, INFO, WARN, ERROR
    }
}