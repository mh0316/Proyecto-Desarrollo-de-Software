import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { DenunciaService } from '../../services/denuncia';
import { HttpClientModule } from '@angular/common/http';
import { ModalComponent } from '../../components/modal/modal.component';
import { ModalService } from '../../services/modal.service';

@Component({
  selector: 'app-lista-denuncias',
  standalone: true,
  imports: [CommonModule, HttpClientModule, ReactiveFormsModule, ModalComponent],
  templateUrl: './lista-denuncias.component.html',
  styleUrls: ['./lista-denuncias.component.scss']
})
export class ListaDenunciasComponent implements OnInit {
  denuncias: any[] = [];
  denunciasOriginales: any[] = []; // Guardamos las denuncias sin filtrar
  cargando = true;
  error = '';

  // Paginación
  currentPage = 0;
  pageSize = 20;
  totalPages = 0;
  totalElements = 0;
  hasNext = false;
  hasPrevious = false;

  // Math object for template
  Math = Math;

  // Formulario de filtros
  filtrosForm: FormGroup;
  estados: string[] = ['PENDIENTE', 'EN_REVISION', 'VALIDADA', 'RECHAZADA'];

  constructor(
    private denunciaService: DenunciaService,
    private router: Router,
    private fb: FormBuilder,
    public modalService: ModalService
  ) {
    // Inicializar el formulario de filtros
    this.filtrosForm = this.fb.group({
      estado: [''],
      patente: [''],
      comuna: [''],
      ordenFecha: ['recientes'],
      tieneEvidencias: ['']
    });
  }

  ngOnInit(): void {
    this.obtenerDenuncias();
  }

