import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
    selector: 'app-sidebar',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './sidebar.component.html',
    styleUrl: './sidebar.component.scss'
})
export class SidebarComponent {
    menuItems = [
        {
            label: 'Dashboard',
            icon: '📊',
            route: '/dashboard'
        },
        {
            label: 'Denuncias',
            icon: '📋',
            route: '/denuncias'
        }
    ];

    constructor(
        private authService: AuthService,
        private router: Router
    ) { }

    logout(): void {
        if (confirm('¿Estás seguro de que deseas cerrar sesión?')) {
            this.authService.logout();
        }
    }

    isActive(route: string): boolean {
        return this.router.url === route;
    }
}
