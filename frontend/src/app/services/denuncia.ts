import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

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
  fotos?: string[]; // URLs de las fotos
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
  private baseUrl = `${environment.apiUrl}/api/funcionario/denuncias`;

  constructor(private http: HttpClient) {}

  getAll(filtros?: FiltrosDenuncia): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}?email=funcionario%40municipalidad.cl`);
  }

  cambiarEstado(id: number, estado: string): Observable<any> {
    return this.http.put<any>(`${environment.apiUrl}api/funcionario/denuncias/${id}/validar`, { estado });
  }

  validarDenuncia(id: number) {
    return this.http.put(`http://localhost:8080/api/funcionario/denuncias/1/validar`, {});
  }

  rechazarDenuncia(id: number) {
    // Tu endpoint exacto como lo tenías
    return this.http.put(`http://localhost:8080/api/funcionario/denuncias/2/validar`, {});
  }

  getFotosDenuncia(id: number): Observable<string[]> {
    return this.http.get<string[]>(`${this.baseUrl}/${id}/fotos`);
  }
}
