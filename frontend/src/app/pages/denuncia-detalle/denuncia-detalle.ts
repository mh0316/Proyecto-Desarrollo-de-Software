import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../../environments/environment';

interface Evidencia {
  id: number;
  tipo: string;
  nombreArchivo: string;
  url: string;
  mimeType: string;
  tamanoBytes: number;
  fechaSubida: string;
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
  imports: [CommonModule],
  templateUrl: './denuncia-detalle.html',
  styleUrls: ['./denuncia-detalle.scss']
})
export class DenunciaDetalleComponent implements OnInit {
  denunciaId: number | null = null;
  denuncia: Denuncia | null = null;
  evidencias: Evidencia[] = [];
  loading = true;
  error = '';
  apiUrl = environment.apiUrl;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient
  ) { }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.denunciaId = +params['id'];
      if (this.denunciaId) {
        this.cargarDenuncia();
        this.cargarEvidencias();
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

  eliminarDenuncia() {
    if (!this.denunciaId) return;

    const confirmacion = confirm(
      '¿Está seguro de que desea eliminar esta denuncia?\n\n' +
      'Esta acción eliminará:\n' +
      '- La denuncia completa\n' +
      '- Todas las evidencias asociadas\n' +
      '- Los archivos de evidencia del servidor\n\n' +
      'Esta acción NO se puede deshacer.'
    );

    if (!confirmacion) return;

    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.http.delete<any>(`${this.apiUrl}/api/denuncias/${this.denunciaId}`, { headers })
      .subscribe({
        next: (response) => {
          if (response.success) {
            alert('Denuncia eliminada correctamente');
            this.router.navigate(['/denuncias']);
          }
        },
        error: (err) => {
          console.error('Error al eliminar denuncia:', err);
          alert('Error al eliminar la denuncia: ' + (err.error?.message || 'Error desconocido'));
        }
      });
  }
}
