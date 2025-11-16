import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DenunciaService } from '../../services/denuncia';

@Component({
  selector: 'app-lista-denuncias',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './lista-denuncias.component.html',
  styleUrl: './lista-denuncias.component.scss'
})
export class ListaDenunciasComponent implements OnInit {
  denuncias: any[] = [];
  cargando = true; // ✅ propiedad agregada

  constructor(private denunciaService: DenunciaService) {}

  ngOnInit(): void {
    this.obtenerDenuncias();
  }

  obtenerDenuncias(): void {
    this.cargando = true;
    this.denunciaService.getAll().subscribe({
      next: (response) => {
        this.denuncias = response.data || response; // soporta ambos formatos
        this.cargando = false;
      },
      error: (err) => {
        console.error('Error al obtener denuncias:', err);
        this.cargando = false;
      }
    });
  }

  // ✅ método agregado
  cambiarEstado(id: number, estado: string): void {
    this.denunciaService.cambiarEstado(id, estado).subscribe({
      next: () => {
        alert(`Denuncia ${id} actualizada a ${estado}`);
        this.obtenerDenuncias();
      },
      error: (err) => console.error('Error al cambiar estado:', err)
    });
  }
}
