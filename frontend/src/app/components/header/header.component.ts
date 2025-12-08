import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
    selector: 'app-header',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './header.component.html',
    styleUrl: './header.component.scss'
})
export class HeaderComponent implements OnInit {
    userName: string = '';

    constructor(
        private authService: AuthService,
        private router: Router
    ) { }

    ngOnInit(): void {
        this.loadUserName();
    }

    loadUserName(): void {
        const token = this.authService.getToken();
        if (token) {
            const decodedToken = this.authService.decodeToken(token);
            if (decodedToken) {
                const nombre = decodedToken.nombre || '';
                const apellido = decodedToken.apellido || '';
                this.userName = `${nombre} ${apellido}`.trim();
            }
        }
    }

    logout(): void {
        if (confirm('¿Estás seguro de que deseas cerrar sesión?')) {
            this.authService.logout();
        }
    }
}
