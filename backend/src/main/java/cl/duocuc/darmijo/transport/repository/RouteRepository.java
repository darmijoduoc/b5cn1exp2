package cl.duocuc.darmijo.transport.repository;

import cl.duocuc.darmijo.transport.models.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    Optional<Route> findByUlid(String ulid);
    Optional<Route> findByCode(String code);
}
