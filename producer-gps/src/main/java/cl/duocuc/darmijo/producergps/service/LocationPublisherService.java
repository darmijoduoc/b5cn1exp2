package cl.duocuc.darmijo.producergps.service;

import cl.duocuc.darmijo.producergps.config.RabbitMQConfig;
import cl.duocuc.darmijo.producergps.dto.VehicleLocationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class LocationPublisherService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void publishLocation(VehicleLocationDTO location) {
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.GPS_QUEUE, location);
            log.debug("Mensaje de ubicación enviado a la cola: {} para vehículo: {}",
                    RabbitMQConfig.GPS_QUEUE, location.getVehicleId());
        } catch (AmqpException e) {
            log.error("Error al enviar mensaje de ubicación a RabbitMQ para vehículo: {}",
                    location.getVehicleId(), e);
            throw new RuntimeException("Error al publicar ubicación", e);
        }
    }

    public void publishLocationsBatch(List<VehicleLocationDTO> locations) {
        log.info("Publicando lote de {} ubicaciones", locations.size());
        int successCount = 0;
        int failCount = 0;

        for (VehicleLocationDTO location : locations) {
            try {
                publishLocation(location);
                successCount++;
            } catch (Exception e) {
                failCount++;
                log.error("Error al publicar ubicación del vehículo: {}",
                        location.getVehicleId(), e);
            }
        }

        log.info("Publicación de lote completada. Exitosas: {}, Fallidas: {}", successCount, failCount);
    }
}
