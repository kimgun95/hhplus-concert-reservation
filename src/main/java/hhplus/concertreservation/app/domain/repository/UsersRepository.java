package hhplus.concertreservation.app.domain.repository;

import hhplus.concertreservation.app.domain.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Long> {

}
