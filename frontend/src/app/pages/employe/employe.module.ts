import { NgModule } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BsDropdownModule } from 'ngx-bootstrap/dropdown';
import { CollapseModule } from 'ngx-bootstrap/collapse';
import { PaginationModule } from 'ngx-bootstrap/pagination';
import { BsDatepickerModule } from 'ngx-bootstrap/datepicker';
import { UIModule } from '../../shared/ui/ui.module';
import { NoteEmployeComponent } from './note/note.component';
import { EmployeRoutingModule } from './employe-routing.module';
import { TabsModule } from 'ngx-bootstrap/tabs';
import { ModalModule } from 'ngx-bootstrap/modal';
import { NgxDropzoneModule } from 'ngx-dropzone';
import { NgSelectModule } from '@ng-select/ng-select';
import { NgxSliderModule } from 'ngx-slider-v2';
import { WidgetModule } from 'src/app/shared/widget/widget.module';
import { HttpClientModule } from '@angular/common/http';




@NgModule({
  declarations: [
    NoteEmployeComponent,
    
   
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    BsDatepickerModule.forRoot(),
    PaginationModule.forRoot(),
    BsDropdownModule.forRoot(),
    CollapseModule.forRoot(),
    TabsModule.forRoot(),
    ModalModule.forRoot(),
    NgxDropzoneModule,
    UIModule,
    WidgetModule,
    NgxSliderModule,
    NgSelectModule,
    EmployeRoutingModule,
    HttpClientModule
    
  ] , providers :[ DecimalPipe]
})

export class EmployeModule { }
