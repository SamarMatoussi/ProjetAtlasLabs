import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { NoteUploadComponent } from './NoteUpload.component';

describe('UploadsComponent', () => {
  let component: NoteUploadComponent;
  let fixture: ComponentFixture<NoteUploadComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ NoteUploadComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NoteUploadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
