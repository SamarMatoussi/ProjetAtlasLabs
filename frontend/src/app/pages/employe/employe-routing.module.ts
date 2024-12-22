import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { NoteEmployeComponent } from './note/note.component';


const routes: Routes = [
   {
    path:"notes",
    component:NoteEmployeComponent
   },
  

];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class EmployeRoutingModule {}
