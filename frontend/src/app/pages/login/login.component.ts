import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule], // 👈 AGREGA RouterModule
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  email = '';
  password = '';
  error = '';

  constructor(private authService: AuthService, private router: Router) {}

  login() {
    this.error = '';

    const credenciales = { email: this.email, password: this.password };
    this.authService.login(credenciales).subscribe({
      next: (res: any) => {
        if (res.token) {
          localStorage.setItem('token', res.token);
          this.router.navigate(['/denuncias']);
        } else {
          this.error = 'Respuesta inválida del servidor.';
        }
      },
      error: (err) => {
        this.error = '❌ Credenciales inválidas o error en el servidor.';
        console.error(err);
      }
    });
  }
}
