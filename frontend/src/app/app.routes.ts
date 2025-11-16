import { Routes } from '@angular/router';
import { ListaDenunciasComponent } from './pages/lista-denuncias/lista-denuncias.component';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: '', redirectTo: 'denuncias', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'registro', component: RegisterComponent },
  { path: 'denuncias', component: ListaDenunciasComponent },
];

