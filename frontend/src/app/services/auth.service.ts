import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Session } from '../models/session';
import { User } from '../models/user';

@Injectable({
  providedIn: 'root'
})
export class AuthService {


  private readonly apiUrl = `${environment.apiBaseUrl}`;
  
  constructor(private http: HttpClient) {}

  getAll(): Observable<Session[]> {
      return this.http.get<Session[]>(this.apiUrl);
  }

  authenticate(email: string, password: string): Observable<Session> {
      return this.http.post<Session>(`${this.apiUrl}/authenticate`, { email, password });
  }

  logout(): Observable<void> {
      return this.http.post<void>(`${this.apiUrl}/logout`, {});
  }

  getCurrentUser(): Observable<User> {
      return this.http.get<User>(`${this.apiUrl}/current_user`);
  }

  refreshSession(): Observable<Session> {
      return this.http.get<Session>(`${this.apiUrl}/refresh`);
  }

  forgotPassword(email: string): Observable<{ message: string }> {
      return this.http.post<{ message: string }>(`${this.apiUrl}/forgot_password`, { email });
  }

}
