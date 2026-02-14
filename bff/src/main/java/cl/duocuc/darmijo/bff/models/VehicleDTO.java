package cl.duocuc.darmijo.bff.models;

import lombok.Data;

@Data
public class VehicleDTO {
    private String ulid;
    private String plate;
    private String type;
    private String status;
    private String currentAddress;
    private RouteDTO route;
}
