package hhplus.concertreservation.app.application.service;

import hhplus.concertreservation.app.domain.entity.Users;
import hhplus.concertreservation.app.domain.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UsersService {

    private final UsersRepository usersRepository;

    public Users getUserByUserId(Long userId) {
        return usersRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("존재하지 않는 유저입니다")
        );
    }
}
