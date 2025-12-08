import { Component, OnInit, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { DenunciaService } from '../../services/denuncia'; // Adjust path if needed
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

@Component({
    selector: 'app-dashboard',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './dashboard.component.html',
    styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit, AfterViewInit {
    userName: string = '';
    stats: any = null;
    isLoadingTests: boolean = true;

    // References to canvas elements
    @ViewChild('chartMensual') chartMensualRef: ElementRef | undefined;
    @ViewChild('chartCategoria') chartCategoriaRef: ElementRef | undefined;
    @ViewChild('chartTasa') chartTasaRef: ElementRef | undefined;
    @ViewChild('chartHorario') chartHorarioRef: ElementRef | undefined;

    constructor(
        private authService: AuthService,
        private denunciaService: DenunciaService,
        private router: Router
    ) { }

    ngOnInit(): void {
        this.loadUserName();
        this.loadStats();
    }

    ngAfterViewInit(): void {
        // Charts will be rendered after data is loaded
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

    loadStats(): void {
        this.denunciaService.getEstadisticasAvanzadas().subscribe({
            next: (response) => {
                if (response.success) {
                    this.stats = response.estadisticas;
                    this.isLoadingTests = false;
                    // Provide a small delay to ensure DOM is ready
                    setTimeout(() => this.renderCharts(), 100);
                }
            },
            error: (err) => {
                console.error('Error loading stats', err);
                this.isLoadingTests = false;
            }
        });
    }

    renderCharts(): void {
        if (!this.stats) return;

        this.renderChartMensual();
        this.renderChartCategoria();
        this.renderChartTasa();
        this.renderChartHorario();
    }

    renderChartMensual(): void {
        if (!this.chartMensualRef) return;

        const ctx = this.chartMensualRef.nativeElement.getContext('2d');
        const labels = Object.keys(this.stats.denunciasPorMes);
        const data = Object.values(this.stats.denunciasPorMes);

        new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Denuncias por Mes',
                    data: data,
                    borderColor: '#3b82f6',
                    backgroundColor: 'rgba(59, 130, 246, 0.1)',
                    tension: 0.4,
                    fill: true
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false
            }
        });
    }

    renderChartCategoria(): void {
        if (!this.chartCategoriaRef) return;

        const ctx = this.chartCategoriaRef.nativeElement.getContext('2d');
        const labels = Object.keys(this.stats.denunciasPorCategoria);
        const data = Object.values(this.stats.denunciasPorCategoria);
        // Colors for diverse categories
        const colors = ['#f59e0b', '#ef4444', '#10b981', '#3b82f6', '#8b5cf6', '#ec4899'];

        new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: labels,
                datasets: [{
                    data: data,
                    backgroundColor: colors,
                    hoverOffset: 4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false
            }
        });
    }

    renderChartTasa(): void {
        if (!this.chartTasaRef) return;

        const ctx = this.chartTasaRef.nativeElement.getContext('2d');

        new Chart(ctx, {
            type: 'pie',
            data: {
                labels: ['Validación', 'Rechazo'],
                datasets: [{
                    data: [this.stats.tasaValidacion, this.stats.tasaRechazo],
                    backgroundColor: ['#10b981', '#ef4444'],
                    hoverOffset: 4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false
            }
        });
    }

    renderChartHorario(): void {
        if (!this.chartHorarioRef) return;

        const ctx = this.chartHorarioRef.nativeElement.getContext('2d');
        // Pre-fill 0-23 hours
        const labels = Array.from({ length: 24 }, (_, i) => `${i}:00`);
        const data = labels.map((_, i) => this.stats.denunciasPorHorario[i] || 0);

        new Chart(ctx, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Denuncias por Hora',
                    data: data,
                    backgroundColor: '#6366f1',
                    borderRadius: 4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        });
    }

    navigateToDenuncias(): void {
        this.router.navigate(['/denuncias']);
    }

    navigateToGeoStats(): void {
        this.router.navigate(['/dashboard/geo']);
    }

    navigateToReportes(): void {
        this.router.navigate(['/dashboard/reportes']);
    }
}