  obtenerDenuncias(): void {
    this.cargando = true;
    this.error = '';

    this.denunciaService.getAll(undefined, this.currentPage, this.pageSize).subscribe({
      next: (response) => {
        console.log('Respuesta completa de la API:', response);

        if (response && response.denuncias) {
          this.denunciasOriginales = response.denuncias;

          // Actualizar metadata de paginación
          if (response.pagination) {
            this.currentPage = response.pagination.currentPage;
            this.totalPages = response.pagination.totalPages;
            this.totalElements = response.pagination.totalElements;
            this.pageSize = response.pagination.pageSize;
            this.hasNext = response.pagination.hasNext;
            this.hasPrevious = response.pagination.hasPrevious;
          }
        } else if (Array.isArray(response)) {
          this.denunciasOriginales = response;
        } else {
          this.denunciasOriginales = [];
        }

        // Aplicar filtros después de cargar
        this.aplicarFiltros();
        this.cargando = false;
        console.log('Denuncias cargadas:', this.denuncias);
        console.log('Paginación:', { currentPage: this.currentPage, totalPages: this.totalPages, totalElements: this.totalElements });
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
    let denunciasFiltradas = [...this.denunciasOriginales];

    // Filtro por estado
    const estadoSeleccionado = this.filtrosForm.get('estado')?.value;
    if (estadoSeleccionado) {
      denunciasFiltradas = denunciasFiltradas.filter(
        (d) => d.estado === estadoSeleccionado
      );
    }

    // Filtro por patente
    const patenteIngresada = this.filtrosForm.get('patente')?.value?.toLowerCase();
    if (patenteIngresada) {
      denunciasFiltradas = denunciasFiltradas.filter((d) =>
        d.patente && d.patente.toLowerCase().includes(patenteIngresada)
      );
    }

    // Filtro por comuna
    const comunaIngresada = this.filtrosForm.get('comuna')?.value?.toLowerCase();
    if (comunaIngresada) {
      denunciasFiltradas = denunciasFiltradas.filter((d) =>
        d.comuna && d.comuna.toLowerCase().includes(comunaIngresada)
      );
    }

    // Filtro por evidencias
    const tieneEvidencias = this.filtrosForm.get('tieneEvidencias')?.value;
    if (tieneEvidencias === 'si') {
      denunciasFiltradas = denunciasFiltradas.filter((d) => d.cantidadEvidencias > 0);
    } else if (tieneEvidencias === 'no') {
      denunciasFiltradas = denunciasFiltradas.filter((d) => d.cantidadEvidencias === 0);
    }

    // Ordenar por fecha
    const ordenFecha = this.filtrosForm.get('ordenFecha')?.value;
    if (ordenFecha === 'recientes') {
      denunciasFiltradas.sort((a, b) => {
        const fechaA = new Date(a.fechaCreacion || a.id || 0);
        const fechaB = new Date(b.fechaCreacion || b.id || 0);
        return fechaB.getTime() - fechaA.getTime();
      });
    } else if (ordenFecha === 'antiguos') {
      denunciasFiltradas.sort((a, b) => {
        const fechaA = new Date(a.fechaCreacion || a.id || 0);
        const fechaB = new Date(b.fechaCreacion || b.id || 0);
        return fechaA.getTime() - fechaB.getTime();
      });
    }

    this.denuncias = denunciasFiltradas;
    console.log('Filtros aplicados.  Resultados:', this.denuncias.length);
  }

  limpiarFiltros(): void {
    this.filtrosForm.reset({
      estado: '',
      patente: '',
      comuna: '',
      ordenFecha: 'recientes'
    });
    this.aplicarFiltros();
  }

  verDetalle(id: number): void {
    this.router.navigate(['/denuncias', id]);
  }

  async cambiarEstado(id: number, estado: string): Promise<void> {
    let comentario = '';

    // Si es rechazo, solicitar motivo con modal
    if (estado === 'RECHAZADA') {
      const motivo = await this.modalService.showInput(
        'Motivo de Rechazo',
        'Por favor, ingrese el motivo por el cual está rechazando esta denuncia:',
        'Ej: Información insuficiente, fuera de jurisdicción, etc.'
      );

      if (!motivo) {
        return; // Usuario canceló
      }

      comentario = motivo;
    } else {
      const confirmed = await this.modalService.showConfirm(
        'Confirmar cambio de estado',
        `¿Estás seguro de cambiar el estado a ${this.getEstadoLabel(estado)}?`
      );
      if (!confirmed) return;
    }

    this.denunciaService.cambiarEstado(id, estado, comentario).subscribe({
      next: async (response) => {
        console.log('Estado cambiado exitosamente:', response);
        await this.modalService.showAlert('Éxito', `Denuncia #${id} actualizada a ${this.getEstadoLabel(estado)}`, 'success');

        // Actualizar el estado localmente sin recargar todo
        const denuncia = this.denunciasOriginales.find(d => d.id === id);
        if (denuncia) {
          denuncia.estado = estado;
        }

        this.aplicarFiltros(); // Volver a aplicar filtros
      },
      error: async (err) => {
        console.error('Error al cambiar estado:', err);
        await this.modalService.showAlert('Error', 'Error al cambiar el estado de la denuncia', 'error');
      }
    });
  }

  getEstadoLabel(estado: string): string {
    const labels: { [key: string]: string } = {
      'PENDIENTE': 'Pendiente',
      'EN_REVISION': 'En Revisión',
      'VALIDADA': 'Validada',
      'RECHAZADA': 'Rechazada',
      'CERRADA': 'Cerrada'
    };
    return labels[estado] || estado;
  }

  isBotonDeshabilitado(denuncia: any, accion: string): boolean {
    // Deshabilitar el botón si la denuncia ya está en ese estado
    return denuncia.estado === accion;
  }

  getEstadoClass(estado: string): string {
    const clases: { [key: string]: string } = {
      'PENDIENTE': 'estado-pendiente',
      'EN_REVISION': 'estado-en-revision',
      'VALIDADA': 'estado-validada',
      'RECHAZADA': 'estado-rechazada',
      'CERRADA': 'estado-cerrada'
    };
    return clases[estado] || '';
  }

  // Métodos de paginación
  nextPage(): void {
    if (this.hasNext) {
      this.currentPage++;
      this.obtenerDenuncias();
    }
  }

  previousPage(): void {
    if (this.hasPrevious) {
      this.currentPage--;
      this.obtenerDenuncias();
    }
  }

  goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.obtenerDenuncias();
    }
  }

  changePageSize(newSize: number): void {
    this.pageSize = newSize;
    this.currentPage = 0; // Reset to first page
    this.obtenerDenuncias();
  }

  getPageNumbers(): number[] {
    const pages: number[] = [];
    const maxPagesToShow = 5;

    if (this.totalPages <= maxPagesToShow) {
      // Show all pages
      for (let i = 0; i < this.totalPages; i++) {
        pages.push(i);
      }
    } else {
      // Show current page and surrounding pages
      const startPage = Math.max(0, this.currentPage - 2);
      const endPage = Math.min(this.totalPages - 1, this.currentPage + 2);

      for (let i = startPage; i <= endPage; i++) {
        pages.push(i);
      }
    }

    return pages;
  }

  onModalConfirm(value?: string | void): void {
    if (typeof value === 'string') {
      this.modalService.confirm(value);
    } else {
      this.modalService.confirm();
    }
  }

  onModalCancel(): void {
    this.modalService.cancel();
  }
}
