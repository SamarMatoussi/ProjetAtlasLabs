import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GestionUtilisateurComponent } from './list.component';

describe('ListComponent', () => {
  let component: GestionUtilisateurComponent;
  let fixture: ComponentFixture<GestionUtilisateurComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GestionUtilisateurComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GestionUtilisateurComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
