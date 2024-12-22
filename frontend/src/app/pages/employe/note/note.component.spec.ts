import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NoteEmployeComponent } from './note.component';

describe('ListComponent', () => {
  let component: NoteEmployeComponent;
  let fixture: ComponentFixture<NoteEmployeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NoteEmployeComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NoteEmployeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
