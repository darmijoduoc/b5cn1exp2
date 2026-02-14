package cl.duocuc.darmijo.bff.controllers;

import cl.duocuc.darmijo.bff.clients.TransportClient;
import cl.duocuc.darmijo.bff.models.RouteDTO;
import cl.duocuc.darmijo.bff.models.VehicleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bff/transport")
@CrossOrigin
public class BffController {

    @Autowired
    private TransportClient transportClient;

    @GetMapping("/vehicles")
    public List<VehicleDTO> getVehicles(@RequestHeader("Authorization") String authHeader) {
        return transportClient.getVehicles(authHeader);
    }

    @PostMapping("/vehicles")
    public VehicleDTO createVehicle(@RequestHeader("Authorization") String authHeader, @RequestBody VehicleDTO vehicle) {
        return transportClient.createVehicle(authHeader, vehicle);
    }

    @DeleteMapping("/vehicles/{ulid}")
    public void deleteVehicle(@RequestHeader("Authorization") String authHeader, @PathVariable String ulid) {
        transportClient.deleteVehicle(authHeader, ulid);
    }

    @GetMapping("/routes")
    public List<RouteDTO> getRoutes(@RequestHeader("Authorization") String authHeader) {
        return transportClient.getRoutes(authHeader);
    }

    @PostMapping("/routes")
    public RouteDTO createRoute(@RequestHeader("Authorization") String authHeader, @RequestBody RouteDTO route) {
        return transportClient.createRoute(authHeader, route);
    }

    @DeleteMapping("/routes/{ulid}")
    public void deleteRoute(@RequestHeader("Authorization") String authHeader, @PathVariable String ulid) {
        transportClient.deleteRoute(authHeader, ulid);
    }

    @PatchMapping("/vehicles/{ulid}/location")
    public VehicleDTO updateLocation(@RequestHeader("Authorization") String authHeader, @PathVariable String ulid, @RequestBody String address) {
        return transportClient.updateLocation(authHeader, ulid, address);
    }
}