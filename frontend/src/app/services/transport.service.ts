import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Vehicle, Route } from '../models/transport';

@Injectable({
  providedIn: 'root'
})
export class TransportService {
  private apiUrl = `${environment.apiBaseUrl}/transport`;
  //private apiUrl = `http://localhost:8081/api/bff/transport`;

  constructor(private http: HttpClient) { }

  // Vehicles
  getVehicles(): Observable<Vehicle[]> {
    return this.http.get<Vehicle[]>(`${this.apiUrl}/vehicles`);
  }

  saveVehicle(vehicle: Vehicle): Observable<Vehicle> {
    return this.http.post<Vehicle>(`${this.apiUrl}/vehicles`, vehicle);
  }

  deleteVehicle(ulid: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/vehicles/${ulid}`);
  }

  // Routes
  getRoutes(): Observable<Route[]> {
    return this.http.get<Route[]>(`${this.apiUrl}/routes`);
  }

  saveRoute(route: Route): Observable<Route> {
    return this.http.post<Route>(`${this.apiUrl}/routes`, route);
  }

  deleteRoute(ulid: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/routes/${ulid}`);
  }

  updateLocation(ulid: string, address: string): Observable<Vehicle> {
    return this.http.patch<Vehicle>(`${this.apiUrl}/vehicles/${ulid}/location`, address);
  }
}
