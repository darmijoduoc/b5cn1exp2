package cl.duocuc.darmijo.transport.service;

import cl.duocuc.darmijo.transport.models.Route;
import cl.duocuc.darmijo.transport.models.Vehicle;
import cl.duocuc.darmijo.transport.repository.RouteRepository;
import cl.duocuc.darmijo.transport.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TransportService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private RouteRepository routeRepository;

    public List<Vehicle> listVehicles() {
        return vehicleRepository.findAll();
    }

    public Vehicle getVehicleByUlid(String ulid) {
        return vehicleRepository.findByUlid(ulid).orElseThrow(() -> new RuntimeException("Vehicle not found"));
    }

    public Vehicle saveVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public void deleteVehicle(String ulid) {
        Vehicle v = getVehicleByUlid(ulid);
        vehicleRepository.delete(v);
    }

    public List<Route> listRoutes() {
        return routeRepository.findAll();
    }

    public Route saveRoute(Route route) {
        // Ensure bidirectional relationship for points
        if (route.getPoints() != null) {
            route.getPoints().forEach(p -> p.setRoute(route));
        }
        return routeRepository.save(route);
    }

    public void deleteRoute(String ulid) {
        Route r = routeRepository.findByUlid(ulid).orElseThrow(() -> new RuntimeException("Route not found"));
        routeRepository.delete(r);
    }

    public Vehicle updateLocation(String ulid, String newAddress) {
        Vehicle vehicle = getVehicleByUlid(ulid);
        vehicle.setCurrentAddress(newAddress);
        return vehicleRepository.save(vehicle);
    }
}
