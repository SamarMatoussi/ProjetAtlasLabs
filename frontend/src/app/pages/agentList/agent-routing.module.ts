import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { GestionEmployeComponent } from './employe/list.component';
import { EvaluerComponent } from './Evaluer/evaluer.component';
import { GestionEmployeNoteComponent } from './employeNote/list.component';






const routes: Routes = [
   {
    path:"employe",
    component:GestionEmployeComponent
   },
   {
    path:"Notation",
    component:GestionEmployeNoteComponent
   },
   {
    path: 'noter/:employeCin',
    component: EvaluerComponent
},


];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class AgentRoutingModule {}
