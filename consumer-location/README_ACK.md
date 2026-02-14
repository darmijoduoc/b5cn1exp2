# Acknowledgement Manual - Consumer Location

## Configuración

Este consumer utiliza **acknowledgement manual** para garantizar que los mensajes solo se marquen como procesados después de guardar exitosamente en la base de datos.

## Flujo de Procesamiento

```
1. Recibir mensaje de RabbitMQ
   ↓
2. Log: "Mensaje de ubicación recibido"
   ↓
3. Guardar en base de datos Oracle (VEHICLE_LOCATIONS)
   ↓
4. Log: "Ubicación guardada en base de datos"
   ↓
5. Sleep 300ms ⏱️
   ↓
6. channel.basicAck() - Confirmar procesamiento
   ↓
7. Log: "Ubicación procesada exitosamente (ACK enviado)"
```

## Características

### ✅ Acknowledgement Manual

- **Modo**: `AcknowledgeMode.MANUAL`
- **Confirmación**: `channel.basicAck(deliveryTag, false)`
- **Timing**: 300ms después de guardar en BD

### 🔄 Manejo de Errores

Si ocurre un error durante el procesamiento:

```java
channel.basicNack(deliveryTag, false, true)
// false: no procesar múltiples mensajes
// true: requeue (reencolar el mensaje)
```

El mensaje vuelve a la cola y será procesado nuevamente.

### ⏱️ Delay de 300ms

El sleep de 300ms simula:
- Tiempo de confirmación de escritura en BD
- Latencia de red
- Procesamiento adicional

## Logs

### Procesamiento Exitoso

```
Mensaje de ubicación recibido para vehículo: BUS-001 en lat: -33.4489, lon: -70.6693
Ubicación guardada en base de datos para vehículo: BUS-001 en 2026-02-14T19:00:00
Ubicación procesada exitosamente para vehículo: BUS-001 (ACK enviado)
```

### Error y Requeue

```
Mensaje de ubicación recibido para vehículo: BUS-002 en lat: -33.4500, lon: -70.6700
Error al guardar ubicación para vehículo: BUS-002
java.sql.SQLException: ...
Error al procesar mensaje de ubicación para vehículo: BUS-002
Mensaje reencolado para vehículo: BUS-002
```

## Ventajas

1. **Garantía de procesamiento**: El mensaje solo se confirma si se guardó correctamente
2. **Reintento automático**: Mensajes fallidos vuelven a la cola
3. **No hay pérdida de datos**: Si el consumer falla, los mensajes no confirmados persisten
4. **Control de flujo**: El delay evita saturar la base de datos

## Configuración

### RabbitMQConfig.java

```java
@Bean
public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
        ConnectionFactory connectionFactory,
        Jackson2JsonMessageConverter messageConverter) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    factory.setMessageConverter(messageConverter);
    factory.setAcknowledgeMode(AcknowledgeMode.MANUAL); // ⭐ Manual ACK
    return factory;
}
```

### LocationConsumer.java

```java
@RabbitListener(queues = RabbitMQConfig.GPS_QUEUE)
public void handleLocationMessage(VehicleLocationDTO location, Channel channel, Message message) {
    try {
        storageService.saveLocation(location);
        Thread.sleep(300); // ⏱️ 300ms delay
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false); // ✅ ACK
    } catch (Exception e) {
        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true); // 🔄 NACK
    }
}
```

## Testing

```bash
# Enviar mensajes de prueba
./load_test.py

# Ver logs con ACK
docker logs -f consumer-location | grep "ACK enviado"

# Contar mensajes procesados
docker logs consumer-location 2>&1 | grep "ACK enviado" | wc -l
```

## Troubleshooting

### Mensajes no se procesan

```bash
# Verificar modo de ACK
docker logs consumer-location | grep "AcknowledgeMode"

# Ver mensajes pendientes en RabbitMQ
curl -s -u admin:admin123 http://localhost:15672/api/queues/%2F/gps.locations.queue | jq '.messages'
```

### Mensajes reencolados constantemente

Si ves muchos logs de "Mensaje reencolado":
1. Verificar conexión a Oracle
2. Revisar credenciales de BD
3. Verificar que la tabla VEHICLE_LOCATIONS existe

---

**Última actualización:** 2026-02-14
