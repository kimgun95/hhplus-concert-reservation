package hhplus.concertreservation.domain.repository;

import hhplus.concertreservation.domain.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Long> {

}
