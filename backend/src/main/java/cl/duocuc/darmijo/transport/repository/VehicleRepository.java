package cl.duocuc.darmijo.transport.repository;

import cl.duocuc.darmijo.transport.models.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByUlid(String ulid);
    Optional<Vehicle> findByPlate(String plate);
}
