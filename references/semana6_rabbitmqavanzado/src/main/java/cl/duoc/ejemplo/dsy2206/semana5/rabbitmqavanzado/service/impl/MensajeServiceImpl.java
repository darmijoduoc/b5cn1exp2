package cl.duoc.ejemplo.dsy2206.semana5.rabbitmqavanzado.service.impl;

import java.io.IOException;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.rabbitmq.client.Channel;

import cl.duoc.ejemplo.dsy2206.semana5.rabbitmqavanzado.config.RabbitMQConfig;
import cl.duoc.ejemplo.dsy2206.semana5.rabbitmqavanzado.service.MensajeService;

@Service
public class MensajeServiceImpl implements MensajeService {

	private final RabbitTemplate rabbitTemplate;

	public MensajeServiceImpl(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	@Override
	public void enviarMensaje(String mensaje) {

		rabbitTemplate.convertAndSend(RabbitMQConfig.MAIN_QUEUE, mensaje);
	}

	@Override
	public void enviarObjeto(Object objeto) {

		rabbitTemplate.convertAndSend(RabbitMQConfig.MAIN_QUEUE, objeto);
	}

//	@RabbitListener(id = "listener-myQueue", queues = RabbitMQConfig.MAIN_QUEUE)
	@Override
	public void recibirMensaje(Object objeto) {

		System.out.println("Mensaje recibido en myQueue: " + objeto);
	}

	@RabbitListener(id = "listener-dlx-queue", queues = RabbitMQConfig.DLX_QUEUE)
	@Override
	public void recibirDeadLetter(Object objeto) {

		System.out.println("Mensaje recibido en DLQ: " + objeto);
	}

	@RabbitListener(id = "listener-myQueue", queues = RabbitMQConfig.MAIN_QUEUE, ackMode = "MANUAL")
	@Override
	public void recibirMensajeConAckManual(Message mensaje, Channel canal) throws IOException {

		try {
			System.out.println("Mensaje recibido: " + new String(mensaje.getBody()));
			Thread.sleep(10000);

			canal.basicAck(mensaje.getMessageProperties().getDeliveryTag(), false);
			System.out.println("Acknowledge OK enviado");
		} catch (Exception e) {
			canal.basicNack(mensaje.getMessageProperties().getDeliveryTag(), false, false);
			System.out.println("Acknowledge NO OK enviado");
		}
	}
}
