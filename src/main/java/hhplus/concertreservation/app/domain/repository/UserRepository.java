package hhplus.concertreservation.app.domain.repository;

import hhplus.concertreservation.app.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
