package cl.duocuc.darmijo.consumerlocation.consumers;

import cl.duocuc.darmijo.consumerlocation.config.RabbitMQConfig;
import cl.duocuc.darmijo.consumerlocation.models.VehicleLocationDTO;
import cl.duocuc.darmijo.consumerlocation.service.LocationStorageService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LocationConsumer {

    @Autowired
    private LocationStorageService storageService;

    @RabbitListener(queues = RabbitMQConfig.GPS_QUEUE, ackMode = "MANUAL")
    public void handleLocationMessage(
        VehicleLocationDTO location,
        Channel channel,
        Message message
    ) {
        try {
            log.info(
                "Mensaje de ubicación recibido para vehículo: {} en lat: {}, lon: {}",
                location.getVehicleId(),
                location.getLatitude(),
                location.getLongitude()
            );

            // Guardar en base de datos
            storageService.saveLocation(location);

            log.info(
                "Ubicación guardada en base de datos para vehículo: {}",
                location.getVehicleId()
            );

            // Sleep de 300ms antes de confirmar
            Thread.sleep(300);

            // Acknowledgement manual
            channel.basicAck(
                message.getMessageProperties().getDeliveryTag(),
                false
            );

            log.info(
                "Ubicación procesada exitosamente para vehículo: {} (ACK enviado)",
                location.getVehicleId()
            );
        } catch (Exception e) {
            log.error(
                "Error al procesar mensaje de ubicación para vehículo: {}",
                location.getVehicleId(),
                e
            );
            try {
                // Rechazar el mensaje y reencolarlo
                channel.basicNack(
                    message.getMessageProperties().getDeliveryTag(),
                    false,
                    true
                );
                log.warn(
                    "Mensaje reencolado para vehículo: {}",
                    location.getVehicleId()
                );
            } catch (Exception nackException) {
                log.error("Error al rechazar mensaje", nackException);
            }
        }
    }
}
