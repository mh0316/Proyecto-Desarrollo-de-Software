import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { DenunciaService } from '../../services/denuncia';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-lista-denuncias',
  standalone: true,
  imports: [CommonModule, HttpClientModule, FormsModule],
  templateUrl: './lista-denuncias.component.html',
  styleUrl: './lista-denuncias.component.scss'
})
export class ListaDenunciasComponent implements OnInit {

  denuncias: any[] = [];
  denunciasFiltradas: any[] = [];

  cargando = true;
  error = '';

  // filtros
  filtroEstado = '';
  filtroComuna = '';
  ordenFecha = 'desc';

  constructor(private denunciaService: DenunciaService,
              private router: Router) { }

  ngOnInit(): void {
    this.obtenerDenuncias();
  }

  obtenerDenuncias(): void {
    this.cargando = true;
    this.error = '';

    this.denunciaService.getAll().subscribe({
      next: (response) => {

        if (response && response.denuncias) {
          this.denuncias = response.denuncias;
        } else if (Array.isArray(response)) {
          this.denuncias = response;
        } else {
          this.denuncias = [];
        }

        this.aplicarFiltros();
        this.cargando = false;
      },
      error: (err) => {
        console.error('Error al obtener denuncias:', err);
        this.error = 'Error al cargar las denuncias. Por favor, intenta nuevamente.';
        this.cargando = false;
        this.denuncias = [];
        this.denunciasFiltradas = [];
      }
    });
  }

  aplicarFiltros(): void {
    let resultado = [...this.denuncias];

    if (this.filtroEstado) {
      resultado = resultado.filter(d => d.estado === this.filtroEstado);
    }

    if (this.filtroComuna.trim() !== '') {
      const texto = this.filtroComuna.toLowerCase();
      resultado = resultado.filter(d => d.comuna.toLowerCase().includes(texto));
    }

    // ordenar por fecha si existe el campo d.fecha
    resultado.sort((a, b) => {
      const fa = new Date(a.fecha).getTime();
      const fb = new Date(b.fecha).getTime();
      return this.ordenFecha === 'desc' ? fb - fa : fa - fb;
    });

    this.denunciasFiltradas = resultado;
  }

  limpiarFiltros(): void {
    this.filtroEstado = '';
    this.filtroComuna = '';
    this.ordenFecha = 'desc';
    this.aplicarFiltros();
  }

  verDetalle(id: number): void {
    this.router.navigate(['/denuncias', id]);
  }

  cambiarEstado(id: number, estado: string): void {
    if (!confirm(`¿Estás seguro de cambiar el estado a ${estado}?`)) {
      return;
    }

    this.denunciaService.cambiarEstado(id, estado).subscribe({
      next: (response) => {
        alert(`Denuncia #${id} actualizada a ${estado}`);
        this.obtenerDenuncias();
      },
      error: (err) => {
        alert('Error al cambiar el estado de la denuncia');
      }
    });
  }
}
