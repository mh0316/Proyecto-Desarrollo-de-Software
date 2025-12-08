import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { DenunciaService } from '../../services/denuncia';

@Component({
    selector: 'app-dashboard-reportes',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './dashboard-reportes.component.html',
    styleUrl: './dashboard-reportes.component.scss'
})
export class DashboardReportesComponent implements OnInit {
    stats: any = null;
    isLoading: boolean = true;

    // Helper arrays for iteration
    topUsuariosList: any[] = [];
    reincidenciaList: any[] = [];

    constructor(
        private denunciaService: DenunciaService,
        private router: Router
    ) { }

    ngOnInit(): void {
        this.loadStats();
    }

    loadStats(): void {
        this.denunciaService.getEstadisticasAvanzadas().subscribe({
            next: (response) => {
                if (response.success) {
                    this.stats = response.estadisticas;
                    this.processData();
                    this.isLoading = false;
                }
            },
            error: (err) => {
                console.error('Error loading stats', err);
                this.isLoading = false;
            }
        });
    }

    processData(): void {
        if (this.stats.topUsuarios) {
            this.topUsuariosList = Object.entries(this.stats.topUsuarios)
                .map(([key, value]) => ({ usuario: key, cantidad: value }));
        }

        if (this.stats.reincidenciaPatentes) {
            this.reincidenciaList = Object.entries(this.stats.reincidenciaPatentes)
                .map(([key, value]) => ({ patente: key, cantidad: value }));
        }
    }

    goBack(): void {
        this.router.navigate(['/dashboard']);
    }
}
