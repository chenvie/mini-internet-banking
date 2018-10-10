import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NotifListenerComponent } from './notif-listener.component';

describe('NotifListenerComponent', () => {
  let component: NotifListenerComponent;
  let fixture: ComponentFixture<NotifListenerComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NotifListenerComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NotifListenerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
