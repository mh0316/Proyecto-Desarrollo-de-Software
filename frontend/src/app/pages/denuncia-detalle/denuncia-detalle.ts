import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { ModalComponent, ModalConfig } from '../../components/modal/modal.component';
import { ModalService } from '../../services/modal.service';

interface Evidencia {
  id: number;
  tipo: string;
  nombreArchivo: string;
  url: string;
  mimeType: string;
  tamanoBytes: number;
  fechaSubida: string;
}

interface ComentarioInterno {
  id: number;
  nombreUsuario: string;
  apellidoUsuario: string;
  emailUsuario: string;
  comentario: string;
  fechaComentario: string;
}

interface HistorialAccion {
  id: number;
  nombreUsuario: string;
  apellidoUsuario: string;
  emailUsuario: string;
  tipoAccion: string;
  descripcion: string;
  fechaAccion: string;
}

interface Denuncia {
  id: number;
  emailUsuario: string;
  nombreUsuario: string;
  categoria: {
    id: number;
    nombre: string;
    codigo: string;
    colorHex: string;
  };
  descripcion: string;
  patente: string;
  latitud: number;
  longitud: number;
  direccion: string;
  sector: string;
  comuna: string;
  estado: string;
  fechaDenuncia: string;
  cantidadEvidencias: number;
  evidenciasUrls: string[];
}

