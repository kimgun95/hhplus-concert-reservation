package hhplus.concertreservation.application.service;

import hhplus.concertreservation.app.application.service.UserService;
import hhplus.concertreservation.app.domain.entity.Ledger;
import hhplus.concertreservation.app.domain.entity.Queue;
import hhplus.concertreservation.app.domain.entity.User;
import hhplus.concertreservation.app.domain.repository.LedgerRepository;
import hhplus.concertreservation.app.domain.repository.QueueRepository;
import hhplus.concertreservation.app.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private QueueRepository queueRepository;
    @Mock private LedgerRepository ledgerRepository;

    @InjectMocks
    private UserService sut;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 포인트충전성공시_원장생성() {
        String token = "some-token";
        Long amount = 100L;
        Long userId = 1L;

        Queue queue = Queue.create(userId);
        User user = User.builder()
                .id(userId)
                .userName("Hong")
                .userPoint(0L)
                .build();
        when(queueRepository.findByToken(token)).thenReturn(Optional.of(queue));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(ledgerRepository.save(any(Ledger.class))).thenAnswer(invocation -> invocation.getArgument(0));

        sut.chargeUserPoint(userId, amount);

        assertEquals(amount, user.getUserPoint());
        verify(ledgerRepository).save(any(Ledger.class));
    }

    @Test
    void 유저포인트조회하기() {
        String token = "some-token";
        Long userId = 1L;

        Queue queue = Queue.create(userId);
        User user = User.builder()
                .id(userId)
                .userName("Hong")
                .userPoint(50L)
                .build();
        when(queueRepository.findByToken(token)).thenReturn(Optional.of(queue));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = sut.getUserPoint(userId);

        assertEquals(50L, result.getUserPoint());
    }
}