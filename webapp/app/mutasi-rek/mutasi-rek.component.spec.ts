import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MutasiRekComponent } from './mutasi-rek.component';

describe('MutasiRekComponent', () => {
  let component: MutasiRekComponent;
  let fixture: ComponentFixture<MutasiRekComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MutasiRekComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MutasiRekComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
