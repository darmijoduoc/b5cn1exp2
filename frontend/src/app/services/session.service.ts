import { Injectable } from '@angular/core';
import { Session } from '../models/session';
import { User } from '../models/user';
import { BehaviorSubject, map, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SessionService {
  private sessionSubject = new BehaviorSubject<Session | null>(this.getStoredSession());
  
  // Observable público para que los componentes se suscriban
  public session$ = this.sessionSubject.asObservable();

  constructor() { }

  private getStoredSession(): Session | null {
    const sessionData = localStorage.getItem('session');
    return sessionData ? JSON.parse(sessionData) : null;
  }

  getSession(): Observable<Session | null> {
    return this.session$;
  }

  clearSession(): void {
    localStorage.removeItem('session');
    this.sessionSubject.next(null);
  }

  setSession(session: Session): void {
    localStorage.setItem('session', JSON.stringify(session));
    this.sessionSubject.next(session);
  }

  isLoggedIn(): Observable<boolean> {
    return this.session$.pipe(
      map(session => session !== null)
    );
  }

  // Método para obtener el valor actual de la sesión de forma síncrona
  getCurrentSession(): Session | null {
    return this.sessionSubject.value;
  }

}
