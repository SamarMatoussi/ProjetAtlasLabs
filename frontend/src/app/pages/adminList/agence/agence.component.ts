import { Component, OnInit, ViewChild, TemplateRef } from '@angular/core';
import { Observable } from 'rxjs';
import { BsModalService, BsModalRef } from 'ngx-bootstrap/modal';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import Swal from 'sweetalert2';
import { GestionAgence } from './agence.model';
import { GestionAgenceService } from './agence.service';

@Component({
  selector: 'app-list',
  templateUrl: './agence.component.html',
  styleUrls: ['./agence.component.scss'],
  providers: [GestionAgenceService]
})

export class GestionAgenceComponent implements OnInit {
  agenceForm: FormGroup;
  listeAgence!: GestionAgence[];
  gestionAgencebyid: GestionAgence = {
    nom: '',
    email: '',
    adresse: '',
    numeroTelephone: '',
    codePostal: '',
  };
  gestionAgence!: Observable<GestionAgence[]>;
  total: Observable<number>;
  submitted = false;
  selectedStatut = '';
  selectedRole = '';

  @ViewChild('addContent') addContent: TemplateRef<any>;
  @ViewChild('updateContent') updateContent: TemplateRef<any>;
  @ViewChild('viewContent') viewContent: TemplateRef<any>;

  modalRef: BsModalRef<unknown>;
  currentPage: any;
  selectedAgence: GestionAgence | undefined;

  constructor(private modalService: BsModalService, public service: GestionAgenceService, private formBuilder: FormBuilder) {
    this.total = service.total$;
    this.agenceForm = this.formBuilder.group({
        id: [null],
        nom: ['', Validators.required],
        adresse: ['', Validators.required],
        numeroTelephone: ['', [Validators.required, Validators.pattern('^[0-9]{8}$')]], // Exactement 8 chiffres
        codePostal: ['', [Validators.required, Validators.pattern('^[0-9]{4}$')]], // Exemple pour 5 chiffres
        email: ['', [Validators.required, Validators.email]],
    });
}
  private handleError(field: string): string {
    const errorMessages: { [key: string]: string } = {
        nom: 'Le nom est requis.',
        adresse: 'L\'adresse est requise.',
        numeroTelephone: 'Veuillez entrer un numéro de téléphone valide.',
        codePostal: 'Veuillez entrer un code postal valide.',
        email: 'Veuillez entrer une adresse email valide.',
    };
    return errorMessages[field] || 'Erreur de validation.';
}
  

  openViewModal(data: GestionAgence) {
    this.selectedAgence = data;
    this.modalRef = this.modalService.show(this.viewContent, { class: 'modal-md' });
  }

  openAddModal() {
    this.submitted = false;
    this.modalRef = this.modalService.show(this.addContent, { class: 'modal-md' });
  }

  openUpdateModal(data: GestionAgence) {
    this.submitted = false;
    this.agenceForm.patchValue(data);
    this.modalRef = this.modalService.show(this.updateContent, { class: 'modal-md' });
  }

  ngOnInit(): void {
    this.getagenceliste();
  }
  close() {
    this.modalRef?.hide();
  }

  getagenceliste() {
    this.service.getAgenceliste().subscribe({
      next: (data) => {
        this.listeAgence = data;
      },
      error: console.error
    });
  }

  deleteAgence(id: number) {
    Swal.fire({
      title: 'Êtes-vous sûr ?',
      text: 'Voulez-vous vraiment supprimer cette agence ? Cette action est irréversible !',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Oui, supprimez-la !',
      cancelButtonText: 'Non, gardez-la'
    }).then((result) => {
      if (result.isConfirmed) {
        this.service.deleteAgence(id).subscribe(res => {
          this.getagenceliste();
          Swal.fire(
            'Supprimé !',
            'L\'agence a été supprimée avec succès.',
            'success'
          );
        });
      } else if (result.dismiss === Swal.DismissReason.cancel) {
        Swal.fire(
          'Annulé',
          'L\'agence n\'a pas été supprimée.',
          'info'
        );
      }
    });
  }
  

  addagence(): void {
    this.submitted = true; // Marquer le formulaire comme soumis

    // Vérifiez si le formulaire est valide avant d'ajouter l'agence
    if (this.agenceForm.valid) {
        this.service.addAgence(this.agenceForm.value).subscribe({
            next: (res) => {
                this.getagenceliste(); // Récupérer la liste des agences
                this.modalRef.hide();   // Fermer le modal
                this.agenceForm.reset(); // Réinitialiser le formulaire après soumission
                this.submitted = false;  // Réinitialiser l'état de soumission
            },
            error: (err) => {
                // Gérez les erreurs ici, par exemple afficher un message d'erreur
                console.error('Erreur lors de l\'ajout de l\'agence:', err);
            }
        });
    }
}

  searchAgence(searchTerm: string): void {
    // Vérifiez d'abord si le terme de recherche est vide
    if (!searchTerm.trim()) {
      // Si le terme de recherche est vide, réinitialisez la liste avec la liste complète
      this.getagenceliste();
      return;
    }
  
    // Filtrer la liste des agences en fonction du terme de recherche
    this.listeAgence = this.listeAgence.filter(agence =>
      agence.nom.toLowerCase().includes(searchTerm.toLowerCase()) ||
       agence.adresse.toLowerCase().includes(searchTerm.toLowerCase()) ||
       agence.numeroTelephone.toLowerCase().includes(searchTerm.toLowerCase()) ||
       agence.codePostal.toLowerCase().includes(searchTerm.toLowerCase())||
       agence.email.toLowerCase().includes(searchTerm.toLowerCase())

    );
  }
  
  updateagence(): void {
    this.submitted = true; 

    const idToUpdate = this.agenceForm.get('id')?.value;
    if (this.agenceForm.valid && idToUpdate) {
        this.service.updateAgence(idToUpdate, this.agenceForm.value)
            .subscribe({
                next: (res) => {
                    this.getagenceliste();
                    this.modalRef.hide();
                    this.agenceForm.reset(); // Réinitialiser le formulaire après mise à jour
                    this.submitted = false; // Réinitialiser l'état de soumission
                },
                error: (err) => {
                    console.error('Erreur lors de la mise à jour de l\'agence:', err);
                }
            });
    }
}

  agencedetail(id: number): void {
    this.service.agencebyid(id)
      .subscribe({
        next: (data) => {
          this.gestionAgencebyid = data;
          console.log(data);
        },
        error: console.error
      });
  }
  

  pageChanged(event: any) {
    this.currentPage = event.page;
  }
}
