package cl.duocuc.darmijo.consumerschedule.consumers;

import cl.duocuc.darmijo.consumerschedule.config.RabbitMQConfig;
import cl.duocuc.darmijo.consumerschedule.dto.ScheduleUpdateDTO;
import cl.duocuc.darmijo.consumerschedule.service.FileStorageService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ScheduleConsumer {

    @Autowired
    private FileStorageService fileStorageService;

    @RabbitListener(queues = RabbitMQConfig.SCHEDULE_QUEUE)
    public void handleScheduleMessage(ScheduleUpdateDTO scheduleUpdate, Channel channel, Message message) {
        try {
            log.info("Actualización de horario recibida: ID={}, Vehículo={}, Tipo={}",
                    scheduleUpdate.getUpdateId(),
                    scheduleUpdate.getVehicleId(),
                    scheduleUpdate.getScheduleType());

            // Guardar archivo JSON
            fileStorageService.saveScheduleAsJson(scheduleUpdate);

            log.info("Archivo JSON creado para actualización: {}", scheduleUpdate.getUpdateId());

            // Sleep de 1 segundo antes de confirmar
            Thread.sleep(1000);

            // Acknowledgement manual
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            
            log.info("Actualización de horario procesada exitosamente: {} (ACK enviado)",
                    scheduleUpdate.getUpdateId());
        } catch (Exception e) {
            log.error("Error al procesar mensaje de horario para ID: {}",
                    scheduleUpdate.getUpdateId(), e);
            try {
                // Rechazar el mensaje y reencolarlo
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                log.warn("Mensaje reencolado para ID: {}", scheduleUpdate.getUpdateId());
            } catch (Exception nackException) {
                log.error("Error al rechazar mensaje", nackException);
            }
        }
    }
}
