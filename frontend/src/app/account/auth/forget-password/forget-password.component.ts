import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { AuthenticationService } from 'src/app/core/services/auth.service';

@Component({
  selector: 'app-forget-password',
  templateUrl: './forget-password.component.html',
  styleUrls: ['./forget-password.component.scss']
})
export class ForgetPasswordComponent implements OnInit {
  forgetPasswordForm: UntypedFormGroup;
  resetPasswordForm: UntypedFormGroup;
  error: string = '';
  successMessage: string = '';
  step: number = 1;  // Étape initiale (1: Email, 2: Token, 3: Nouveau mot de passe)
  loading: boolean = false;

  constructor(
    private formBuilder: UntypedFormBuilder,
    private authService: AuthenticationService
  ) { }

  ngOnInit(): void {
    // Formulaire pour envoyer l'email
    this.forgetPasswordForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
    });

    // Formulaire pour réinitialiser le mot de passe
    this.resetPasswordForm = this.formBuilder.group({
      token: ['', [Validators.required]], // Champ token ajouté
      newPassword: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', [Validators.required]],
    }, { validator: this.passwordMatchValidator });
  }

  // Validation pour vérifier que les mots de passe correspondent
  passwordMatchValidator(form: UntypedFormGroup) {
    return form.get('newPassword')?.value === form.get('confirmPassword')?.value
      ? null : { 'mismatch': true };
  }

  // Getter pour accéder aux contrôles du formulaire
  get f() { return this.forgetPasswordForm.controls; }
  get r() { return this.resetPasswordForm.controls; }

  // Soumettre l'email pour recevoir un token
  onSubmitEmail() {
    if (this.forgetPasswordForm.invalid) {
      return;
    }
  
    const email = this.f.email.value;
    this.loading = true;
  
    this.authService.forgotPassword(email).subscribe(
      response => {
        this.clearMessages(); // Réinitialiser les messages
        this.successMessage = 'Instructions envoyées à votre email.';
        this.step = 2; // Passer à l'étape suivante
        this.loading = false;
      },
      error => {
        this.clearMessages();
        this.error = 'Une erreur est survenue. Veuillez vérifier votre email et réessayer.';
        this.loading = false;
      }
    );
  }
  

  // Vérifier le token (Optionnel, ici simple changement d'étape)
  onSubmitToken() {
    this.clearMessages(); 
    if (!this.resetPasswordForm.value.token) {
      this.error = 'Le token est requis.';
      return;
    }
    this.error = '';
    this.step = 3; // Passer à l'étape 3
  }

  // Soumettre le nouveau mot de passe
  onSubmitResetPassword() {
    if (this.resetPasswordForm.invalid) {
      return;
    }

    const token = this.resetPasswordForm.value.token;
    const newPassword = this.resetPasswordForm.value.newPassword;

    this.authService.resetPassword(token, newPassword).subscribe(
      response => {
        this.clearMessages(); 
        this.successMessage = response; // Message de succès
        this.error = '';
        this.step = 3; // Afficher confirmation
      },
      error => {
        this.error = error.message; // Message d'erreur
      }
    );
  }
  clearMessages(): void {
    this.successMessage = '';
    this.error = '';
  }
  
}
