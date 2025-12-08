import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
    selector: 'app-dashboard',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './dashboard.component.html',
    styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
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

    navigateToDenuncias(): void {
        this.router.navigate(['/denuncias']);
    }
}
