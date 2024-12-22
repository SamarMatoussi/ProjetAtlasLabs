import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { GestionUtilisateurComponent} from "./gestionUtilisateur/list.component"; 
import { GestionAgenceComponent } from './agence/agence.component';
import { ActiviteComponent } from './activites/activite.component';
import { NoteUploadComponent } from './uploads/NoteUpload.component';
import { KpiComponent } from './gestionKpi/kpi.component';
import { PosteComponent } from './poste/poste.component';
import { EmployeNoterComponent } from './employeNote/list.component';
import { EvaluationComponent } from './evaluation/evaluation.component';


const routes: Routes = [
   {
    path:"gestionAgent",
    component:GestionUtilisateurComponent
   },
   {
    path:"listAgence",
    component:GestionAgenceComponent
   },
   {
    path:"activites",
    component:ActiviteComponent
   },
   {
    path:"UploadsComponent",
    component:NoteUploadComponent
   },
   {
    path:"kpis/:activiteId",
    component:KpiComponent
   },
  
   {
    path:"poste",
    component:PosteComponent
   },
   {
    path:"EvaluerEmployés",
    component:EmployeNoterComponent
   },
   {
    path: 'note/:idEmploye',
    component: EvaluationComponent
},
  
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class AdminRoutingModule {}
