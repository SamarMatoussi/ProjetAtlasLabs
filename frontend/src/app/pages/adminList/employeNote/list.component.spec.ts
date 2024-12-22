import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmployeNoterComponent } from './list.component';

describe('ListComponent', () => {
  let component: EmployeNoterComponent;
  let fixture: ComponentFixture<EmployeNoterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EmployeNoterComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EmployeNoterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
