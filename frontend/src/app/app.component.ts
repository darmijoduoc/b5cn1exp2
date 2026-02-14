import { Component, OnInit, OnDestroy, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLinkActive, RouterModule, RouterOutlet } from '@angular/router';
import { SessionService } from './services/session.service';
import { Session } from './models/session';
import {
  MsalService,
  MsalBroadcastService,
  MSAL_GUARD_CONFIG,
  MsalGuardConfiguration,
} from '@azure/msal-angular';
import {
  AuthenticationResult,
  InteractionStatus,
  PopupRequest,
  RedirectRequest,
  EventMessage,
  EventType,
} from '@azure/msal-browser';
import { Subject } from 'rxjs';
import { filter, takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, CommonModule, RouterModule, RouterLinkActive],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit, OnDestroy {
  
  title = 'labs-angular';
  loading = true;
  error = '';

  loggedIn = false;
  session: Session | null = null;
  rol: 'ADMIN' | 'WORKER' | null = null;

  private readonly _destroying$ = new Subject<void>();

  constructor(
    @Inject(MSAL_GUARD_CONFIG) private msalGuardConfig: MsalGuardConfiguration,
    private authService: MsalService,
    private msalBroadcastService: MsalBroadcastService,
    private sessionService: SessionService
  ) {}


  ngOnInit(): void {
    this.authService.handleRedirectObservable().subscribe();
    
    this.sessionService.isLoggedIn().subscribe(loggedIn => {
      this.loggedIn = loggedIn;
      this.sessionService.getSession().subscribe(session => this.session = session);
    });

    this.authService.instance.enableAccountStorageEvents();
    this.msalBroadcastService.msalSubject$
      .pipe(
        filter(
          (msg: EventMessage) =>
            msg.eventType === EventType.ACCOUNT_ADDED ||
            msg.eventType === EventType.ACCOUNT_REMOVED
        )
      )
      .subscribe((result: EventMessage) => {
        if (this.authService.instance.getAllAccounts().length === 0) {
          this.sessionService.clearSession();
        } else {
          this.setLoginDisplay();
        }
      });

    this.msalBroadcastService.inProgress$
      .pipe(
        filter(
          (status: InteractionStatus) => status === InteractionStatus.None
        ),
        takeUntil(this._destroying$)
      )
      .subscribe(() => {
        this.setLoginDisplay();
        this.checkAndSetActiveAccount();
      });
  }

  setLoginDisplay() {
    const accounts = this.authService.instance.getAllAccounts();
    this.loggedIn = accounts.length > 0;
    console.log('>> [DEBUG] setLoginDisplay - loggedIn:', this.loggedIn, 'accounts:', accounts.length);

    if (this.loggedIn) {
      const account = accounts[0];
      
      // Actualizar sesión si no existe
      if (!this.session) {
        this.sessionService.setSession({
          id: 1,
          ulid: '01ARZ3NDEKTSV4RRFFQ69G5FAV',
          displayName: account.name || account.username,
          email: account.username,
          rol: 'ADMIN'
        });
      }

      // Obtener y guardar el token SIEMPRE que estemos logueados
      this.authService.acquireTokenSilent({ 
        scopes: ['User.Read'],
        account: account 
      }).subscribe({
        next: (tokenResponse) => {
          console.log('>> [DEBUG] ID Token recibido:', tokenResponse.idToken);
          localStorage.setItem('jwt', tokenResponse.idToken);
          console.log('>> [DEBUG] Token guardado en localStorage bajo la clave "jwt"');
        },
        error: (error) => {
          console.error('>> [DEBUG] Error obteniendo el token silenciosamente:', error);
          // Si falla silent, podríamos intentar acquireTokenPopup pero por ahora logueamos el error
        }
      });
    } else {
      if (this.session) {
        console.log('>> [DEBUG] No hay cuentas, limpiando datos locales...');
        this.clearLocalData();
      }
    }
  }

  checkAndSetActiveAccount() {
    let activeAccount = this.authService.instance.getActiveAccount();

    if (
      !activeAccount &&
      this.authService.instance.getAllAccounts().length > 0
    ) {
      let accounts = this.authService.instance.getAllAccounts();
      this.authService.instance.setActiveAccount(accounts[0]);
    }
  }

  loginPopup() {
    console.log('>> [DEBUG] Iniciando loginPopup...');
    const loginRequest = this.msalGuardConfig.authRequest ? 
      { ...this.msalGuardConfig.authRequest } as PopupRequest : 
      { scopes: ['User.Read'] } as PopupRequest;

    this.authService.loginPopup(loginRequest)
      .subscribe({
        next: (response: AuthenticationResult) => {
          console.log('>> [DEBUG] loginPopup exitoso:', response);
          console.log('>> [DEBUG] ID Token de la respuesta:', response.idToken);
          this.authService.instance.setActiveAccount(response.account);
          this.setLoginDisplay();
        },
        error: (error) => {
          console.error('>> [DEBUG] Error en loginPopup:', error);
        }
      });
  }

  logout() {
    this.clearLocalData();
  }

  private clearLocalData() {
    this.sessionService.clearSession();
    localStorage.removeItem('jwt');
    localStorage.clear(); // Limpieza profunda
    this.loggedIn = false;
    this.session = null;
  }

  isAdmin(): boolean {
    return this.session?.rol === 'ADMIN';
  }

  isWorker(): boolean {
    return this.session?.rol === 'WORKER';
  }

  ngOnDestroy(): void {
    this._destroying$.next(undefined);
    this._destroying$.complete();
  }
}
