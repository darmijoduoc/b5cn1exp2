package cl.duocuc.darmijo.users.repository;

import cl.duocuc.darmijo.users.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUlid(String uid);
    
    Optional<User> findByEmail(String email);
}
