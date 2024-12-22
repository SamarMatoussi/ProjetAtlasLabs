import { Component, OnInit, ViewChild, TemplateRef } from '@angular/core';
import { Observable } from 'rxjs';
import { BsModalService, BsModalRef } from 'ngx-bootstrap/modal';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import Swal from 'sweetalert2';
import { Activite } from './activite.model';
import { ActiviteService } from './activite.service';
import { Router } from '@angular/router';
import { PosteService } from '../poste/poste.service';
import { Poste } from '../poste/poste.model';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-list',
  templateUrl: './activite.component.html',
  styleUrls: ['./activite.component.scss'],
  providers: [ActiviteService]
})
export class ActiviteComponent implements OnInit {
  activiteForm: FormGroup;
  activites: Activite[] = [];
  total: Observable<number>;
  submitted = false;
  listeposte!: Poste[];
  selectedPosteIds: number[] = [];
  isLoading: boolean = false;

  @ViewChild('addContent') addContent: TemplateRef<any>;
  @ViewChild('updateContent') updateContent: TemplateRef<any>;
  @ViewChild('viewContent') viewContent: TemplateRef<any>;

  modalRef: BsModalRef<unknown>;
  selectedActivite: Activite | undefined;

  constructor(
    private modalService: BsModalService,
    public service: ActiviteService,
    private formBuilder: FormBuilder,
    private router: Router,
    public posteservice: PosteService
  ) {
    this.activiteForm = this.formBuilder.group({
      id: [null],
      name: ['', Validators.required],
      description: ['', Validators.required],
      posteIds: [[]]
    });

    this.total = service.total$;
  }
  private handleError(field: string) { 
    const errorMessages: { [key: string]: string } = {
        name: 'Le nom est requis.',
        description: 'Le description est requis.',
       
    };
    return errorMessages[field] || 'Erreur de validation.';
}

  openViewModal(data: Activite) {
    this.selectedActivite = data;
    this.modalRef = this.modalService.show(this.viewContent, { class: 'modal-md' });
  }

  openAddModal() {
    this.submitted = false;
    this.activiteForm.reset();
    this.modalRef = this.modalService.show(this.addContent, { class: 'modal-md' });
  }

  openUpdateModal(data: Activite) {
    this.submitted = false;
    this.activiteForm.patchValue(data);
    this.modalRef = this.modalService.show(this.updateContent, { class: 'modal-md' });
  }

  ngOnInit(): void {
    this.getlisteActivite();
    this.getlistePoste();
  }

  close() {
    this.modalRef?.hide();
  }

  getlisteActivite(): void {
    this.isLoading = true; // Démarrer le spinner
    this.service.getListeActivite().subscribe({
      next: (data) => {
        this.activites = data;
      },
      error: console.error,
      complete: () => {
        this.isLoading = false; // Arrêter le spinner
      }
    });
  }
  
   // Méthode pour gérer le changement de sélection des postes
   onPosteChange(event: any) {
    const posteId = +event.target.value;
    if (event.target.checked) {
      this.selectedPosteIds.push(posteId); // Ajouter l'ID si sélectionné
    } else {
      const index = this.selectedPosteIds.indexOf(posteId);
      if (index > -1) {
        this.selectedPosteIds.splice(index, 1); // Retirer l'ID si désélectionné
      }
    }
    this.activiteForm.patchValue({ posteIds: this.selectedPosteIds }); // Mettre à jour le groupe de formulaires
  }

  getlistePoste() {
    this.posteservice.getListePoste().subscribe({
      next: (data) => {
        this.listeposte = data;
      },
      error: console.error
    });
  }

  deleteactivite(id: number) {
    Swal.fire({
      title: 'Are you sure?',
      text: 'You will not be able to recover this imaginary file!',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Yes, delete it!',
      cancelButtonText: 'No, keep it'
    }).then((result) => {
      if (result.isConfirmed) {
        this.service.delateActivite(id).subscribe({
          next: () => {
            this.getlisteActivite();
            Swal.fire('Deleted!', 'Your imaginary file has been deleted.', 'success');
          },
          error: () => {
            Swal.fire('Error', 'Could not delete the activity.', 'error');
          }
        });
      } else {
        Swal.fire('Cancelled', 'Your imaginary file is safe 🙂', 'error');
      }
    });
  }

  searchActivite(searchTerm: string): void {
    if (!searchTerm.trim()) {
      this.getlisteActivite();
      return;
    }

    this.service.getListeActivite().pipe(
      map(activites => activites.filter(activite =>
        activite.name.toLowerCase().includes(searchTerm.toLowerCase())
      ))
    ).subscribe(filteredActivites => {
      this.activites = filteredActivites; // Mettez à jour les activités filtrées
    });
  }

  addPostesToActivite(activiteId: number, posteIds: number[]): void {
    console.log('Activite ID:', activiteId); // Vérifiez si activiteId est défini
  
    if (!activiteId) {
      Swal.fire('Error', 'Activity ID is not defined.', 'error');
      return;
    }
    this.isLoading = true; // Démarrer le spinner

    this.service.addPostesToActivite(activiteId, posteIds).subscribe({
      next: () => {
        this.getlisteActivite();
        this.modalRef.hide();
        Swal.fire('Success', 'Postes added to activity!', 'success');
      },
      error: (err) => {
        console.error('Error adding postes to activity', err);
        Swal.fire('Error', 'Could not add postes to the activity.', 'error');
      }
    });
  }
  
  

  addactivite(): void {
    if (this.activiteForm.invalid) {
      this.submitted = true;
      return;
    }
    this.isLoading = true; // Démarrer le spinner

    const newActivite = { ...this.activiteForm.value, posteIds: this.selectedPosteIds };
  
    this.service.addActivite(newActivite).subscribe({
      next: () => {
        this.activites.push(newActivite); // Ajoutez directement à la liste des activités
        this.modalRef.hide();
        Swal.fire('Success', 'Activity added successfully!', 'success');
        console.log('Activité ajoutée:', newActivite);
      },
    });
  }
  
  

  updateactivite(): void {
    if (this.activiteForm.invalid) {
      this.submitted = true;
      return;
    }
    this.isLoading = true; // Démarrer le spinner

    const idToUpdate = this.activiteForm.get('id')?.value;
    this.service.updateActivite(idToUpdate, this.activiteForm.value).subscribe({
      next: () => {
        this.getlisteActivite();
        this.modalRef.hide();
        Swal.fire('Success', 'Activity updated successfully!', 'success');
      },
      error: () => {
        Swal.fire('Error', 'Could not update the activity.', 'error');
      }
    });
  }

  manageKpiByActivity(id: number) {
    this.router.navigate(['kpis', id]);
  }



  

  removePostesFromActivite(activiteId: number, posteIds: number[]): void {
    this.service.removePostesFromActivite(activiteId, posteIds).subscribe({
      next: () => {
        this.getlisteActivite();
        this.modalRef.hide();
        Swal.fire('Success', 'Postes removed from activity!', 'success');
      },
      error: () => {
        Swal.fire('Error', 'Could not remove postes from the activity.', 'error');
      }
    });
  }
}
