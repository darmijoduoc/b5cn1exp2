package cl.duocuc.darmijo.transport.controllers;

import cl.duocuc.darmijo.transport.models.Route;
import cl.duocuc.darmijo.transport.models.Vehicle;
import cl.duocuc.darmijo.transport.service.TransportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/transport")
@CrossOrigin(origins = "*")
public class TransportController {

    @Autowired
    private TransportService transportService;

    @GetMapping("/vehicles")
    public List<Vehicle> getVehicles() {
        System.out.println(">> [BACKEND] GET /api/transport/vehicles ejecutado.");
        return transportService.listVehicles();
    }

    @PostMapping("/vehicles")
    public Vehicle createVehicle(@RequestBody Vehicle vehicle) {
        System.out.println(">> [BACKEND] POST /api/transport/vehicles ejecutado.");
        return transportService.saveVehicle(vehicle);
    }

    @DeleteMapping("/vehicles/{ulid}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable String ulid) {
        System.out.println(">> [BACKEND] DELETE /api/transport/vehicles/" + ulid + " ejecutado.");
        transportService.deleteVehicle(ulid);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/routes")
    public List<Route> getRoutes() {
        System.out.println(">> [BACKEND] GET /api/transport/routes ejecutado.");
        return transportService.listRoutes();
    }

    @PostMapping("/routes")
    public Route createRoute(@RequestBody Route route) {
        return transportService.saveRoute(route);
    }

    @DeleteMapping("/routes/{ulid}")
    public ResponseEntity<Void> deleteRoute(@PathVariable String ulid) {
        transportService.deleteRoute(ulid);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/vehicles/{ulid}/location")
    public ResponseEntity<Vehicle> updateLocation(@PathVariable String ulid, @RequestBody String address) {
        return ResponseEntity.ok(transportService.updateLocation(ulid, address));
    }
}
