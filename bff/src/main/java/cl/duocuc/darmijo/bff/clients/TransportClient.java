package cl.duocuc.darmijo.bff.clients;

import cl.duocuc.darmijo.bff.models.RouteDTO;
import cl.duocuc.darmijo.bff.models.VehicleDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "transport-backend", url = "${backend.url}")
public interface TransportClient {

    @GetMapping("/api/transport/vehicles")
    List<VehicleDTO> getVehicles(@RequestHeader("Authorization") String authHeader);

    @PostMapping("/api/transport/vehicles")
    VehicleDTO createVehicle(@RequestHeader("Authorization") String authHeader, @RequestBody VehicleDTO vehicle);

    @DeleteMapping("/api/transport/vehicles/{ulid}")
    void deleteVehicle(@RequestHeader("Authorization") String authHeader, @PathVariable("ulid") String ulid);

    @GetMapping("/api/transport/routes")
    List<RouteDTO> getRoutes(@RequestHeader("Authorization") String authHeader);

    @PostMapping("/api/transport/routes")
    RouteDTO createRoute(@RequestHeader("Authorization") String authHeader, @RequestBody RouteDTO route);

    @DeleteMapping("/api/transport/routes/{ulid}")
    void deleteRoute(@RequestHeader("Authorization") String authHeader, @PathVariable("ulid") String ulid);

    @PatchMapping("/api/transport/vehicles/{ulid}/location")
    VehicleDTO updateLocation(@RequestHeader("Authorization") String authHeader, @PathVariable("ulid") String ulid, @RequestBody String address);
}