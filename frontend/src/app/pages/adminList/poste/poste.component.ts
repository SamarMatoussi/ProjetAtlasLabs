import { Component, OnInit, ViewChild, TemplateRef } from '@angular/core';
import { Observable } from 'rxjs';
import { BsModalService, BsModalRef } from 'ngx-bootstrap/modal';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import Swal from 'sweetalert2';
import { PosteService } from './poste.service';
import { Poste } from './poste.model';

@Component({
  selector: 'app-list',
  templateUrl: './poste.component.html',
  styleUrls: ['./poste.component.scss'],
  providers: [PosteService]
})

export class PosteComponent implements OnInit {
  posteForm: FormGroup;
  listeposte: Poste[] = [];
  postebyid: Poste = {
    name: '',
    description: '',
  };
  poste!: Observable<Poste[]>;
  total: Observable<number>;
  submitted = false;
  selectedStatut = '';
  selectedRole = '';

  @ViewChild('addContent') addContent: TemplateRef<any>;
  @ViewChild('updateContent') updateContent: TemplateRef<any>;
  @ViewChild('viewContent') viewContent: TemplateRef<any>;

  modalRef: BsModalRef<unknown>;
  currentPage: any;
  selectedPoste: Poste | undefined;

  constructor(private modalService: BsModalService, public posteservice: PosteService, private formBuilder: FormBuilder) {
    this.total = posteservice.total$;
    this.posteForm = this.formBuilder.group({
      id: [null],
      name: [''],
      description: [''],
      
    });
  }

  openViewModal(data: Poste) {
    this.selectedPoste = data;
    this.modalRef = this.modalService.show(this.viewContent, { class: 'modal-md' });
  }

  openAddModal() {
    this.submitted = false;
    this.modalRef = this.modalService.show(this.addContent, { class: 'modal-md' });
  }

  openUpdateModal(data: Poste) {
    this.submitted = false;
    this.posteForm.patchValue(data);
    this.modalRef = this.modalService.show(this.updateContent, { class: 'modal-md' });
  }

  ngOnInit(): void {
    this.getlistePoste();
  }

  close(): void {
    this.modalRef?.hide();
  }
  getlistePoste() {
    this.posteservice.getListePoste().subscribe({
      next: (data) => {
        console.log('Liste des postes récupérée :', data);
        this.listeposte = data || [];
      },
      error: (err) => {
        console.error('Erreur lors de la récupération des postes:', err);
      },
    });
  }
  
  addPoste(): void {
    this.submitted = true; // Marquer le formulaire comme soumis

    // Vérifiez si le formulaire est valide avant d'ajouter l'agence
    if (this.posteForm.valid) {
        this.posteservice.addPoste(this.posteForm.value).subscribe({
            next: (res) => {
                this.getlistePoste(); // Récupérer la liste des agences
                this.modalRef.hide();   // Fermer le modal
                this.posteForm.reset(); // Réinitialiser le formulaire après soumission
                this.submitted = false;  // Réinitialiser l'état de soumission
            },
            error: (err) => {
                // Gérez les erreurs ici, par exemple afficher un message d'erreur
                console.error('Erreur lors de l\'ajout de la poste:', err);
            }
        });
    }
}
  deletePoste(id: number) {
    Swal.fire({
      title: 'Êtes-vous sûr ?',
      text: 'Voulez-vous vraiment supprimer cette poste ? Cette action est irréversible !',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Oui, supprimez-la !',
      cancelButtonText: 'Non, gardez-la'
    }).then((result) => {
      if (result.isConfirmed) {
        this.posteservice.deletePoste(id).subscribe(
          res => {
            this.getlistePoste();
            Swal.fire(
              'Supprimé !',
              'La poste a été supprimée avec succès.',
              'success'
            );
          },
          error => {
            Swal.fire(
              'Erreur',
              'Une erreur est survenue lors de la suppression de la poste.',
              'error'
            );
          }
        );
      } else if (result.dismiss === Swal.DismissReason.cancel) {
        Swal.fire(
          'Annulé',
          'La poste n\'a pas été supprimée.',
          'info'
        );
      }
    });
  }
  searchPoste(searchTerm: string): void {
    // Vérifiez d'abord si le terme de recherche est vide
    if (!searchTerm.trim()) {
      // Si le terme de recherche est vide, réinitialisez la liste avec la liste complète
      this.getlistePoste();
      return;
    }
  
    // Filtrer la liste des activités en fonction du terme de recherche
    this.listeposte = this.listeposte.filter(activite =>
      activite.name.toLowerCase().includes(searchTerm.toLowerCase()) 
    );
  }
  
  addactivite(): void {
    if (!this.submitted) {
      this.posteservice.addPoste(this.posteForm.value)
        .subscribe({
          next: (res) => {
            this.getlistePoste();
            this.modalRef.hide();
          }
        });
    }
  }

  updateposte(): void {
    const idToUpdate = this.posteForm.get('id')?.value;
    if (!this.submitted && idToUpdate) {
      this.posteservice.updatePoste(idToUpdate, this.posteForm.value)
        .subscribe({
          next: (res) => {
            this.getlistePoste();
            this.modalRef.hide();
          }
        });
    }
  }
  postedetail(id: number): void {
    this.posteservice.postebyid(id)
      .subscribe({
        next: (data) => {
          this.postebyid = data;
          console.log("TESTTTT");
          console.log(data);
          
        },
        error: console.error
      });
  }
  

  pageChanged(event: any) {
    this.currentPage = event.page;
  }
}
