import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TransportService } from '../../services/transport.service';
import { Vehicle } from '../../models/transport';

@Component({
  selector: 'app-monitoring',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './monitoring.component.html',
  styleUrls: ['./monitoring.component.scss']
})
export class MonitoringComponent implements OnInit {
  vehicles: Vehicle[] = [];
  loading = true;

  constructor(private transportService: TransportService) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    this.transportService.getVehicles().subscribe({
      next: (data) => {
        this.vehicles = data.map(v => ({
          ...v,
          currentAddress: this.getRandomLocationFromRoute(v)
        }));
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  getRandomLocationFromRoute(vehicle: Vehicle): string {
    if (vehicle.route && vehicle.route.points && vehicle.route.points.length > 0) {
      const points = vehicle.route.points;
      const randomIndex = Math.floor(Math.random() * points.length);
      return points[randomIndex].name;
    }
    return "Ubicación no disponible (Sin paradas en la ruta)";
  }

  refreshLocations(): void {
    this.vehicles = this.vehicles.map(v => ({
      ...v,
      currentAddress: this.getRandomLocationFromRoute(v)
    }));
  }
}