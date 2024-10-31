package hhplus.concertreservation.app.application.service;

import hhplus.concertreservation.app.domain.checker.UserChecker;
import hhplus.concertreservation.app.domain.constant.TransactionType;
import hhplus.concertreservation.app.domain.entity.Ledger;
import hhplus.concertreservation.app.domain.entity.Users;
import hhplus.concertreservation.app.domain.repository.LedgerRepository;
import hhplus.concertreservation.app.domain.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UsersService {

    private final UsersRepository usersRepository;
    private final LedgerRepository ledgerRepository;
    private final UserChecker userChecker;

    @Transactional
    public void chargeUserPoint(Long userId, Long amount) {
        log.info("포인트 충전 요청 유저의 ID : {}", userId);
        User user = userChecker.getOrThrowIfNotFound(userRepository.findById(userId));
        user.chargePoints(amount);
        ledgerRepository.save(Ledger.create(userId, amount, TransactionType.CHARGE));
    }

    public Users getUserPoint(Long userId) {
        log.info("포인트 조회 요청 유저의 ID : {}", userId);
        return userChecker.getOrThrowIfNotFound(userRepository.findById(userId));
    }
}
