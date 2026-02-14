package cl.duocuc.darmijo.consumerlocation.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleLocationDTO {
    private String vehicleId;
    private Double latitude;
    private Double longitude;
    private LocalDateTime timestamp;
    private Double speed;
    private String route;
}
