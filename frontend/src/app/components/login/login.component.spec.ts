import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import { of, throwError } from 'rxjs';
import { LoginComponent } from './login.component';
import { AuthService } from '../../services/auth.service';
import { SessionService } from '../../services/session.service';
import { Session } from '../../models/session';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let sessionServiceSpy: jasmine.SpyObj<SessionService>;
  let routerSpy: jasmine.SpyObj<Router>;

  const mockSession: Session = {
    id: 1,
    ulid: 'session-ulid-123',
    displayName: 'Usuario Prueba',
    email: 'test@example.com',
    rol: 'ADMIN'
  };

  beforeEach(async () => {
    const authSpy = jasmine.createSpyObj('AuthService', ['authenticate', 'forgotPassword']);
    const sessionSpy = jasmine.createSpyObj('SessionService', ['setSession']);
    const routerSpyObj = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [LoginComponent, ReactiveFormsModule],
      providers: [
        { provide: AuthService, useValue: authSpy },
        { provide: SessionService, useValue: sessionSpy },
        { provide: Router, useValue: routerSpyObj },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: { params: {} },
            queryParams: of({})
          }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    authServiceSpy = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    sessionServiceSpy = TestBed.inject(SessionService) as jasmine.SpyObj<SessionService>;
    routerSpy = TestBed.inject(Router) as jasmine.SpyObj<Router>;

    spyOn(window, 'alert');
    fixture.detectChanges();
  });

  it('debería crear el componente', () => {
    expect(component).toBeTruthy();
  });

  it('debería inicializar el formulario de login', () => {
    expect(component.form).toBeDefined();
    expect(component.form.get('email')?.value).toBe('da@localhost');
    expect(component.form.get('password')?.value).toBe('p4ssw0rD!');
  });

  it('debería hacer login exitoso', () => {
    authServiceSpy.authenticate.and.returnValue(of(mockSession));

    component.form.patchValue({
      email: 'test@example.com',
      password: 'password123'
    });

    component.onSubmit();

    expect(authServiceSpy.authenticate).toHaveBeenCalledWith('test@example.com', 'password123');
    expect(sessionServiceSpy.setSession).toHaveBeenCalledWith(mockSession);
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/']);
  });

  it('debería mostrar alerta cuando falla el login', () => {
    authServiceSpy.authenticate.and.returnValue(throwError(() => new Error('Login failed')));

    component.onSubmit();

    expect(window.alert).toHaveBeenCalledWith('Credenciales inválidas. Intente nuevamente.');
  });

  it('debería abrir el modal de recuperar contraseña', () => {
    component.openForgotPasswordModal();
    expect(component.showForgotPasswordModal).toBeTruthy();
  });

  it('debería cerrar el modal de recuperar contraseña', () => {
    component.closeForgotPasswordModal();
    expect(component.showForgotPasswordModal).toBeFalsy();
  });

  it('debería enviar email de recuperación', () => {
    authServiceSpy.forgotPassword.and.returnValue(of({ message: 'Email enviado' }));
    
    component.forgotPasswordForm.patchValue({ email: 'test@example.com' });
    component.onForgotPasswordSubmit();

    expect(authServiceSpy.forgotPassword).toHaveBeenCalledWith('test@example.com');
    expect(window.alert).toHaveBeenCalledWith('Se ha enviado un enlace de recuperación a su correo electrónico.');
  });
});
