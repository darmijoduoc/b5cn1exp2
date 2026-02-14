# Acknowledgement Manual - Consumer Schedule

## Configuración

Este consumer utiliza **acknowledgement manual** para garantizar que los mensajes solo se marquen como procesados después de crear exitosamente el archivo JSON.

## Flujo de Procesamiento

```
1. Recibir mensaje de RabbitMQ
   ↓
2. Log: "Actualización de horario recibida"
   ↓
3. Crear archivo JSON (/app/data/schedules/)
   ↓
4. Log: "Archivo JSON creado"
   ↓
5. Sleep 1000ms (1 segundo) ⏱️
   ↓
6. channel.basicAck() - Confirmar procesamiento
   ↓
7. Log: "Actualización procesada exitosamente (ACK enviado)"
```

## Características

### ✅ Acknowledgement Manual

- **Modo**: `AcknowledgeMode.MANUAL`
- **Confirmación**: `channel.basicAck(deliveryTag, false)`
- **Timing**: 1 segundo después de crear archivo JSON

### 🔄 Manejo de Errores

Si ocurre un error durante el procesamiento:

```java
channel.basicNack(deliveryTag, false, true)
// false: no procesar múltiples mensajes
// true: requeue (reencolar el mensaje)
```

El mensaje vuelve a la cola y será procesado nuevamente.

### ⏱️ Delay de 1 Segundo

El sleep de 1 segundo simula:
- Tiempo de escritura en disco
- Sincronización de filesystem
- Validación del archivo creado

## Logs

### Procesamiento Exitoso

```
Actualización de horario recibida: ID=UPDATE-001, Vehículo=BUS-001, Tipo=ARRIVAL
Archivo JSON creado para actualización: UPDATE-001
Actualización de horario procesada exitosamente: UPDATE-001 (ACK enviado)
```

### Error y Requeue

```
Actualización de horario recibida: ID=UPDATE-002, Vehículo=BUS-002, Tipo=DEPARTURE
Error al crear directorio de almacenamiento: /app/data/schedules
Error al procesar mensaje de horario para ID: UPDATE-002
Mensaje reencolado para ID: UPDATE-002
```

## Ventajas

1. **Garantía de procesamiento**: El mensaje solo se confirma si el archivo se creó correctamente
2. **Reintento automático**: Mensajes fallidos vuelven a la cola
3. **No hay pérdida de datos**: Si el consumer falla, los mensajes no confirmados persisten
4. **Control de flujo**: El delay de 1s evita saturar el filesystem
5. **Auditoría completa**: Todos los archivos JSON son persistentes

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

### ScheduleConsumer.java

```java
@RabbitListener(queues = RabbitMQConfig.SCHEDULE_QUEUE)
public void handleScheduleMessage(ScheduleUpdateDTO scheduleUpdate, Channel channel, Message message) {
    try {
        fileStorageService.saveScheduleAsJson(scheduleUpdate);
        Thread.sleep(1000); // ⏱️ 1 segundo delay
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
docker logs -f consumer-schedule | grep "ACK enviado"

# Contar mensajes procesados
docker logs consumer-schedule 2>&1 | grep "ACK enviado" | wc -l

# Verificar archivos creados
docker exec consumer-schedule ls -lh /app/data/schedules/

# Comparar cantidad de logs vs archivos
LOGS=$(docker logs consumer-schedule 2>&1 | grep "ACK enviado" | wc -l)
FILES=$(docker exec consumer-schedule sh -c "ls /app/data/schedules/ | wc -l")
echo "ACKs: $LOGS, Archivos: $FILES"
```

## Timing Comparativo

| Consumer | Delay | Justificación |
|----------|-------|---------------|
| Location | 300ms | BD Oracle más rápida |
| Schedule | 1000ms | I/O de filesystem más lento |

El delay más largo en Schedule refleja:
- Escritura física en disco
- Potenciales operaciones de sync
- Creación de metadata del archivo

## Troubleshooting

### Mensajes no se procesan

```bash
# Verificar modo de ACK
docker logs consumer-schedule | grep "AcknowledgeMode"

# Ver mensajes pendientes en RabbitMQ
curl -s -u admin:admin123 http://localhost:15672/api/queues/%2F/schedule.updates.queue | jq '.messages'
```

### Mensajes reencolados constantemente

Si ves muchos logs de "Mensaje reencolado":
1. Verificar permisos del directorio `/app/data/schedules/`
2. Verificar espacio en disco
3. Verificar que el directorio existe

```bash
# Dentro del contenedor
docker exec consumer-schedule ls -la /app/data/schedules/
docker exec consumer-schedule df -h
```

### Archivos duplicados

Si hay más archivos que ACKs enviados:
- Revisar logs de errores entre la creación del archivo y el ACK
- Verificar reinicio del consumer antes del ACK

---

**Última actualización:** 2026-02-14
