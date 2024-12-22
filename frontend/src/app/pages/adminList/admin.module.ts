import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BsDropdownModule } from 'ngx-bootstrap/dropdown';
import { CollapseModule } from 'ngx-bootstrap/collapse';
import { PaginationModule } from 'ngx-bootstrap/pagination';
import { BsDatepickerModule } from 'ngx-bootstrap/datepicker';
import { UIModule } from '../../shared/ui/ui.module';
import { GestionUtilisateurComponent } from './gestionUtilisateur/list.component';
import { GestionAgenceComponent } from './agence/agence.component';
import { AdminRoutingModule } from './admin-routing.module';
import { ActiviteComponent } from './activites/activite.component';
import { NgxDropzoneModule } from 'ngx-dropzone';
import { HttpClientModule } from '@angular/common/http';
import { KpiComponent } from './gestionKpi/kpi.component';
import { PosteComponent } from './poste/poste.component';
import { NoteUploadComponent } from './uploads/NoteUpload.component';
import { EmployeNoterComponent } from './employeNote/list.component';
import { EvaluationComponent } from './evaluation/evaluation.component';
import { TabsModule } from 'ngx-bootstrap/tabs';


@NgModule({
  declarations: [
    GestionUtilisateurComponent,
    GestionAgenceComponent,
    ActiviteComponent,
    NoteUploadComponent,
    KpiComponent,
    PosteComponent,
    EmployeNoterComponent,
    EvaluationComponent
 
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    BsDatepickerModule.forRoot(),
    PaginationModule.forRoot(),
    BsDropdownModule.forRoot(),
    CollapseModule.forRoot(),
    UIModule,
    NgxDropzoneModule,
    HttpClientModule, 
    AdminRoutingModule,
    TabsModule.forRoot(),
  ]
})
export class AdminModule { }
