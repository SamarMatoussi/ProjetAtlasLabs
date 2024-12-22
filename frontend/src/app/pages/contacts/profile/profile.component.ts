import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ProfileService } from './profile.service';  
import Swal from 'sweetalert2';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
})
export class ProfileComponent implements OnInit {
  userProfile: any;
  isEditMode = false;
  showPassword = false;
  editFormGroup: FormGroup;

  constructor(
    private fb: FormBuilder,
    private profileService: ProfileService
  ) {}

  ngOnInit(): void {
    this.profileService.getProfile().subscribe((profile) => {
      this.userProfile = profile;
      this.initForm();
    });
  }

  // Initialiser le formulaire
  initForm() {
    this.editFormGroup = this.fb.group({
      firstname: [this.userProfile?.firstname, Validators.required],
      lastname: [this.userProfile?.lastname, Validators.required],
      phone: [this.userProfile?.phone, [Validators.required, Validators.pattern(/^\d+$/)]],
      email: [{ value: this.userProfile?.email, disabled: true }],
      cin: [{ value: this.userProfile?.cin, disabled: true }],
    });
  }
  

  // Vérifier la correspondance des mots de passe
  passwordMatchValidator(form: FormGroup) {
    return form.get('newPassword')?.value === form.get('confirmPassword')?.value
      ? null
      : { passwordMismatch: true };
  }

  // Passer en mode édition
  toggleEditMode() {
    this.isEditMode = !this.isEditMode;
    if (!this.isEditMode) {
      this.initForm();  // Réinitialiser le formulaire si l'édition est annulée
    }
  }

  onSubmit() {
    if (this.editFormGroup.valid) {
      this.profileService
        .updateProfile(this.userProfile.id, this.editFormGroup.value)
        .subscribe(
          (updatedProfile) => {
            this.userProfile = updatedProfile;
            this.toggleEditMode(); // Quitter le mode édition après la mise à jour
            // Afficher l'alerte de succès après la mise à jour du profil
            Swal.fire({
              title: 'Succès',
              text: 'Profil modifié avec succès',
              icon: 'success',
              confirmButtonText: 'Ok'
            });
          },
          (error) => {
            console.error('Erreur lors de la mise à jour du profil', error);
          }
        );
    }
  }
  

  // Afficher/Masquer le mot de passe
  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }
}
