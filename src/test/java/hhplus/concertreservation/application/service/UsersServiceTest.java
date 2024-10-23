package hhplus.concertreservation.application.service;

import hhplus.concertreservation.app.application.service.UsersService;
import hhplus.concertreservation.app.domain.entity.Users;
import hhplus.concertreservation.app.domain.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class UsersServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private UsersService sut;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 존재하지않는유저를조회시_예외발생() {
        Long userId = 1L;
        when(usersRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            sut.getUserByUserId(userId);
        });
    }

    @Test
    void 존재하는유저를조회시_성공() {
        Long userId = 1L;
        Users user = new Users();
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));

        Users result = sut.getUserByUserId(userId);

        assertNotNull(result);
    }

}