import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GestionEmployeComponent } from './list.component';

describe('ListComponent', () => {
  let component: GestionEmployeComponent;
  let fixture: ComponentFixture<GestionEmployeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GestionEmployeComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GestionEmployeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
