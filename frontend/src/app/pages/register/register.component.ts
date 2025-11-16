import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  nombre = '';
  apellido = '';
  email = '';
  password = '';
  telefono = '';
  rut = '';

  mensaje = '';
  error = '';

  constructor(private authService: AuthService, private router: Router) {}

  registrar() {
    this.error = '';
    this.mensaje = '';

    const nuevoUsuario = {
      nombre: this.nombre,
      apellido: this.apellido,
      email: this.email,
      password: this.password,
      telefono: this.telefono,
      rut: this.rut
    };

    this.authService.register(nuevoUsuario).subscribe({
      next: () => {
        this.mensaje = '✅ Registro exitoso. Redirigiendo al inicio de sesión...';
        setTimeout(() => this.router.navigate(['/login']), 1500);
      },
      error: (err) => {
        this.error = '❌ Error al registrar. Verifica los datos.';
        console.error(err);
      }
    });
  }
}
