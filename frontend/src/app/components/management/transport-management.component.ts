import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TransportService } from '../../services/transport.service';
import { Vehicle, Route, RoutePoint } from '../../models/transport';

@Component({
  selector: 'app-transport-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './transport-management.component.html',
  styleUrls: ['./transport-management.component.scss']
})
export class TransportManagementComponent implements OnInit {
  vehicles: Vehicle[] = [];
  routes: Route[] = [];
  
  // Form models
  newVehicle: any = { plate: '', type: 'Bus', status: 'Active', routeUlid: '' };
  newRoute: any = { code: '', title: '', description: '', points: [] };
  
  // Point manager
  newPointName: string = '';

  constructor(private transportService: TransportService) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.transportService.getVehicles().subscribe(data => this.vehicles = data);
    this.transportService.getRoutes().subscribe(data => {
      console.log('Routes loaded:', data);
      this.routes = data;
    });
  }

  addPoint(): void {
    if (this.newPointName.trim()) {
      this.newRoute.points.push({
        name: this.newPointName.trim(),
        position: this.newRoute.points.length
      });
      this.newPointName = '';
    }
  }

  removePoint(index: number): void {
    this.newRoute.points.splice(index, 1);
    this.newRoute.points.forEach((p: any, i: number) => p.position = i);
  }

  createVehicle(): void {
    const vehicleToSave: any = {
      plate: this.newVehicle.plate,
      type: this.newVehicle.type,
      status: this.newVehicle.status,
      currentAddress: 'En Terminal',
      route: this.routes.find(r => r.ulid === this.newVehicle.routeUlid)
    };

    this.transportService.saveVehicle(vehicleToSave).subscribe({
      next: () => {
        this.loadData();
        this.newVehicle = { plate: '', type: 'Bus', status: 'Active', routeUlid: '' };
      },
      error: (err) => console.error('Error creating vehicle:', err)
    });
  }

  deleteVehicle(ulid: string): void {
    if(confirm('¿Está seguro de que desea eliminar este vehículo?')) {
      this.transportService.deleteVehicle(ulid).subscribe(() => this.loadData());
    }
  }

  createRoute(): void {
    if (this.newRoute.points.length < 2) {
      alert('La ruta debe tener al menos 2 puntos (Origen y Destino)');
      return;
    }
    
    this.transportService.saveRoute(this.newRoute).subscribe({
      next: () => {
        this.loadData();
        this.newRoute = { code: '', title: '', description: '', points: [] };
      },
      error: (err) => console.error('Error creating route:', err)
    });
  }

  deleteRoute(ulid: string): void {
    if(confirm('Are you sure you want to delete this route?')) {
      this.transportService.deleteRoute(ulid).subscribe(() => this.loadData());
    }
  }
}
