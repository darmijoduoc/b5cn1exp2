package cl.duocuc.darmijo.transport.models;

import com.github.f4b6a3.ulid.UlidCreator;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "transport_vehicle")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String ulid;
    
    @Column(unique = true)
    private String plate;
    
    private String type; // Bus, Subway, Train
    private String status; // Active, Maintenance, Out of Service
    private String currentAddress;
    
    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;
    
    @PrePersist
    public void prePersist() {
        if (this.ulid == null) {
            this.ulid = UlidCreator.getUlid().toString();
        }
    }
}
