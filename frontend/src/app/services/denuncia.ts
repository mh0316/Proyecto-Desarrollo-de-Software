import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
// RxJS
import { Observable, timer, of } from 'rxjs';
import { shareReplay, tap, switchMap } from 'rxjs/operators';
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

  // --- Caching ---
  // Cache for basic stats (1 min)
  private statsCache$: Observable<any> | null = null;
  // Cache for advanced stats (5 min)
  private advancedStatsCache$: Observable<any> | null = null;

  constructor(private http: HttpClient) { }

  /**
   * Helper to clear caches when data mutates
   */
  private clearCaches() {
    console.log('🧹 Limpiando cache de estadísticas...');
    this.statsCache$ = null;
    this.advancedStatsCache$ = null;
  }

  /**
   * Obtiene todas las denuncias con filtros opcionales y paginación
   * @param filtros Objeto con los filtros a aplicar
   * @param page Número de página (0-indexed)
   * @param size Tamaño de página
   * @returns Observable con el listado de denuncias paginadas
   */
  getAll(filtros?: FiltrosDenuncia, page: number = 0, size: number = 20): Observable<any> {
    let params = new HttpParams();

    // Agregar parámetros de paginación
    params = params.set('page', page.toString());
    params = params.set('size', size.toString());

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
    return this.http.put<any>(`${this.baseUrl}/${id}/estado`, body).pipe(
      tap(() => this.clearCaches()) // Invalidate cache on change
    );
  }

  /**
   * Valida una denuncia (cambia su estado a VALIDADA)
   * @param id ID de la denuncia a validar
   * @returns Observable con la respuesta del servidor
   */
  validarDenuncia(id: number): Observable<any> {
    return this.http.put(`${environment.apiUrl}/api/funcionario/denuncias/${id}/validar`, {}).pipe(
      tap(() => this.clearCaches())
    );
  }

  /**
   * Rechaza una denuncia (cambia su estado a RECHAZADA)
   * @param id ID de la denuncia a rechazar
   * @returns Observable con la respuesta del servidor
   */
  rechazarDenuncia(id: number): Observable<any> {
    return this.http.put(`${environment.apiUrl}/api/funcionario/denuncias/${id}/rechazar`, {}).pipe(
      tap(() => this.clearCaches())
    );
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
    return this.http.post<Denuncia>(this.baseUrl, denuncia).pipe(
      tap(() => this.clearCaches())
    );
  }

  /**
   * Actualiza una denuncia existente
   * @param id ID de la denuncia
   * @param denuncia Datos actualizados
   * @returns Observable con la denuncia actualizada
   */
  actualizar(id: number, denuncia: Partial<Denuncia>): Observable<Denuncia> {
    return this.http.put<Denuncia>(`${this.baseUrl}/${id}`, denuncia).pipe(
      tap(() => this.clearCaches())
    );
  }

  /**
   * Elimina una denuncia
   * @param id ID de la denuncia a eliminar
   * @returns Observable con la respuesta del servidor
   */
  eliminar(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${id}`).pipe(
      tap(() => this.clearCaches())
    );
  }

  /**
   * Obtiene estadísticas de denuncias
   * Caches response for 1 minute
   * @returns Observable con las estadísticas
   */
  getEstadisticas(): Observable<any> {
    // If cache exists, return it
    if (this.statsCache$) {
      return this.statsCache$;
    }

    // Create request with caching
    // Usamos pipe(shareReplay(1)) para compartir la última respuesta a nuevos suscriptores
    this.statsCache$ = this.http.get(`${this.baseUrl}/estadisticas`).pipe(
      shareReplay(1)
    );

    // Auto-expire cache after 1 minute (60000ms)
    // Usamos timer para limpiar la variable de cache, forzando un nuevo request la próxima vez
    timer(60000).subscribe(() => {
      this.statsCache$ = null;
    });

    return this.statsCache$;
  }

  /**
   * Obtiene estadísticas avanzadas para el dashboard
   * Caches response for 5 minutes
   * @returns Observable con las estadísticas avanzadas
   */
  getEstadisticasAvanzadas(): Observable<any> {
    // If cache exists, return it
    if (this.advancedStatsCache$) {
      return this.advancedStatsCache$;
    }

    // Create request with caching
    // shareReplay(1) "replays" the last emission (the HTTP response) to any number of subscribers
    // without making a new network request.
    this.advancedStatsCache$ = this.http.get(`${this.baseUrl}/estadisticas-avanzadas`).pipe(
      shareReplay(1)
    );

    // Auto-expire cache after 5 minutes (300000ms)
    timer(300000).subscribe(() => {
      this.advancedStatsCache$ = null;
    });

    return this.advancedStatsCache$;
  }
}
