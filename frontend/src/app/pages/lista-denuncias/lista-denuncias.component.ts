import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { DenunciaService } from '../../services/denuncia';
import { HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-lista-denuncias',
  standalone: true,
  imports: [CommonModule, HttpClientModule, FormsModule, ReactiveFormsModule],
  templateUrl: './lista-denuncias.component.html',
  styleUrl: './lista-denuncias.component.scss'
})
export class ListaDenunciasComponent implements OnInit {
  denuncias: any[] = [];
  denunciasOriginales: any[] = []; // Guardamos las denuncias sin filtrar
  cargando = true;
  error = '';

  // Formulario de filtros
  filtrosForm: FormGroup;
  estados: string[] = ['PENDIENTE', 'VALIDADA', 'RECHAZADA'];

  constructor(
    private denunciaService: DenunciaService,
    private router: Router,
    private fb: FormBuilder
  ) {
    // Inicializar el formulario de filtros
    this.filtrosForm = this.fb.group({
      estado: [''],
      patente: [''],
      comuna: [''],
      ordenFecha: ['recientes']
    });
  }

  ngOnInit(): void {
    this.obtenerDenuncias();
  }

  obtenerDenuncias(): void {
    this.cargando = true;
    this.error = '';

    this.denunciaService.getAll().subscribe({
      next: (response) => {
        console.log('Respuesta completa de la API:', response);

        if (response && response.denuncias) {
          this.denunciasOriginales = response.denuncias;
        } else if (Array.isArray(response)) {
          this.denunciasOriginales = response;
        } else {
          this.denunciasOriginales = [];
        }

        // Aplicar filtros después de cargar
        this.aplicarFiltros();
        this.cargando = false;
        console.log('Denuncias cargadas:', this.denuncias);
      },
      error: (err) => {
        console.error('Error al obtener denuncias:', err);
        this.error = 'Error al cargar las denuncias. Por favor, intenta nuevamente.';
        this.cargando = false;
        this.denuncias = [];
        this.denunciasOriginales = [];
      }
    });
  }

  aplicarFiltros(): void {
    const filtros = this.filtrosForm.value;
    let resultado = [...this.denunciasOriginales];

    // Filtrar por estado
    if (filtros.estado) {
      resultado = resultado.filter(d => d.estado === filtros.estado);
    }

    // Filtrar por patente (búsqueda parcial, case-insensitive)
    if (filtros.patente && filtros.patente.trim() !== '') {
      const patenteBusqueda = filtros.patente.toLowerCase(). trim();
      resultado = resultado. filter(d =>
        d.patente && d.patente.toLowerCase().includes(patenteBusqueda)
      );
    }

    // Filtrar por comuna (búsqueda parcial, case-insensitive)
    if (filtros.comuna && filtros.comuna.trim() !== '') {
      const comunaBusqueda = filtros.comuna.toLowerCase().trim();
      resultado = resultado.filter(d =>
        d.comuna && d.comuna. toLowerCase().includes(comunaBusqueda)
      );
    }

    // Ordenar por fecha
    if (filtros.ordenFecha === 'recientes') {
      resultado. sort((a, b) => {
        const fechaA = new Date(a.fechaCreacion || a.id || 0);
        const fechaB = new Date(b. fechaCreacion || b.id || 0);
        return fechaB.getTime() - fechaA.getTime();
      });
    } else if (filtros.ordenFecha === 'antiguos') {
      resultado.sort((a, b) => {
        const fechaA = new Date(a.fechaCreacion || a.id || 0);
        const fechaB = new Date(b.fechaCreacion || b.id || 0);
        return fechaA.getTime() - fechaB.getTime();
      });
    }

    this.denuncias = resultado;
    console.log('Filtros aplicados.  Resultados:', this.denuncias. length);
  }

  limpiarFiltros(): void {
    this.filtrosForm. reset({
      estado: '',
      patente: '',
      comuna: '',
      ordenFecha: 'recientes'
    });
    this.aplicarFiltros();
  }

  verDetalle(id: number): void {
    this. router.navigate(['/denuncias', id]);
  }

  cambiarEstado(id: number, estado: string): void {
    if (!confirm(`¿Estás seguro de cambiar el estado a ${estado}?`)) {
      return;
    }

    this.denunciaService.cambiarEstado(id, estado).subscribe({
      next: (response) => {
        console.log('Estado cambiado exitosamente:', response);
        alert(`Denuncia #${id} actualizada a ${estado}`);

        // Actualizar el estado localmente sin recargar todo
        const denuncia = this.denunciasOriginales.find(d => d.id === id);
        if (denuncia) {
          denuncia.estado = estado;
        }

        this.aplicarFiltros(); // Volver a aplicar filtros
      },
      error: (err) => {
        console.error('Error al cambiar estado:', err);
        alert('Error al cambiar el estado de la denuncia');
      }
    });
  }

  getEstadoClass(estado: string): string {
    const clases: { [key: string]: string } = {
      'PENDIENTE': 'estado-pendiente',
      'VALIDADA': 'estado-validada',
      'RECHAZADA': 'estado-rechazada'
    };
    return clases[estado] || '';
  }
}
