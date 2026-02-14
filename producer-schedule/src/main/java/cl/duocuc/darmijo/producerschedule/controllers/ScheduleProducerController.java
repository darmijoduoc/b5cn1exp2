package cl.duocuc.darmijo.producerschedule.controllers;

import cl.duocuc.darmijo.producerschedule.dto.ScheduleUpdateDTO;
import cl.duocuc.darmijo.producerschedule.service.SchedulePublisherService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/producer/schedule")
@Slf4j
public class ScheduleProducerController {

    @Autowired
    private SchedulePublisherService publisherService;

    @PostMapping("/update")
    public ResponseEntity<?> publishScheduleUpdate(@Valid @RequestBody ScheduleUpdateDTO scheduleUpdate) {
        try {
            publisherService.publishScheduleUpdate(scheduleUpdate);
            log.info("Actualización de horario publicada para ID: {}", scheduleUpdate.getUpdateId());
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Actualización de horario enviada a la cola",
                    "updateId", scheduleUpdate.getUpdateId(),
                    "vehicleId", scheduleUpdate.getVehicleId()
            ));
        } catch (Exception e) {
            log.error("Error al publicar actualización de horario para ID: {}", scheduleUpdate.getUpdateId(), e);
            return ResponseEntity.status(500).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/route-change")
    public ResponseEntity<?> publishRouteChange(@Valid @RequestBody ScheduleUpdateDTO scheduleUpdate) {
        try {
            publisherService.publishScheduleUpdate(scheduleUpdate);
            log.info("Cambio de ruta publicado para vehículo: {}", scheduleUpdate.getVehicleId());
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Cambio de ruta enviado a la cola",
                    "updateId", scheduleUpdate.getUpdateId(),
                    "vehicleId", scheduleUpdate.getVehicleId()
            ));
        } catch (Exception e) {
            log.error("Error al publicar cambio de ruta", e);
            return ResponseEntity.status(500).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/updates/batch")
    public ResponseEntity<?> publishScheduleUpdatesBatch(@Valid @RequestBody List<ScheduleUpdateDTO> scheduleUpdates) {
        try {
            if (scheduleUpdates == null || scheduleUpdates.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "error",
                        "message", "La lista de actualizaciones de horario no puede estar vacía"
                ));
            }

            publisherService.publishScheduleUpdatesBatch(scheduleUpdates);
            log.info("Lote de {} actualizaciones de horario publicadas", scheduleUpdates.size());
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Lote enviado a la cola",
                    "count", scheduleUpdates.size()
            ));
        } catch (Exception e) {
            log.error("Error al publicar lote de actualizaciones de horario", e);
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
                "service", "producer-schedule",
                "timestamp", System.currentTimeMillis()
        ));
    }
}
