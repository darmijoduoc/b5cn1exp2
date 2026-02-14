package cl.duocuc.darmijo.consumerschedule.service;

import cl.duocuc.darmijo.consumerschedule.dto.ScheduleUpdateDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    @Value("${file.storage.path:/app/data/schedules}")
    private String storagePath;

    private ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(storagePath));
            log.info("Directorio de almacenamiento creado/verificado: {}", storagePath);

            objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        } catch (IOException e) {
            log.error("Error al crear directorio de almacenamiento: {}", storagePath, e);
            throw new RuntimeException("No se puede inicializar almacenamiento de archivos", e);
        }
    }

    public void saveScheduleAsJson(ScheduleUpdateDTO schedule) throws IOException {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String fileName = String.format("schedule_%s_%s.json", timestamp, uuid);

        Path filePath = Paths.get(storagePath, fileName);

        objectMapper.writeValue(filePath.toFile(), schedule);

        log.info("Archivo JSON creado: {} para ID de actualización: {}", filePath, schedule.getUpdateId());
    }
}
