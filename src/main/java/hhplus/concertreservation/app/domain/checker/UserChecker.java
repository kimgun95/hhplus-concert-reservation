package hhplus.concertreservation.app.domain.checker;

import hhplus.concertreservation.app.domain.entity.User;
import hhplus.concertreservation.config.exception.ErrorCode;
import hhplus.concertreservation.config.exception.FailException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserChecker {
    public User getOrThrowIfNotFound(Optional<User> optionalUsers) {
        return optionalUsers.orElseThrow(
                () -> new FailException(ErrorCode.USER_NOT_FOUND, FailException.LogLevel.ERROR));
    }
}
