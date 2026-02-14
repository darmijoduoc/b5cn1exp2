package cl.duocuc.darmijo.transport.models;

import com.github.f4b6a3.ulid.UlidCreator;
import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "transport_route")
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String ulid;
    
    @Column(unique = true)
    private String code;
    
    private String title;
    private String description;
    
    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("position ASC")
    @JsonIgnoreProperties("route")
    private List<RoutePoint> points = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "previous_route_id")
    @JsonIgnoreProperties({"points", "previousRoute"})
    private Route previousRoute;
    
    @PrePersist
    public void prePersist() {
        if (this.ulid == null) {
            this.ulid = UlidCreator.getUlid().toString();
        }
    }

    public void addPoint(String name, int position) {
        RoutePoint point = new RoutePoint(name, position, this);
        this.points.add(point);
    }
}
