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
  imports: [CommonModule, HttpClientModule, ReactiveFormsModule, FormsModule, ModalComponent],
  templateUrl: './lista-denuncias.component.html',
  styleUrls: ['./lista-denuncias.component.scss']
})
export class ListaDenunciasComponent implements OnInit {
  denuncias: any[] = [];
  cargando = true;
  error = '';

  // Paginación
  currentPage = 0;
  pageSize = 20;
  totalElements = 0;
  totalPages = 0;
  pageSizeOptions = [10, 20, 50, 100];

  // Formulario de filtros
  filtrosForm: FormGroup;
  estados: string[] = ['PENDIENTE', 'EN_REVISION', 'VALIDADA', 'RECHAZADA'];

  // Datos para autocompletado
  comunasDisponibles: string[] = [];
  patentesDisponibles: string[] = [];

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
    this.cargarDatosAutocompletado();
  }

  obtenerDenuncias(): void {
    this.cargando = true;
    this.error = '';

    // Construir filtros desde el formulario (solo los que el backend soporta)
    const filtros: any = {};
    const estado = this.filtrosForm.get('estado')?.value;
    const patente = this.filtrosForm.get('patente')?.value;
    const comuna = this.filtrosForm.get('comuna')?.value;

    if (estado) filtros.estado = estado;
    if (patente) filtros.patente = patente;
    if (comuna) filtros.comuna = comuna;

    this.denunciaService.getAllPaginated(this.currentPage, this.pageSize, filtros).subscribe({
      next: (response) => {
        console.log('Respuesta paginada:', response);

        if (response && response.success && response.data) {
          const pageData = response.data;
          let denuncias = pageData.content || [];

          // Aplicar filtros del lado del cliente (evidencias)
          const tieneEvidencias = this.filtrosForm.get('tieneEvidencias')?.value;
          if (tieneEvidencias === 'si') {
            denuncias = denuncias.filter((d: any) => d.cantidadEvidencias > 0);
          } else if (tieneEvidencias === 'no') {
            denuncias = denuncias.filter((d: any) => d.cantidadEvidencias === 0);
          }

          // Aplicar ordenamiento del lado del cliente
          const ordenFecha = this.filtrosForm.get('ordenFecha')?.value;
          if (ordenFecha === 'recientes') {
            denuncias.sort((a: any, b: any) => {
              const fechaA = new Date(a.fechaDenuncia || 0);
              const fechaB = new Date(b.fechaDenuncia || 0);
              return fechaB.getTime() - fechaA.getTime();
            });
          } else if (ordenFecha === 'antiguos') {
            denuncias.sort((a: any, b: any) => {
              const fechaA = new Date(a.fechaDenuncia || 0);
              const fechaB = new Date(b.fechaDenuncia || 0);
              return fechaA.getTime() - fechaB.getTime();
            });
          }

          this.denuncias = denuncias;
          this.totalElements = pageData.totalElements || 0;
          this.totalPages = pageData.totalPages || 0;
          this.currentPage = pageData.currentPage || 0;
        } else {
          this.denuncias = [];
          this.totalElements = 0;
          this.totalPages = 0;
        }

        this.cargando = false;
        console.log(`Cargadas ${this.denuncias.length} denuncias de ${this.totalElements} totales`);
      },
      error: (err) => {
        console.error('Error al obtener denuncias:', err);
        this.error = 'Error al cargar las denuncias. Por favor, intenta nuevamente.';
        this.cargando = false;
        this.denuncias = [];
      }
    });
  }

  aplicarFiltros(): void {
    // Resetear a la primera página cuando se aplican filtros
    this.currentPage = 0;
    this.obtenerDenuncias();
  }

  limpiarFiltros(): void {
    this.filtrosForm.reset({
      estado: '',
      patente: '',
      comuna: '',
      ordenFecha: 'recientes',
      tieneEvidencias: ''
    });
    this.currentPage = 0;
    this.obtenerDenuncias();
  }

  // Métodos de paginación
  cambiarPagina(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.obtenerDenuncias();
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  cambiarTamanoPagina(size: number): void {
    this.pageSize = size;
    this.currentPage = 0; // Resetear a primera página
    this.obtenerDenuncias();
  }

  irPrimeraPagina(): void {
    this.cambiarPagina(0);
  }

  irUltimaPagina(): void {
    this.cambiarPagina(this.totalPages - 1);
  }

  get paginaActualDisplay(): number {
    return this.currentPage + 1;
  }

  get rangoInicio(): number {
    return this.totalElements === 0 ? 0 : this.currentPage * this.pageSize + 1;
  }

  get rangoFin(): number {
    const fin = (this.currentPage + 1) * this.pageSize;
    return fin > this.totalElements ? this.totalElements : fin;
  }

  // Generar array de números de página para mostrar
  get paginasParaMostrar(): number[] {
    const maxPaginas = 5;
    const paginas: number[] = [];

    let inicio = Math.max(0, this.currentPage - Math.floor(maxPaginas / 2));
    let fin = Math.min(this.totalPages, inicio + maxPaginas);

    // Ajustar inicio si estamos cerca del final
    if (fin - inicio < maxPaginas) {
      inicio = Math.max(0, fin - maxPaginas);
    }

    for (let i = inicio; i < fin; i++) {
      paginas.push(i);
    }

    return paginas;
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

        // Recargar la página actual
        this.obtenerDenuncias();
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

  /**
   * Cargar datos únicos para autocompletado
   */
  cargarDatosAutocompletado(): void {
    // Obtener todas las denuncias sin paginación para extraer comunas y patentes únicas
    this.denunciaService.getAll().subscribe({
      next: (response) => {
        let todasLasDenuncias: any[] = [];

        if (response && response.denuncias) {
          todasLasDenuncias = response.denuncias;
        } else if (Array.isArray(response)) {
          todasLasDenuncias = response;
        }

        // Extraer comunas únicas (filtrar nulos y vacíos)
        const comunasSet = new Set<string>();
        todasLasDenuncias.forEach((d: any) => {
          if (d.comuna && d.comuna.trim()) {
            comunasSet.add(d.comuna.trim());
          }
        });
        this.comunasDisponibles = Array.from(comunasSet).sort();

        // Extraer patentes únicas (filtrar nulos y vacíos)
        const patentesSet = new Set<string>();
        todasLasDenuncias.forEach((d: any) => {
          if (d.patente && d.patente.trim()) {
            patentesSet.add(d.patente.trim().toUpperCase());
          }
        });
        this.patentesDisponibles = Array.from(patentesSet).sort();

        console.log(`Autocompletado cargado: ${this.comunasDisponibles.length} comunas, ${this.patentesDisponibles.length} patentes`);
      },
      error: (err) => {
        console.error('Error al cargar datos de autocompletado:', err);
      }
    });
  }
}
