import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InfoSaldoComponent } from './info-saldo.component';

describe('InfoSaldoComponent', () => {
  let component: InfoSaldoComponent;
  let fixture: ComponentFixture<InfoSaldoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ InfoSaldoComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InfoSaldoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
