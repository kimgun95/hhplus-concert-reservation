package hhplus.concertreservation.app.application.service;

import hhplus.concertreservation.app.domain.constant.TransactionType;
import hhplus.concertreservation.app.domain.entity.Ledger;
import hhplus.concertreservation.app.domain.entity.Users;
import hhplus.concertreservation.app.domain.repository.LedgerRepository;
import hhplus.concertreservation.app.domain.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UsersService {

    private final UsersRepository usersRepository;
    private final LedgerRepository ledgerRepository;

    @Transactional
    public void chargeUserPoint(Long userId, Long amount) {
        // 유저의 포인트 충전
        Users user = Users.getOrThrowIfNotFound(usersRepository.findById(userId));
        user.chargePoints(amount);
        // 원장 생성
        ledgerRepository.save(Ledger.create(userId, amount, TransactionType.CHARGE));
    }

    public Users getUserPoint(Long userId) {
        return Users.getOrThrowIfNotFound(usersRepository.findById(userId));
    }
}
