package hhplus.concertreservation.app.domain.entity;

import hhplus.concertreservation.config.exception.ErrorCode;
import hhplus.concertreservation.config.exception.FailException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Builder
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;

    private String userName;
    private Long userPoint;

    public void usePoints(Long point) {
        if (this.userPoint < point) throw new FailException(ErrorCode.USER_POINT_NOT_ENOUGH);
        this.userPoint -= point;
    }

    public void chargePoints(Long point) {
        this.userPoint += point;
    }

    public static Users getOrThrowIfNotFound(Optional<Users> optionalUsers) {
        return optionalUsers.orElseThrow(
                () -> new FailException(ErrorCode.USER_NOT_FOUND, FailException.LogLevel.ERROR));
    }
}
