import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { BehaviorSubject, of } from 'rxjs';
import { AppComponent } from './app.component';
import { SessionService } from './services/session.service';
import { Session } from './models/session';

describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: any;
  let sessionServiceSpy: jasmine.SpyObj<SessionService>;
  let isLoggedInSubject: BehaviorSubject<boolean>;
  let sessionSubject: BehaviorSubject<Session | null>;

  const mockAdminSession: Session = {
    id: 1,
    ulid: 'admin-ulid-123',
    displayName: 'Usuario Administrador',
    email: 'admin@example.com',
    rol: 'ADMIN'
  };

  const mockWorkerSession: Session = {
    id: 2,
    ulid: 'worker-ulid-456',
    displayName: 'Usuario Trabajador',
    email: 'worker@example.com',
    rol: 'WORKER'
  };

  beforeEach(async () => {
    isLoggedInSubject = new BehaviorSubject<boolean>(false);
    sessionSubject = new BehaviorSubject<Session | null>(null);

    const sessionSpy = jasmine.createSpyObj('SessionService', ['isLoggedIn', 'getSession']);
    sessionSpy.isLoggedIn.and.returnValue(isLoggedInSubject.asObservable());
    sessionSpy.getSession.and.returnValue(sessionSubject.asObservable());

    await TestBed.configureTestingModule({
      imports: [AppComponent, RouterTestingModule],
      providers: [
        { provide: SessionService, useValue: sessionSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
    sessionServiceSpy = TestBed.inject(SessionService) as jasmine.SpyObj<SessionService>;
  });

  // Test 1: Creación e Inicialización del Componente
  it('debería crear la aplicación e inicializar con valores por defecto', () => {
    expect(component).toBeTruthy();
    expect(component.title).toBe('labs-angular');
    expect(component.loading).toBe(true);
    expect(component.error).toBe('');
    expect(component.loggedIn).toBe(false);
    expect(component.session).toBeNull();
    expect(component.rol).toBeNull();
  });

  // Test 2: Gestión de Estado de Sesión en Inicialización
  it('debería suscribirse al estado de sesión y actualizar propiedades del componente en ngOnInit', () => {
    // Inicialmente no logueado
    component.ngOnInit();
    fixture.detectChanges();

    expect(component.loggedIn).toBe(false);
    expect(component.session).toBeNull();

    // Simular login de usuario
    isLoggedInSubject.next(true);
    sessionSubject.next(mockAdminSession);
    fixture.detectChanges();

    expect(component.loggedIn).toBe(true);
    expect(component.session).toEqual(mockAdminSession);
    expect(sessionServiceSpy.isLoggedIn).toHaveBeenCalled();
    expect(sessionServiceSpy.getSession).toHaveBeenCalled();
  });

  // Test 3: Detección de Rol de Administrador
  it('debería identificar correctamente usuarios administradores', () => {
    component.session = mockAdminSession;
    expect(component.isAdmin()).toBe(true);

    component.session = mockWorkerSession;
    expect(component.isAdmin()).toBe(false);

    component.session = null;
    expect(component.isAdmin()).toBe(false);
  });

  // Test 4: Detección de Rol de Trabajador
  it('debería identificar correctamente usuarios trabajadores', () => {
    component.session = mockWorkerSession;
    expect(component.isWorker()).toBe(true);

    component.session = mockAdminSession;
    expect(component.isWorker()).toBe(false);

    component.session = null;
    expect(component.isWorker()).toBe(false);
  });



  // Test 5: Navegación de Login para Usuarios No Autenticados
  it('debería mostrar enlace de login cuando el usuario no está logueado', () => {
    component.loggedIn = false;
    component.session = null;
    fixture.detectChanges();

    const loginLink = fixture.nativeElement.querySelector('a[routerLink="/login"]');
    expect(loginLink).toBeTruthy();
    expect(loginLink.textContent.trim()).toBe('Iniciar Sesión');

    // Verificar que no se muestra navegación autenticada
    expect(component.loggedIn).toBe(false);
  });
});
