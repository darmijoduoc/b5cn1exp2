package cl.duocuc.darmijo.consumerlocation.repository;

import cl.duocuc.darmijo.consumerlocation.models.VehicleLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VehicleLocationRepository extends JpaRepository<VehicleLocation, Long> {
    
    List<VehicleLocation> findByVehicleIdOrderByTimestampDesc(String vehicleId);
    
    List<VehicleLocation> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}
