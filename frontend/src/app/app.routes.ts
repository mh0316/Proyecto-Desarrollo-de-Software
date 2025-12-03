import { Routes } from '@angular/router';
import { ListaDenunciasComponent } from './pages/lista-denuncias/lista-denuncias.component';
import { DenunciaDetalleComponent } from './pages/denuncia-detalle/denuncia-detalle';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { AuthGuard } from './services/auth.guard'; // 👈 Importar el Guard

export const routes: Routes = [
  // Redirección principal
  { path: '', redirectTo: '/login', pathMatch: 'full' },

  // Rutas públicas
  { path: 'login', component: LoginComponent },
  { path: 'registro', component: RegisterComponent },

  // Rutas protegidas
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
