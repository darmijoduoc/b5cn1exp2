import { TestBed } from '@angular/core/testing';
import { SessionService } from './session.service';
import { Session } from '../models/session';

describe('SessionService', () => {
  let service: SessionService;

  const mockSession: Session = {
    id: 1,
    ulid: 'session-ulid',
    displayName: 'Test User',
    email: 'test@test.com',
    rol: 'ADMIN'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionService);
    
    // Limpiar localStorage antes de cada test
    localStorage.clear();
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('debería crear el servicio', () => {
    expect(service).toBeTruthy();
  });

  it('debería inicializar con sesión null cuando no hay datos en localStorage', () => {
    service.getSession().subscribe(session => {
      expect(session).toBeNull();
    });
  });

  it('debería establecer una sesión correctamente', () => {
    service.setSession(mockSession);
    
    const storedSession = localStorage.getItem('session');
    expect(storedSession).toBeTruthy();
    expect(JSON.parse(storedSession!)).toEqual(mockSession);
    
    service.getSession().subscribe(session => {
      expect(session).toEqual(mockSession);
    });
  });

  it('debería limpiar la sesión correctamente', () => {
    service.setSession(mockSession);
    service.clearSession();
    
    const storedSession = localStorage.getItem('session');
    expect(storedSession).toBeNull();
    
    service.getSession().subscribe(session => {
      expect(session).toBeNull();
    });
  });

  it('debería retornar true cuando el usuario está logueado', () => {
    service.setSession(mockSession);
    
    service.isLoggedIn().subscribe(isLoggedIn => {
      expect(isLoggedIn).toBe(true);
    });
  });

  it('debería retornar false cuando el usuario no está logueado', () => {
    service.clearSession();
    
    service.isLoggedIn().subscribe(isLoggedIn => {
      expect(isLoggedIn).toBe(false);
    });
  });

  it('debería obtener la sesión actual de forma síncrona', () => {
    service.setSession(mockSession);
    
    const currentSession = service.getCurrentSession();
    expect(currentSession).toEqual(mockSession);
    
    service.clearSession();
    const nullSession = service.getCurrentSession();
    expect(nullSession).toBeNull();
  });
});