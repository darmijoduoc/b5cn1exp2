package cl.duocuc.darmijo.consumerlocation.service;

import cl.duocuc.darmijo.consumerlocation.models.VehicleLocation;
import cl.duocuc.darmijo.consumerlocation.models.VehicleLocationDTO;
import cl.duocuc.darmijo.consumerlocation.repository.VehicleLocationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class LocationStorageService {

    @Autowired
    private VehicleLocationRepository repository;

    @Transactional
    public void saveLocation(VehicleLocationDTO locationDTO) {
        try {
            VehicleLocation location = new VehicleLocation();
            location.setVehicleId(locationDTO.getVehicleId());
            location.setLatitude(locationDTO.getLatitude());
            location.setLongitude(locationDTO.getLongitude());
            location.setTimestamp(locationDTO.getTimestamp());
            location.setSpeed(locationDTO.getSpeed());
            location.setRoute(locationDTO.getRoute());

            repository.save(location);
            log.info(
                "Ubicación guardada en base de datos para vehículo: {} en {}",
                locationDTO.getVehicleId(),
                locationDTO.getTimestamp()
            );
        } catch (Exception e) {
            log.error(
                "Error al guardar ubicación para vehículo: {}",
                locationDTO.getVehicleId(),
                e
            );
            throw new RuntimeException(
                "Error al guardar ubicación en base de datos",
                e
            );
        }
    }
}
