import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DenunciaDetalle } from './denuncia-detalle';

describe('DenunciaDetalle', () => {
  let component: DenunciaDetalle;
  let fixture: ComponentFixture<DenunciaDetalle>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DenunciaDetalle]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DenunciaDetalle);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
