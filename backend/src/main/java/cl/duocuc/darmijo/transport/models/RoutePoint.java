package cl.duocuc.darmijo.transport.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "transport_route_point")
public class RoutePoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private int position;
    
    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;

    public RoutePoint(String name, int position, Route route) {
        this.name = name;
        this.position = position;
        this.route = route;
    }
}
