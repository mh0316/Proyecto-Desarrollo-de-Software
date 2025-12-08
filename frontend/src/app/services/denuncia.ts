import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

// Interfaces
export interface Denuncia {
  id: number;
  email: string;
  categoriaId: number;
  descripcion: string;
  patente: string;
  latitud: number;
  longitud: number;
  direccion?: string;
  sector?: string;
  comuna?: string;
  estado: 'PENDIENTE' | 'VALIDADA' | 'RECHAZADA';
  fechaCreacion: Date;
  fotos?: string[];
}

export interface FiltrosDenuncia {
  estado?: string;
  patente?: string;
  email?: string;
  comuna?: string;
  fechaDesde?: string;
  fechaHasta?: string;
  sort?: string;
}

@Injectable({
  providedIn: 'root'
})
export class DenunciaService {
  private baseUrl = `${environment.apiUrl}/api/denuncias`;
  private funcionarioUrl = `${environment.apiUrl}/api/denuncias`;

  constructor(private http: HttpClient) { }

  /**
   * Obtiene todas las denuncias con filtros opcionales
   * @param filtros Objeto con los filtros a aplicar
   * @returns Observable con el listado de denuncias
   */
  getAll(filtros?: FiltrosDenuncia): Observable<any> {
    let params = new HttpParams();

    // Agregar timestamp para cache-busting
    const timestamp = new Date().getTime();
    params = params.set('_t', timestamp.toString());

    // Aplicar filtros si existen
    if (filtros) {
      if (filtros.estado) {
        params = params.set('estado', filtros.estado);
      }
      if (filtros.patente) {
        params = params.set('patente', filtros.patente);
      }
      if (filtros.email) {
        params = params.set('email', filtros.email);
      }
      if (filtros.comuna) {
        params = params.set('comuna', filtros.comuna);
      }
      if (filtros.fechaDesde) {
        params = params.set('fechaDesde', filtros.fechaDesde);
      }
      if (filtros.fechaHasta) {
        params = params.set('fechaHasta', filtros.fechaHasta);
      }
      if (filtros.sort) {
        params = params.set('sort', filtros.sort);
      }
    }

    return this.http.get<any>(this.baseUrl, { params });
  }

  /**
   * Obtiene denuncias del funcionario
   * @param email Email del funcionario
   * @returns Observable con las denuncias asignadas
   */
  getDenunciasFuncionario(email: string = 'funcionario@municipalidad. cl'): Observable<any> {
    const params = new HttpParams().set('email', email);
    return this.http.get<any>(this.funcionarioUrl, { params });
  }

  /**
   * Obtiene una denuncia específica por ID
   * @param id ID de la denuncia
   * @returns Observable con los datos de la denuncia
   */
  getById(id: number): Observable<Denuncia> {
    return this.http.get<Denuncia>(`${this.baseUrl}/${id}`);
  }

  /**
   * Cambia el estado de una denuncia
   * @param id ID de la denuncia
   * @param estado Nuevo estado (PENDIENTE, EN_REVISION, VALIDADA, RECHAZADA)
   * @param comentario Comentario opcional (requerido para RECHAZADA)
   * @returns Observable con la respuesta del servidor
   */
  cambiarEstado(id: number, estado: string, comentario?: string): Observable<any> {
    const body: any = { estado };
    if (comentario) {
      body.comentario = comentario;
    }
    return this.http.put<any>(`${this.baseUrl}/${id}/estado`, body);
  }

  /**
   * Valida una denuncia (cambia su estado a VALIDADA)
   * @param id ID de la denuncia a validar
   * @returns Observable con la respuesta del servidor
   */
  validarDenuncia(id: number): Observable<any> {
    // Usa la URL base del environment
    return this.http.put(`${environment.apiUrl}/api/funcionario/denuncias/${id}/validar`, {});
  }

  /**
   * Rechaza una denuncia (cambia su estado a RECHAZADA)
   * @param id ID de la denuncia a rechazar
   * @returns Observable con la respuesta del servidor
   */
  rechazarDenuncia(id: number): Observable<any> {
    // Usa la URL base del environment
    return this.http.put(`${environment.apiUrl}/api/funcionario/denuncias/${id}/rechazar`, {});
  }

  /**
   * Marca una denuncia como pendiente
   * @param id ID de la denuncia
   * @returns Observable con la respuesta del servidor
   */
  marcarPendiente(id: number): Observable<any> {
    return this.cambiarEstado(id, 'PENDIENTE');
  }

  /**
   * Obtiene las fotos asociadas a una denuncia
   * @param id ID de la denuncia
   * @returns Observable con array de URLs de fotos
   */
  getFotosDenuncia(id: number): Observable<string[]> {
    return this.http.get<string[]>(`${this.baseUrl}/${id}/fotos`);
  }

  /**
   * Crea una nueva denuncia
   * @param denuncia Datos de la denuncia a crear
   * @returns Observable con la denuncia creada
   */
  crear(denuncia: Partial<Denuncia>): Observable<Denuncia> {
    return this.http.post<Denuncia>(this.baseUrl, denuncia);
  }

  /**
   * Actualiza una denuncia existente
   * @param id ID de la denuncia
   * @param denuncia Datos actualizados
   * @returns Observable con la denuncia actualizada
   */
  actualizar(id: number, denuncia: Partial<Denuncia>): Observable<Denuncia> {
    return this.http.put<Denuncia>(`${this.baseUrl}/${id}`, denuncia);
  }

  /**
   * Elimina una denuncia
   * @param id ID de la denuncia a eliminar
   * @returns Observable con la respuesta del servidor
   */
  eliminar(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  /**
   * Obtiene estadísticas de denuncias
   * @returns Observable con las estadísticas
   */
  getEstadisticas(): Observable<any> {
    return this.http.get(`${this.baseUrl}/estadisticas`);
  }
  /**
   * Obtiene estadísticas avanzadas para el dashboard
   * @returns Observable con las estadísticas avanzadas
   */
  getEstadisticasAvanzadas(): Observable<any> {
    return this.http.get(`${this.baseUrl}/estadisticas-avanzadas`);
  }
}
