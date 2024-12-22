import { Component, OnInit, ViewChild, TemplateRef } from '@angular/core';
import { Observable } from 'rxjs';
import { BsModalService, BsModalRef } from 'ngx-bootstrap/modal';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import Swal from 'sweetalert2';
import { employe } from './list.model';
import { GestionEmployeService } from './list.service';
import { Router } from '@angular/router';
import { PosteService } from '../../adminList/poste/poste.service';
import { Poste } from '../../adminList/poste/poste.model';

@Component({
  selector: 'app-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.scss'],
  providers: [GestionEmployeService]
})
export class GestionEmployeComponent implements OnInit {
  employeForm: FormGroup;
  listegestionEmploye!: employe[];
  gestionEmployebyid: employe = {
    firstname: '',
    lastname: '',
    phone: '',
    cin: 0,
    email: '',
    password: '',
    role: '',
    type: undefined,
    type_color: undefined,
    status_color: undefined,
    status: 'success'
  };
  gestionEmploye!: Observable<employe[]>;
  total: Observable<number>;
  submitted = false;
  selectedStatut = '';
  selectedRole = '';
  @ViewChild('addContent') addContent: TemplateRef<any>;
  @ViewChild('updateContent') updateContent: TemplateRef<any>;
  @ViewChild('viewContent') viewContent: TemplateRef<any>;

  modalRef: BsModalRef<unknown>;
  currentPage: any;
  selectedAgent: employe | undefined;
  postes: Poste[] = []; 

  constructor(
    private modalService: BsModalService,
    public serviceEmp: GestionEmployeService,
    private formBuilder: FormBuilder,
    private router: Router,public posteservice:PosteService
  ) {
    this.total = serviceEmp.total$;
    this.employeForm = this.formBuilder.group({
      id: [null],
      firstname: ['', Validators.required],
      lastname: ['', Validators.required],
      cin: ['', [Validators.required, Validators.pattern('^[0-9]{8}$')]], 
      phone: ['', [Validators.required, Validators.pattern('^[0-9]{8}$')]],     
      email: ['', [Validators.required, Validators.email]], // Validation pour l'email
      password: ['', [Validators.required, Validators.minLength(6)]], 
      posteId: [null, Validators.required] // Champ pour l'ID du poste
    });
          this.loadPostes();
  }
  private handleError(field: string) { 
    const errorMessages: { [key: string]: string } = {
        firstname: 'Le prénom est requis.',
        lastname: 'Le nom est requis.',
        phone: 'Veuillez entrer un numéro de téléphone valide.',
        email: 'Veuillez entrer une adresse email valide.',
        password: 'Le mot de passe doit contenir au moins 6 caractères.',
        cin: 'Le numéro CIN est requis.'
    };
    return errorMessages[field] || 'Erreur de validation.';
}

  ngOnInit(): void {
    this.getemployeliste();
    this.loadPostes();

  }

  openViewModal(data: employe) {
    this.selectedAgent = data;
    this.modalRef = this.modalService.show(this.viewContent, { class: 'modal-md' });
  }

  openAddModal() {
    this.submitted = false;
    this.employeForm.reset(); // Reset le formulaire
    this.modalRef = this.modalService.show(this.addContent, { class: 'modal-md' });
  }

  openUpdateModal(data: employe) {
    this.submitted = false;
    this.employeForm.patchValue(data);
    this.modalRef = this.modalService.show(this.updateContent, { class: 'modal-md' });
  }

  close() {
    this.modalRef?.hide();
  }

