import { Routes } from '@angular/router';
import { ListaDenunciasComponent } from './pages/lista-denuncias/lista-denuncias.component';
import { DenunciaDetalleComponent } from './pages/denuncia-detalle/denuncia-detalle';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { DashboardGeoComponent } from './pages/dashboard-geo/dashboard-geo.component';
import { DashboardReportesComponent } from './pages/dashboard-reportes/dashboard-reportes.component';
import { AuthGuard } from './services/auth.guard'; // 👈 Importar el Guard

export const routes: Routes = [
  // Redirección principal
  { path: '', redirectTo: '/login', pathMatch: 'full' },

  // Rutas públicas
  { path: 'login', component: LoginComponent },
  { path: 'registro', component: RegisterComponent },

  // Rutas protegidas
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'dashboard/geo',
    component: DashboardGeoComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'dashboard/reportes',
    component: DashboardReportesComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'denuncias',
    component: ListaDenunciasComponent,
    canActivate: [AuthGuard] // 👈 Proteger esta ruta
  },
  {
    path: 'denuncias/:id',
    component: DenunciaDetalleComponent,
    canActivate: [AuthGuard]
  },

  // Ruta comodín (siempre al final)
  { path: '**', redirectTo: '/login' }
];

