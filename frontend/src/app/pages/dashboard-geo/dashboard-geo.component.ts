import { Component, OnInit, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { DenunciaService } from '../../services/denuncia';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

@Component({
    selector: 'app-dashboard-geo',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './dashboard-geo.component.html',
    styleUrl: './dashboard-geo.component.scss'
})
export class DashboardGeoComponent implements OnInit, AfterViewInit {
    stats: any = null;
    isLoading: boolean = true;

    @ViewChild('chartComuna') chartComunaRef: ElementRef | undefined;
    @ViewChild('chartSector') chartSectorRef: ElementRef | undefined;

    constructor(
        private denunciaService: DenunciaService,
        private router: Router
    ) { }

    ngOnInit(): void {
        this.loadStats();
    }

    ngAfterViewInit(): void {
    }

    loadStats(): void {
        this.denunciaService.getEstadisticasAvanzadas().subscribe({
            next: (response) => {
                if (response.success) {
                    this.stats = response.estadisticas;
                    this.isLoading = false;
                    setTimeout(() => this.renderCharts(), 100);
                }
            },
            error: (err) => {
                console.error('Error loading stats', err);
                this.isLoading = false;
            }
        });
    }

    renderCharts(): void {
        if (!this.stats) return;
        this.renderChartComuna();
        this.renderChartSector();
    }

    renderChartComuna(): void {
        if (!this.chartComunaRef || !this.stats.denunciasPorComuna) return;

        const ctx = this.chartComunaRef.nativeElement.getContext('2d');
        const labels = Object.keys(this.stats.denunciasPorComuna);
        const data = Object.values(this.stats.denunciasPorComuna);
        const colors = this.generateColors(labels.length);

        new Chart(ctx, {
            type: 'bar', // Using Bar for clarity if many comunas
            data: {
                labels: labels,
                datasets: [{
                    label: 'Denuncias por Comuna',
                    data: data,
                    backgroundColor: colors,
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                indexAxis: 'y' // Horizontal bar chart for readable labels
            }
        });
    }

    renderChartSector(): void {
        if (!this.chartSectorRef || !this.stats.denunciasPorSector) return;

        const ctx = this.chartSectorRef.nativeElement.getContext('2d');
        const labels = Object.keys(this.stats.denunciasPorSector);
        const data = Object.values(this.stats.denunciasPorSector);
        const colors = this.generateColors(labels.length);

        new Chart(ctx, {
            type: 'pie',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Denuncias por Sector (Temuco)',
                    data: data,
                    backgroundColor: colors,
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false
            }
        });
    }

    generateColors(count: number): string[] {
        const colors = [];
        for (let i = 0; i < count; i++) {
            colors.push(`hsl(${(i * 360) / count}, 70%, 60%)`);
        }
        return colors;
    }

    goBack(): void {
        this.router.navigate(['/dashboard']);
    }
}
