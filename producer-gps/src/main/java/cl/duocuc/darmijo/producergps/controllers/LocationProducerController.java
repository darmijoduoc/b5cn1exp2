package cl.duocuc.darmijo.producergps.controllers;

import cl.duocuc.darmijo.producergps.dto.VehicleLocationDTO;
import cl.duocuc.darmijo.producergps.service.LocationPublisherService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/producer/gps")
@Slf4j
public class LocationProducerController {

    @Autowired
    private LocationPublisherService publisherService;

    @PostMapping("/location")
    public ResponseEntity<?> publishLocation(@Valid @RequestBody VehicleLocationDTO location) {
        try {
            publisherService.publishLocation(location);
            log.info("Ubicación publicada para vehículo: {}", location.getVehicleId());
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Ubicación enviada a la cola",
                    "vehicleId", location.getVehicleId(),
                    "timestamp", location.getTimestamp()
            ));
        } catch (Exception e) {
            log.error("Error al publicar ubicación del vehículo: {}", location.getVehicleId(), e);
            return ResponseEntity.status(500).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/locations/batch")
    public ResponseEntity<?> publishLocationsBatch(@Valid @RequestBody List<VehicleLocationDTO> locations) {
        try {
            if (locations == null || locations.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "error",
                        "message", "La lista de ubicaciones no puede estar vacía"
                ));
            }

            publisherService.publishLocationsBatch(locations);
            log.info("Lote de {} ubicaciones publicadas", locations.size());
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Lote enviado a la cola",
                    "count", locations.size()
            ));
        } catch (Exception e) {
            log.error("Error al publicar lote de ubicaciones", e);
            return ResponseEntity.status(500).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "producer-gps",
                "timestamp", System.currentTimeMillis()
        ));
    }
}
