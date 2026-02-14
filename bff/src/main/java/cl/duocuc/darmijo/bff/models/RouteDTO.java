package cl.duocuc.darmijo.bff.models;

import lombok.Data;
import java.util.List;

@Data
public class RouteDTO {
    private String ulid;
    private String code;
    private String title;
    private String description;
    private List<RoutePointDTO> points;
    private RouteDTO previousRoute;
}
