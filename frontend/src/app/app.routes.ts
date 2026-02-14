// ======================================================================
// Semana 3 - Configuración inicial de rutas (app.routes.ts)
// ======================================================================
//
// Angular 17 usa arquitectura standalone, así que las rutas se definen
// en este arreglo sin necesidad de NgModules.
// se conectan en app.config.ts
// con provideRouter(routes).
//
// Cada entrada indica:
//   path: URL relativa (por ejemplo 'libros')
//   component: componente standalone a mostrar
//
// El sistema SPA (Single Page Application) de Angular usará <router-outlet>
// para cargar estos componentes dinámicamente.
// Rutas definidas:
//   /libros           → Listado (GET todos)
//   /libros/nuevo     → Crear (POST)
//   /libros/editar/:id→ Editar (PUT) usando el mismo form
//   /libros/:id       → Detalle (GET por ID) para visibilidad del requisito
//   '' → redirección inicial a /libros
// ======================================================================

import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { HomeComponent } from './components/home/home.component';
import { MonitoringComponent } from './components/monitoring/monitoring.component';
import { TransportManagementComponent } from './components/management/transport-management.component';
import { MsalGuard } from '@azure/msal-angular';


export const routes: Routes = [
    {
        path: '',
        component: HomeComponent,
        pathMatch: 'full'
    },
    {
        path: 'login',
        component: LoginComponent,
    },
    {
        path: 'monitoring',
        component: MonitoringComponent,
        canActivate: [MsalGuard]
    },
    {
        path: 'management',
        component: TransportManagementComponent,
        canActivate: [MsalGuard]
    },
    { path: '**', redirectTo: '/' }


];
