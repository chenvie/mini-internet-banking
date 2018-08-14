import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HistoriComponent } from './histori.component';

describe('HistoriComponent', () => {
  let component: HistoriComponent;
  let fixture: ComponentFixture<HistoriComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ HistoriComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HistoriComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
