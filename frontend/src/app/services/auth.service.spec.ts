import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { Session } from '../models/session';
import { User } from '../models/user';
import { environment } from '../../environments/environment';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;
  const apiUrl = environment.apiBaseUrl;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('debería crear el servicio', () => {
    expect(service).toBeTruthy();
  });

  it('debería autenticar usuario correctamente', () => {
    const mockSession: Session = {
      id: 1,
      ulid: 'session-ulid',
      displayName: 'Juan Pérez',
      email: 'juan@test.com',
      rol: 'ADMIN'
    };
    const email = 'test@test.com';
    const password = 'password123';

    service.authenticate(email, password).subscribe(session => {
      expect(session).toEqual(mockSession);
    });

    const req = httpMock.expectOne(`${apiUrl}/authenticate`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ email, password });
    req.flush(mockSession);
  });

  it('debería realizar logout correctamente', () => {
    service.logout().subscribe(response => {
      expect(response).toBeNull();
    });

    const req = httpMock.expectOne(`${apiUrl}/logout`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({});
    req.flush(null);
  });

  it('debería obtener el usuario actual', () => {
    const mockUser: User = {
      id: 1,
      displayName: 'Test User',
      email: 'test@test.com'
    };

    service.getCurrentUser().subscribe(user => {
      expect(user).toEqual(mockUser);
    });

    const req = httpMock.expectOne(`${apiUrl}/current_user`);
    expect(req.request.method).toBe('GET');
    req.flush(mockUser);
  });

  it('debería refrescar la sesión', () => {
    const mockSession: Session = {
      id: 1,
      ulid: 'session-ulid',
      displayName: 'Juan Pérez',
      email: 'juan@test.com',
      rol: 'WORKER'
    };

    service.refreshSession().subscribe(session => {
      expect(session).toEqual(mockSession);
    });

    const req = httpMock.expectOne(`${apiUrl}/refresh`);
    expect(req.request.method).toBe('GET');
    req.flush(mockSession);
  });

  it('debería solicitar recuperación de contraseña', () => {
    const email = 'test@test.com';
    const mockResponse = { message: 'Email enviado correctamente' };

    service.forgotPassword(email).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${apiUrl}/forgot_password`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ email });
    req.flush(mockResponse);
  });

  it('debería obtener todas las sesiones', () => {
    const mockSessions: Session[] = [
      {
        id: 1,
        ulid: 'session-1',
        displayName: 'Usuario 1',
        email: 'user1@test.com',
        rol: 'ADMIN'
      },
      {
        id: 2,
        ulid: 'session-2',
        displayName: 'Usuario 2',
        email: 'user2@test.com',
        rol: 'WORKER'
      }
    ];

    service.getAll().subscribe(sessions => {
      expect(sessions).toEqual(mockSessions);
      expect(sessions.length).toBe(2);
    });

    const req = httpMock.expectOne(apiUrl);
    expect(req.request.method).toBe('GET');
    req.flush(mockSessions);
  });
});