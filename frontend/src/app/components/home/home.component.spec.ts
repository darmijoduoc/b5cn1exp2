import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { BehaviorSubject, of } from 'rxjs';
import { HomeComponent } from './home.component';
import { SessionService } from '../../services/session.service';
import { Session } from '../../models/session';

describe('HomeComponent', () => {
  let component: HomeComponent;
  let fixture: ComponentFixture<HomeComponent>;
  let sessionServiceSpy: jasmine.SpyObj<SessionService>;
  let router: Router;
  let isLoggedInSubject: BehaviorSubject<boolean>;
  let sessionSubject: BehaviorSubject<Session | null>;

  const mockSession: Session = {
    id: 1,
    ulid: 'test-ulid',
    displayName: 'Usuario Test',
    email: 'test@example.com',
    rol: 'WORKER'
  };

  beforeEach(() => {
    isLoggedInSubject = new BehaviorSubject<boolean>(false);
    sessionSubject = new BehaviorSubject<Session | null>(null);

    const sessionSpy = jasmine.createSpyObj('SessionService', ['isLoggedIn', 'getSession', 'clearSession']);

    sessionSpy.isLoggedIn.and.returnValue(isLoggedInSubject.asObservable());
    sessionSpy.getSession.and.returnValue(sessionSubject.asObservable());

    TestBed.configureTestingModule({
      imports: [HomeComponent, RouterTestingModule],
      providers: [
        { provide: SessionService, useValue: sessionSpy }
      ]
    });

    fixture = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;
    sessionServiceSpy = TestBed.inject(SessionService) as jasmine.SpyObj<SessionService>;
    router = TestBed.inject(Router);
  });

  it('debería crear el componente', () => {
    expect(component).toBeTruthy();
  });

  it('debería inicializar con valores por defecto', () => {
    expect(component.loggedIn).toBe(false);
    expect(component.session).toBeNull();
  });

  it('debería verificar el estado del usuario al inicializar', () => {
    component.ngOnInit();
    
    expect(sessionServiceSpy.isLoggedIn).toHaveBeenCalled();
  });

  it('debería actualizar el estado cuando el usuario está logueado', () => {
    component.ngOnInit();
    
    // Simular que el usuario se loguea
    isLoggedInSubject.next(true);
    sessionSubject.next(mockSession);
    
    expect(component.loggedIn).toBe(true);
    expect(component.session).toEqual(mockSession);
    expect(sessionServiceSpy.getSession).toHaveBeenCalled();
  });

  it('debería mostrar mensaje de bienvenida cuando está logueado', () => {
    component.ngOnInit();
    
    // Simular que el usuario está logueado
    isLoggedInSubject.next(true);
    sessionSubject.next(mockSession);
    fixture.detectChanges();

    const welcomeMessage = fixture.nativeElement.querySelector('h2');
    const userGreeting = fixture.nativeElement.querySelector('p');
    
    expect(welcomeMessage.textContent).toBe('¡Bienvenido!');
    expect(userGreeting.textContent.trim()).toBe('Hola Usuario Test');
  });

  it('debería mostrar enlace de login cuando no está logueado', () => {
    component.ngOnInit();
    
    // Simular que el usuario NO está logueado
    isLoggedInSubject.next(false);
    sessionSubject.next(null);
    fixture.detectChanges();

    const loginLink = fixture.nativeElement.querySelector('a[routerLink="/login"]');
    const welcomeTitle = fixture.nativeElement.querySelector('h2');
    
    expect(loginLink).toBeTruthy();
    expect(loginLink.textContent.trim()).toBe('Iniciar Sesión');
    expect(welcomeTitle.textContent).toBe('Bienvenido al Sistema');
  });

  it('debería cerrar sesión y navegar al home', () => {
    spyOn(router, 'navigate');
    
    component.logout();
    
    expect(sessionServiceSpy.clearSession).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/']);
  });
});
