package cl.duocuc.darmijo.consumerschedule.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleUpdateDTO {
    private String updateId;
    private String vehicleId;
    private String routeId;
    private ScheduleType scheduleType;
    private LocalDateTime scheduledTime;
    private LocalDateTime actualTime;
    private String status;
    private LocalDateTime timestamp;
}
