package cl.duocuc.darmijo.producerschedule.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleUpdateDTO {

    @NotBlank(message = "Update ID is required")
    private String updateId;

    @NotBlank(message = "Vehicle ID is required")
    private String vehicleId;

    @NotBlank(message = "Route ID is required")
    private String routeId;

    @NotNull(message = "Schedule type is required")
    private ScheduleType scheduleType;

    @NotNull(message = "Scheduled time is required")
    private LocalDateTime scheduledTime;

    private LocalDateTime actualTime;

    private String status;

    @NotNull(message = "Timestamp is required")
    private LocalDateTime timestamp;
}