  getemployeliste() {
    console.log('Appel de getEmployesByAuthenticatedAgent démarré.');
    
    this.serviceEmp.getEmployesByAuthenticatedAgent().subscribe({
      next: (data) => {
        console.log('Données reçues de l’API :', data);
  
        // Associez les postes aux employés en utilisant posteId pour obtenir poste.name
        this.listegestionEmploye = data.map(emp => {
          const poste = this.postes.find(poste => poste.id === emp.posteId);
          return {
            ...emp,
            posteName: poste ? poste.name : 'Non attribué'
          };
        });
  
        console.log('Employés après ajout des noms des postes:', this.listegestionEmploye);
      },
      complete: () => {
        console.log('Requête à getEmployesByAuthenticatedAgent terminée.');
      }
    });
  }
  
  
  
  loadPostes() {
    this.posteservice.getListePoste().subscribe({
      next: (data) => {
        this.postes = data;
        console.log('Liste des postes chargée :', this.postes);
      },
      error: (err) => {
        console.error('Erreur lors du chargement des postes :', err);
      }
    });
  }
  

  searchEmploye(searchTerm: string): void {
    if (!searchTerm.trim()) {
      this.getemployeliste();
      return;
    }

    this.listegestionEmploye = this.listegestionEmploye.filter(user =>
      user.firstname.toLowerCase().includes(searchTerm.toLowerCase()) ||
      user.lastname.toLowerCase().includes(searchTerm.toLowerCase()) ||
      user.phone.toLowerCase().includes(searchTerm.toLowerCase()) ||
      user.email.toLowerCase().includes(searchTerm.toLowerCase()) ||
      user.cin.toString().includes(searchTerm.toLowerCase())
    );
  }
/*
  deleteEmploye(id: number) {
    console.log('ID de l\'employé à supprimer:', id);  // Ajoutez ce log pour déboguer
  
    if (!id) {
      console.error('ID non valide:', id);
      Swal.fire('Erreur!', 'L\'ID de l\'employé est invalide.', 'error');
      return;
    }
  
    Swal.fire({
      title: 'Êtes-vous sûr de vouloir supprimer cet employé ?',
      text: 'Cette action est irréversible.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Oui, supprimer !',
      cancelButtonText: 'Annuler'
    }).then((result) => {
      if (result.isConfirmed) {
        this.serviceEmp.deleteEmploye(id).subscribe(
          () => {
            this.listegestionEmploye = this.listegestionEmploye.filter(emp => emp.id !== id);
            Swal.fire('Supprimé!', 'L\'employé a été supprimé avec succès.', 'success');
          },
          error => {
            console.error('Erreur lors de la suppression :', error);
            Swal.fire('Erreur!', 'Erreur lors de la suppression de l\'employé.', 'error');
          }
        );
      } else {
        Swal.fire('Annulé', 'L\'employé n\'a pas été supprimé.', 'error');
      }
    });
  }
  */
  
  deleteEmploye(cin: number) {
    console.log('CIN de l\'employé à supprimer:', cin);  // Ajoutez ce log pour déboguer
  
    if (!cin) {
      console.error('CIN non valide:', cin);
      Swal.fire('Erreur!', 'Le CIN de l\'employé est invalide.', 'error');
      return;
    }
  
    Swal.fire({
      title: 'Êtes-vous sûr de vouloir supprimer cet employé ?',
      text: 'Cette action est irréversible.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Oui, supprimer !',
      cancelButtonText: 'Annuler'
    }).then((result) => {
      if (result.isConfirmed) {
        this.serviceEmp.deleteEmploye(cin).subscribe(
          () => {
            this.listegestionEmploye = this.listegestionEmploye.filter(emp => emp.cin !== cin);
            Swal.fire('Supprimé!', 'L\'employé a été supprimé avec succès.', 'success');
          },
          error => {
            console.error('Erreur lors de la suppression :', error);
            Swal.fire('Erreur!', 'Erreur lors de la suppression de l\'employé.', 'error');
          }
        );
      } else {
        Swal.fire('Annulé', 'L\'employé n\'a pas été supprimé.', 'error');
      }
    });
  }
  
  