@Component({
  selector: 'app-denuncia-detalle',
  standalone: true,
  imports: [CommonModule, FormsModule, ModalComponent],
  templateUrl: './denuncia-detalle.html',
  styleUrls: ['./denuncia-detalle.scss']
})
export class DenunciaDetalleComponent implements OnInit {
  denunciaId: number | null = null;
  denuncia: Denuncia | null = null;
  evidencias: Evidencia[] = [];
  comentariosInternos: ComentarioInterno[] = [];
  historialAcciones: HistorialAccion[] = [];
  loading = true;
  error = '';
  apiUrl = environment.apiUrl;
  nuevoComentario = '';
  modalConfig: ModalConfig | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient,
    public modalService: ModalService
  ) { }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.denunciaId = +params['id'];
      if (this.denunciaId) {
        this.cargarDenuncia();
        this.cargarEvidencias();
        this.cargarComentariosInternos();
        this.cargarHistorialAcciones();
      }
    });
  }

  cargarDenuncia() {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.http.get<any>(`${this.apiUrl}/api/denuncias/${this.denunciaId}`, { headers })
      .subscribe({
        next: (response) => {
          if (response.success) {
            this.denuncia = response.denuncia;
          }
        },
        error: (err) => {
          console.error('Error al cargar denuncia:', err);
          this.error = 'Error al cargar la denuncia';
          this.loading = false;
        }
      });
  }

  cargarEvidencias() {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.http.get<any>(`${this.apiUrl}/api/denuncias/${this.denunciaId}/evidencias`, { headers })
      .subscribe({
        next: (response) => {
          if (response.success) {
            this.evidencias = response.evidencias;
          }
          this.loading = false;
        },
        error: (err) => {
          console.error('Error al cargar evidencias:', err);
          this.loading = false;
        }
      });
  }

  volver() {
    this.router.navigate(['/denuncias']);
  }

  getImageUrl(url: string): string {
    return `${this.apiUrl}${url}`;
  }

  async eliminarDenuncia() {
    if (!this.denunciaId) return;

    const confirmed = await this.modalService.showConfirm(
      'Confirmar eliminación',
      '¿Está seguro de que desea eliminar esta denuncia?\n\n' +
      'Esta acción eliminará:\n' +
      '- La denuncia completa\n' +
      '- Todas las evidencias asociadas\n' +
      '- Los archivos de evidencia del servidor\n\n' +
      'Esta acción NO se puede deshacer.'
    );

    if (!confirmed) return;

    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.http.delete<any>(`${this.apiUrl}/api/denuncias/${this.denunciaId}`, { headers })
      .subscribe({
        next: async (response) => {
          if (response.success) {
            await this.modalService.showAlert('Éxito', 'Denuncia eliminada correctamente', 'success');
            this.router.navigate(['/denuncias']);
          }
        },
        error: async (err) => {
          console.error('Error al eliminar denuncia:', err);
          await this.modalService.showAlert('Error', 'Error al eliminar la denuncia: ' + (err.error?.message || 'Error desconocido'), 'error');
        }
      });
  }

  cargarComentariosInternos() {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.http.get<any>(`${this.apiUrl}/api/denuncias/${this.denunciaId}/comentarios`, { headers })
      .subscribe({
        next: (response) => {
          if (response.success && response.comentarios) {
            this.comentariosInternos = response.comentarios;
          }
        },
        error: (err) => {
          console.error('Error al cargar comentarios internos:', err);
        }
      });
  }

  cargarHistorialAcciones() {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.http.get<any>(`${this.apiUrl}/api/denuncias/${this.denunciaId}/historial`, { headers })
      .subscribe({
        next: (response) => {
          if (response.success && response.historial) {
            this.historialAcciones = response.historial;
          }
        },
        error: (err) => {
          console.error('Error al cargar historial de acciones:', err);
        }
      });
  }

  async cambiarEstado(nuevoEstado: string) {
    if (!this.denunciaId) return;

    let comentario = '';

    // Si es rechazo, solicitar motivo con modal
    if (nuevoEstado === 'RECHAZADA') {
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
        `¿Está seguro de cambiar el estado a ${this.getEstadoLabel(nuevoEstado)}?`
      );
      if (!confirmed) return;
    }

    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    const body: any = { estado: nuevoEstado };
    if (comentario) {
      body.comentario = comentario;
    }

    this.http.put<any>(`${this.apiUrl}/api/denuncias/${this.denunciaId}/estado`, body, { headers })
      .subscribe({
        next: async (response) => {
          if (response.success) {
            await this.modalService.showAlert('Éxito', `Estado cambiado a ${this.getEstadoLabel(nuevoEstado)} exitosamente`, 'success');
            this.cargarDenuncia();
            this.cargarHistorialAcciones();
          }
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

  getTipoAccionLabel(tipoAccion: string): string {
    const labels: { [key: string]: string } = {
      'CREACION': 'Creación',
      'VALIDACION': 'Validación',
      'RECHAZO': 'Rechazo',
      'CAMBIO_ESTADO': 'Cambio de Estado',
      'COMENTARIO': 'Comentario',
      'EVIDENCIA': 'Evidencia'
    };
    return labels[tipoAccion] || tipoAccion;
  }

  async agregarComentario() {
    if (!this.denunciaId || !this.nuevoComentario.trim()) {
      await this.modalService.showAlert('Error', 'El comentario no puede estar vacío', 'error');
      return;
    }

    const token = localStorage.getItem('token');
    const userEmail = this.getEmailFromToken(token);

    if (!userEmail) {
      await this.modalService.showAlert('Error', 'No se pudo obtener el email del usuario', 'error');
      return;
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    const body = {
      emailUsuario: userEmail,
      comentario: this.nuevoComentario
    };

    this.http.post<any>(`${this.apiUrl}/api/denuncias/${this.denunciaId}/comentarios`, body, { headers })
      .subscribe({
        next: async (response) => {
          if (response.success) {
            await this.modalService.showAlert('Éxito', 'Comentario agregado correctamente', 'success');
            this.nuevoComentario = '';
            this.cargarComentariosInternos();
            this.cargarHistorialAcciones();
          }
        },
        error: async (err) => {
          console.error('Error al agregar comentario:', err);
          await this.modalService.showAlert('Error', 'Error al agregar el comentario', 'error');
        }
      });
  }

  async eliminarComentario(comentarioId: number) {
    const confirmed = await this.modalService.showConfirm(
      'Confirmar eliminación',
      '¿Está seguro de que desea eliminar este comentario?'
    );

    if (!confirmed) return;

    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.http.delete<any>(`${this.apiUrl}/api/denuncias/comentarios/${comentarioId}`, { headers })
      .subscribe({
        next: async (response) => {
          if (response.success) {
            await this.modalService.showAlert('Éxito', 'Comentario eliminado correctamente', 'success');
            this.cargarComentariosInternos();
            this.cargarHistorialAcciones();
          }
        },
        error: async (err) => {
          console.error('Error al eliminar comentario:', err);
          await this.modalService.showAlert('Error', 'Error al eliminar el comentario', 'error');
        }
      });
  }

  private getEmailFromToken(token: string | null): string | null {
    if (!token) return null;
    try {
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(
        atob(base64)
          .split('')
          .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      );
      const decoded = JSON.parse(jsonPayload);
      return decoded.email || decoded.sub || null;
    } catch (error) {
      console.error('Error decoding token:', error);
      return null;
    }
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
