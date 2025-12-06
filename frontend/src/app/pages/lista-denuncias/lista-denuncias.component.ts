import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { DenunciaService, Denuncia } from '../../services/denuncia';
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
  denunciaSeleccionada: any = null;
  cargando = true;
  error = '';
  mostrarModal = false;
  mostrarFiltros = false;

  filtrosForm: FormGroup;
  estados = ['PENDIENTE', 'VALIDADA', 'RECHAZADA'];

  constructor(
    private denunciaService: DenunciaService,
    private fb: FormBuilder
  ) {
    this.filtrosForm = this.fb.group({
      estado: [''],
      patente: [''],
      email: [''],
      comuna: ['']
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

        let denunciasFiltradas = [];

        if (response && response.denuncias) {
          denunciasFiltradas = response.denuncias;
        } else if (Array.isArray(response)) {
          denunciasFiltradas = response;
        }

        // Aplicar filtros del formulario
        this.denuncias = this.aplicarFiltrosLocal(denunciasFiltradas);

        this.cargando = false;
        console.log('Denuncias cargadas:', this.denuncias);
      },
      error: (err) => {
        console.error('Error al obtener denuncias:', err);
        this.error = 'Error al cargar las denuncias. Por favor, intenta nuevamente.';
        this.cargando = false;
        this.denuncias = [];
      }
    });
  }

  aplicarFiltrosLocal(denuncias: any[]): any[] {
    const filtros = this.filtrosForm.value;
    let resultado = [...denuncias];

    if (filtros.estado) {
      resultado = resultado.filter(d => d.estado === filtros.estado);
    }

    if (filtros.patente) {
      resultado = resultado.filter(d =>
        d.patente && d.patente.toLowerCase().includes(filtros.patente.toLowerCase())
      );
    }

    if (filtros.email) {
      resultado = resultado.filter(d =>
        d.email && d.email.toLowerCase().includes(filtros.email.toLowerCase())
      );
    }

    if (filtros.comuna) {
      resultado = resultado.filter(d =>
        d.comuna && d.comuna.toLowerCase().includes(filtros.comuna.toLowerCase())
      );
    }

    return resultado;
  }

  validar(id: number) {
    if (!confirm('¿Estás seguro de validar esta denuncia?')) {
      return;
    }

    this.denunciaService.validarDenuncia(id).subscribe({
      next: () => {
        alert(`Denuncia #${id} validada correctamente`);
        this.obtenerDenuncias();
      },
      error: (err) => {
        console.error('Error al validar:', err);
        alert('Error al validar la denuncia');
      }
    });
  }

  rechazar(id: number) {
    if (!confirm('¿Estás seguro de rechazar esta denuncia?')) {
      return;
    }

    this.denunciaService.rechazarDenuncia(id).subscribe({
      next: () => {
        alert(`Denuncia #${id} rechazada correctamente`);
        this.obtenerDenuncias();
      },
      error: (err) => {
        console.error('Error al rechazar:', err);
        alert('Error al rechazar la denuncia');
      }
    });
  }

  cambiarEstado(id: number, estado: string): void {
    if (!confirm(`¿Estás seguro de cambiar el estado a ${estado}?`)) {
      return;
    }

    this.denunciaService.cambiarEstado(id, estado).subscribe({
      next: (response) => {
        console.log('Estado cambiado exitosamente:', response);
        alert(`Denuncia #${id} actualizada a ${estado}`);
        this.obtenerDenuncias();
      },
      error: (err) => {
        console.error('Error al cambiar estado:', err);
        alert('Error al cambiar el estado de la denuncia');
      }
    });
  }

  aplicarFiltros(): void {
    this.cargando = true;
    // Como la API no soporta filtros, filtramos localmente
    this.obtenerDenuncias();
  }

  limpiarFiltros(): void {
    this.filtrosForm.reset();
    this.obtenerDenuncias();
  }

  verDetalles(denuncia: any): void {
    this.denunciaSeleccionada = denuncia;
    this.mostrarModal = true;

    if (denuncia.id) {
      this.denunciaService.getFotosDenuncia(denuncia.id).subscribe({
        next: (fotos) => {
          if (this.denunciaSeleccionada) {
            this.denunciaSeleccionada.fotos = fotos;
          }
        },
        error: (err) => {
          console.log('No se pudieron cargar las fotos:', err);
        }
      });
    }
  }

  cerrarModal(): void {
    this.mostrarModal = false;
    this.denunciaSeleccionada = null;
  }

  getEstadoClass(estado: string): string {
    switch (estado) {
      case 'VALIDADA': return 'estado-validada';
      case 'RECHAZADA': return 'estado-rechazada';
      case 'PENDIENTE': return 'estado-pendiente';
      default: return '';
    }
  }
}
