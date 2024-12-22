import { Component, OnInit, ViewChild, TemplateRef } from '@angular/core';
import { Observable } from 'rxjs';
import { BsModalService, BsModalRef } from 'ngx-bootstrap/modal';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import Swal from 'sweetalert2';
import { GestionUtilisateur } from './list.model';
import { GestionUtilisateurService } from './list.service';
import { GestionAgence } from '../agence/agence.model';
import { GestionAgenceService } from '../agence/agence.service';

@Component({
  selector: 'app-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.scss'],
  providers: [GestionUtilisateurService]
})

export class GestionUtilisateurComponent implements OnInit {
  agentForm: FormGroup;
  listegestionUser!: GestionUtilisateur[];
  gestionUserbyid: GestionUtilisateur = {
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
  gestionUtilisateur!: Observable<GestionUtilisateur[]>;
  total: Observable<number>;
  submitted = false;
  selectedStatut = '';
  selectedRole = '';

  @ViewChild('addContent') addContent: TemplateRef<any>;
  @ViewChild('updateContent') updateContent: TemplateRef<any>;
  @ViewChild('viewContent') viewContent: TemplateRef<any>;

  modalRef: BsModalRef<unknown>;
  currentPage: any;
  selectedAgent: GestionUtilisateur | undefined;
  listeAgence: GestionAgence[] = []; 

  constructor(private modalService: BsModalService, public service: GestionUtilisateurService, private formBuilder: FormBuilder ,public serviceAgence:GestionAgenceService) {
    this.total = service.total$;
    this.agentForm = this.formBuilder.group({
      id: [null],
      firstname: ['', Validators.required],
      lastname: ['', Validators.required],
      cin: ['', [Validators.required, Validators.pattern('^[0-9]{8}$')]], // Doit être exactement 8 chiffres
      phone: ['', [Validators.required, Validators.pattern('^[0-9]{8}$')]],       email: ['', [Validators.required, Validators.email]], // Validation pour l'email
      password: ['', [Validators.required, Validators.minLength(6)]], // Validation pour le mot de passe
      agenceId: [null, Validators.required] // Champ pour l'ID du poste

          });
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
getAvailableAgences() {
  return this.listeAgence.filter(agence => 
    !this.listegestionUser.some(user => user.agenceId === agence.id)
  );
}

  openViewModal(data: GestionUtilisateur) {
    this.selectedAgent = data;
    this.modalRef = this.modalService.show(this.viewContent, { class: 'modal-md' });
  }

  openAddModal() {
    this.submitted = false;
    this.modalRef = this.modalService.show(this.addContent, { class: 'modal-md' });
  }

  openUpdateModal(data: GestionUtilisateur) {
    this.submitted = false;
    this.agentForm.patchValue(data);
    this.modalRef = this.modalService.show(this.updateContent, { class: 'modal-md' });
  }

  ngOnInit(): void {
    this.getuserliste();
    this.loadAgences();
  }
  close() {
    this.modalRef?.hide();
  }

  getuserliste() {
    if (!this.listeAgence || this.listeAgence.length === 0) {
      console.warn('La liste des agences est vide ou non initialisée.');
      return;
    }
  
    const agenceMap = new Map(this.listeAgence.map(agence => [agence.id, agence.nom]));
  
    this.service.getUserliste().subscribe({
      next: (data) => {
        this.listegestionUser = data.map(user => ({
          ...user,
          agenceNom: agenceMap.get(user.agenceId) || 'Non attribué',
        }));
        console.log('Utilisateurs après ajout des noms des agences :', this.listegestionUser);
      },
      error: (err) => {
        console.error('Erreur lors de la récupération des utilisateurs :', err);
        this.showError('Erreur lors du chargement de la liste des utilisateurs.');
      }
    });
  }
  
  private showError(message: string) {
    Swal.fire('Erreur!', message, 'error');
  }
  searchUser(searchTerm: string): void {
    // Vérifiez d'abord si le terme de recherche est vide
    if (!searchTerm.trim()) {
      // Si le terme de recherche est vide, réinitialisez la liste avec la liste complète
      this.getuserliste();
      return;
    }
  
    // Filtrer la liste des utilisateurs en fonction du terme de recherche
    this.listegestionUser = this.listegestionUser.filter(user =>
      user.firstname.toLowerCase().includes(searchTerm.toLowerCase()) ||
      user.lastname.toLowerCase().includes(searchTerm.toLowerCase()) ||
      user.phone.toLowerCase().includes(searchTerm.toLowerCase()) ||
      user.email.toLowerCase().includes(searchTerm.toLowerCase()) ||
      user.cin.toString().includes(searchTerm.toLowerCase())
    );
  }
  
  loadAgences() {
    this.serviceAgence.getAgenceliste().subscribe({
      next: (data) => {
        this.listeAgence = data;
        console.log('Liste des agences chargée :', this.listeAgence);
        this.getuserliste(); // Appeler getuserliste après le chargement des agences
      },
      error: (err) => {
        console.error('Erreur lors du chargement des agences :', err);
        this.showError('Erreur lors du chargement des agences.');
      }
    });
  }

  adduser(): void {
    if (!this.submitted) {
      this.service.addUser(this.agentForm.value)
        .subscribe({
          next: (res) => {
            this.getuserliste();
            this.modalRef.hide();
          }
        });
    }
  }
  
  deleteuser(id: number) {
    Swal.fire({
      title: 'Êtes-vous sûr ?',
      text: 'Voulez-vous vraiment supprimer cet agent ? Cette action est irréversible !',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Oui, supprimez-le !',
      cancelButtonText: 'Non, gardez-le'
    }).then((result) => {
      if (result.isConfirmed) {
        this.service.delateUser(id)
          .subscribe(res => {
            this.getuserliste();
          });
        Swal.fire(
          'Supprimé !',
          'L\'agent a été supprimé avec succès.',
          'success'
        );
      } else if (result.dismiss === Swal.DismissReason.cancel) {
        Swal.fire(
          'Annulé',
          'L\'agent n\'a pas été supprimé.',
          'info'
        );
      }
    });
  }
  

  

  updateuser(): void {
    const idToUpdate = this.agentForm.get('id')?.value;
    if (!this.submitted && idToUpdate) {
      this.service.updateUser(idToUpdate, this.agentForm.value)
        .subscribe({
          next: (res) => {
            this.getuserliste();
            this.modalRef.hide();
          }
        });
    }
  }
  userdetail(id: number): void {
    this.service.userbyid(id)
      .subscribe({
        next: (data) => {
          this.gestionUserbyid = data;
          console.log('Données de l’utilisateur :', data); // Log des données
  
          // Vérifiez si data contient la propriété isEnabled et affectez-la à gestionUserbyid
          if ('isEnabled' in data) {
            this.gestionUserbyid.isEnabled = data.isEnabled;
            console.log('isEnabled:', this.gestionUserbyid.isEnabled); // Log de la valeur de isEnabled
          } else {
            console.log('Propriété isEnabled non trouvée dans les données.'); // Log si isEnabled n'existe pas
          }
        },
        error: (err) => {
          console.error('Erreur lors de la récupération des données utilisateur :', err); // Log de l'erreur
        }
      });
  }
  
  toggleAccount(cin: number, activate: boolean) {
    // Message de confirmation avant l'action
    Swal.fire({
      title: `Êtes-vous sûr de vouloir ${activate ? 'activer' : 'désactiver'} ce compte ?`,
      text: "Cette action peut être réversible.",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Oui, continuer',
      cancelButtonText: 'Annuler'
    }).then((result) => {
      if (result.isConfirmed) {
        // Si l'utilisateur confirme, on procède avec l'activation/désactivation
        this.service.revokeAccount(cin, activate).subscribe(
          response => {
            // Mettre à jour les données de l'utilisateur avec la nouvelle valeur d'activation
            const userIndex = this.listegestionUser.findIndex(user => user.cin === cin);
            if (userIndex !== -1) {
              this.listegestionUser[userIndex].isEnabled = activate;
            }
  
            // Alerte succès
            Swal.fire({
              title: 'Succès',
              text: `Le compte a été ${activate ? 'activé' : 'désactivé'} avec succès.`,
              icon: 'success',
              confirmButtonText: 'OK'
            });
          },
          error => {
            console.error('Erreur lors de la révocation du compte :', error);
  
            // Alerte erreur
            Swal.fire({
              title: 'Erreur',
              text: 'Une erreur est survenue lors de la mise à jour du statut du compte.',
              icon: 'error',
              confirmButtonText: 'OK'
            });
          }
        );
      } else if (result.dismiss === Swal.DismissReason.cancel) {
        // Si l'utilisateur annule, une alerte d'annulation peut être affichée
        Swal.fire(
          'Annulé',
          'L\'action a été annulée.',
          'info'
        );
      }
    });
  }
  
  

  pageChanged(event: any) {
    this.currentPage = event.page;
  }
}