package cl.duocuc.darmijo.producerschedule.service;

import cl.duocuc.darmijo.producerschedule.config.RabbitMQConfig;
import cl.duocuc.darmijo.producerschedule.dto.ScheduleUpdateDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SchedulePublisherService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void publishScheduleUpdate(ScheduleUpdateDTO scheduleUpdate) {
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.SCHEDULE_QUEUE, scheduleUpdate);
            log.debug("Actualización de horario enviada a la cola: {} para ID: {}",
                    RabbitMQConfig.SCHEDULE_QUEUE, scheduleUpdate.getUpdateId());
        } catch (AmqpException e) {
            log.error("Error al enviar actualización de horario a RabbitMQ para ID: {}",
                    scheduleUpdate.getUpdateId(), e);
            throw new RuntimeException("Error al publicar actualización de horario", e);
        }
    }

    public void publishScheduleUpdatesBatch(List<ScheduleUpdateDTO> scheduleUpdates) {
        log.info("Publicando lote de {} actualizaciones de horario", scheduleUpdates.size());
        int successCount = 0;
        int failCount = 0;

        for (ScheduleUpdateDTO scheduleUpdate : scheduleUpdates) {
            try {
                publishScheduleUpdate(scheduleUpdate);
                successCount++;
            } catch (Exception e) {
                failCount++;
                log.error("Error al publicar actualización de horario para ID: {}",
                        scheduleUpdate.getUpdateId(), e);
            }
        }

        log.info("Publicación de lote completada. Exitosas: {}, Fallidas: {}", successCount, failCount);
    }
}
