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
  ) { }

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

        const token = res.token || (res.success && res.token ? res.token : null);

        if (token) {
          // Decodificar el token para verificar el rol
          const decodedToken = this.authService.decodeToken(token);

          if (decodedToken) {
            const rol = decodedToken.rol;
            const email = decodedToken.email || decodedToken.sub;
            const usuarioId = decodedToken.usuarioId;

            console.log('Datos del token:', { rol, email, usuarioId });

            // Verificar si es FUNCIONARIO
            if (rol !== 'FUNCIONARIO') {
              this.error = '⚠️ Página exclusiva para funcionarios';
              this.authService.logout(); // Limpiar cualquier dato previo
              return;
            }

            // Guardar datos en localStorage
            localStorage.setItem('userEmail', email);
            localStorage.setItem('userId', usuarioId);
            localStorage.setItem('userRole', rol);

            // Proceder con el login exitoso
            this.authService.handleLoginSuccess(token, res.user);
          } else {
            this.error = 'Error al procesar la autenticación';
          }
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