  addemploye(): void {
  console.log('Méthode addemploye appelée'); // Debug
  if (this.employeForm.invalid) {
    this.submitted = true;
    console.log('Formulaire invalide'); // Debug
    return;
  }

  console.log('Envoi des données :', this.employeForm.value); // Debug
  this.serviceEmp.addEmploye(this.employeForm.value).subscribe({
    next: () => {
      console.log('Employé ajouté avec succès'); // Debug
      this.getemployeliste();
      this.modalRef.hide();
      Swal.fire('Ajouté!', 'L\'employé a été ajouté avec succès.', 'success');
    },
    error: (err) => {
      console.error('Erreur lors de l\'ajout', err); // Debug
      if (err.error && err.error.includes('CIN déjà utilisé')) {
        Swal.fire('Erreur!', 'Le numéro de CIN est déjà utilisé.', 'error');
      } else if (err.error && err.error.includes('Email déjà utilisé')) {
        Swal.fire('Erreur!', 'L\'email est déjà utilisé.', 'error');
      } else {
        this.showError('Erreur lors de l\'ajout de l\'employé.');
      }
    }
  });
}




updateemploye(): void {
  const cinToUpdate = this.employeForm.get('cin')?.value;

  if (!cinToUpdate) {
    Swal.fire('Erreur!', 'Le CIN est requis pour la mise à jour.', 'error');
    return;
  }

  if (this.employeForm.invalid) {
    this.submitted = true;
    Swal.fire('Erreur!', 'Veuillez corriger les erreurs dans le formulaire.', 'error');
    return;
  }

  // Log des données envoyées pour déboguer
  console.log('Données envoyées à la mise à jour:', this.employeForm.value);

  // Appel au service de mise à jour
  this.serviceEmp.updateEmploye(cinToUpdate, this.employeForm.value).subscribe({
    next: () => {
      this.getemployeliste(); // Mettre à jour la liste
      this.modalRef.hide();  // Fermer le modal
      Swal.fire('Mis à jour!', 'L\'employé a été mis à jour avec succès.', 'success');
    },
    error: (err) => {
      console.error('Erreur lors de la mise à jour :', err);
      Swal.fire('Erreur!', 'Erreur lors de la mise à jour de l\'employé.', 'error');
    }
  });
}


  employedetail(id: number): void {
    this.serviceEmp.employebyid(id).subscribe({
      next: (data) => {
        this.gestionEmployebyid = data;
      },
      error: () => this.showError('Erreur lors de la récupération des détails de l\'employé.')
    });
  }

  toggleAccount(cin: number, activate: boolean) {
    const action = activate ? 'activer' : 'désactiver';
    
    Swal.fire({
      title: `Êtes-vous sûr de vouloir ${action} ce compte ?`,
      text: 'Cette action peut être réversible.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: `Oui, ${action} !`,
      cancelButtonText: 'Non, garder'
    }).then((result) => {
      if (result.isConfirmed) {
        this.serviceEmp.revokeAccount(cin, activate).subscribe(
          response => {
            // Mettre à jour la liste des employés
            const userIndex = this.listegestionEmploye.findIndex(user => user.cin === cin);
            if (userIndex !== -1) {
              this.listegestionEmploye[userIndex].isEnabled = activate;
              // Mettre à jour la vue
              this.listegestionEmploye = [...this.listegestionEmploye]; // Crée un nouveau tableau pour forcer le changement
              Swal.fire('Succès!', `Le compte a été ${activate ? 'activé' : 'désactivé'}.`, 'success');
            }
          },
          error => {
            console.error('Erreur lors de la révocation du compte :', error);
            this.showError('Erreur lors de la révocation du compte.');
          }
        );
      } else if (result.dismiss === Swal.DismissReason.cancel) {
        Swal.fire('Annulé', 'Aucune action effectuée 🙂', 'error');
      }
    });
  }
  
  
  

  pageChanged(event: any) {
    this.currentPage = event.page;
  }

  private showError(message: string) {
    Swal.fire('Erreur!', message, 'error');
  }
}
