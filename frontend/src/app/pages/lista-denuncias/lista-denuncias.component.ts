import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DenunciaService } from '../../services/denuncia';
import { HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-lista-denuncias',
  standalone: true,
  imports: [CommonModule, HttpClientModule],
  templateUrl: './lista-denuncias.component.html',
  styleUrl: './lista-denuncias.component.scss'
})
export class ListaDenunciasComponent implements OnInit {
  denuncias: any[] = [];
  cargando = true;
  error = '';

  constructor(private denunciaService: DenunciaService) {}

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
          this.denuncias = response.denuncias;
        } else if (Array.isArray(response)) {
          this.denuncias = response;
        } else {
          this.denuncias = [];
        }

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

  cambiarEstado(id: number, estado: string): void {
    if (!confirm(`¿Estás seguro de cambiar el estado a ${estado}?`)) {
      return;
    }

    this.denunciaService.cambiarEstado(id, estado).subscribe({
      next: (response) => {
        console.log('Estado cambiado exitosamente:', response);
        alert(`Denuncia #${id} actualizada a ${estado}`);
        this.obtenerDenuncias(); // Recargar la lista
      },
      error: (err) => {
        console.error('Error al cambiar estado:', err);
        alert('Error al cambiar el estado de la denuncia');
      }
    });
  }
}
