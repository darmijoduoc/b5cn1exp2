package cl.duocuc.darmijo.transport;

import cl.duocuc.darmijo.transport.models.Route;
import cl.duocuc.darmijo.transport.models.Vehicle;
import cl.duocuc.darmijo.transport.repository.RouteRepository;
import cl.duocuc.darmijo.transport.repository.VehicleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import java.util.Arrays;
import java.util.List;

@Configuration
public class TransportDataLoader {

    @Bean
    @Transactional
    CommandLineRunner initTransportData(VehicleRepository vehicleRepo, RouteRepository routeRepo) {
        return args -> {
            vehicleRepo.deleteAll();
            routeRepo.deleteAll();

            // Ruta 201
            Route r1 = new Route();
            r1.setCode("201");
            r1.setTitle("Pudahuel -> Estación Central");
            r1.setDescription("Servicio troncal del sector poniente");
            List<String> stops1 = Arrays.asList(
                "Terminal Pudahuel", "Metro San Pablo", "Metro Neptuno", 
                "Metro Pajaritos", "Metro Las Rejas", "Metro Ecuador", 
                "Metro San Alberto Hurtado", "Metro U. de Santiago", "Estación Central", "Terminal San Borja"
            );
            for (int i = 0; i < stops1.size(); i++) {
                r1.addPoint(stops1.get(i), i);
            }
            r1 = routeRepo.save(r1);

            // Ruta 202
            Route r2 = new Route();
            r2.setCode("202");
            r2.setTitle("Estación Central -> Plaza Italia");
            r2.setDescription("Conexión centro cívico y comercial");
            r2.setPreviousRoute(r1);
            List<String> stops2 = Arrays.asList(
                "Estación Central", "Metro ULA", "Metro República", 
                "Metro Los Héroes", "Metro La Moneda", "Metro U. de Chile", 
                "Metro Santa Lucía", "Metro U. Católica", "Metro Baquedano", "Plaza Italia"
            );
            for (int i = 0; i < stops2.size(); i++) {
                r2.addPoint(stops2.get(i), i);
            }
            r2 = routeRepo.save(r2);

            // Ruta 203
            Route r3 = new Route();
            r3.setCode("203");
            r3.setTitle("Plaza Italia -> Las Condes");
            r3.setDescription("Eje Providencia y sector oriente");
            r3.setPreviousRoute(r2);
            List<String> stops3 = Arrays.asList(
                "Plaza Italia", "Metro Salvador", "Metro Manuel Montt", 
                "Metro Pedro de Valdivia", "Metro Los Leones", "Metro Tobalaba", 
                "Metro El Golf", "Metro Alcántara", "Metro Escuela Militar", "Terminal Manquehue"
            );
            for (int i = 0; i < stops3.size(); i++) {
                r3.addPoint(stops3.get(i), i);
            }
            r3 = routeRepo.save(r3);

            // Vehículos (Usamos estados en inglés para la lógica del código, pero datos demo en español)
            vehicleRepo.save(createV("BJ-FW-10", "Bus", "Active", "Terminal Pudahuel", r1));
            vehicleRepo.save(createV("MT-L1-05", "Metro", "Active", "Estación Central", r2));
            vehicleRepo.save(createV("GH-TY-99", "Bus", "Maintenance", "Talleres Manquehue", r3));

            System.out.println(">> [OK] Datos de demostración cargados exitosamente en español.");
        };
    }

    private Vehicle createV(String p, String t, String s, String a, Route r) {
        Vehicle v = new Vehicle();
        v.setPlate(p); v.setType(t); v.setStatus(s); v.setCurrentAddress(a); v.setRoute(r);
        return v;
    }
}