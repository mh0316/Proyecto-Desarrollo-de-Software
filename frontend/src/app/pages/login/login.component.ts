import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  email = '';
  password = '';
  error = '';
  loading = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  login() {
    this.error = '';
    this.loading = true;

    // Validación básica
    if (!this.email || !this.password) {
      this.error = 'Por favor ingresa email y contraseña';
      this.loading = false;
      return;
    }

    const credenciales = {
      email: this.email,
      password: this.password
    };

    this.authService.login(credenciales).subscribe({
      next: (res: any) => {
        this.loading = false;

        if (res.token) {
          // Usar el nuevo método del auth service
          this.authService.handleLoginSuccess(res.token, res.user);
        } else if (res.success && res.token) {
          // Por si la respuesta tiene estructura { success: true, token: '...' }
          this.authService.handleLoginSuccess(res.token, res.user);
        } else {
          this.error = 'Respuesta inválida del servidor - no se recibió token';
        }
      },
      error: (err) => {
        this.loading = false;
        console.error('Error en login:', err);

        if (err.status === 401) {
          this.error = '❌ Credenciales inválidas';
        } else if (err.status === 404) {
          this.error = '❌ Servicio no disponible';
        } else {
          this.error = '❌ Error en el servidor. Intenta nuevamente.';
        }
      }
    });
  }
}
