import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GestionEmployeNoteComponent } from './list.component';

describe('ListComponent', () => {
  let component: GestionEmployeNoteComponent;
  let fixture: ComponentFixture<GestionEmployeNoteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GestionEmployeNoteComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GestionEmployeNoteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
