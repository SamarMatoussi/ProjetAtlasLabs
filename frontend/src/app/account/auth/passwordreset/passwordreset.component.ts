import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { PasswordresetService } from 'src/app/core/services/passworedrest.service';
import { ChangePasswordRequest } from 'src/app/core/models/changePasswordRequest';

@Component({
  selector: 'app-passwordreset',
  templateUrl: './passwordreset.component.html',
  styleUrls: ['./passwordreset.component.scss']
})
export class PasswordResetComponent implements OnInit {
  resetForm: FormGroup; 
  otpForm: FormGroup; 
  changePasswordForm: FormGroup; 
  loading = false; 
  success: string | null = null; 
  error: string | null = null; 
  submitted = false; 
  step = 1; 
  newPassword: string;
  confirmationPassword: string;
  constructor(
    private passwordResetService: PasswordresetService,
    private formBuilder: FormBuilder
  ) {}

  ngOnInit(): void {
    // Initialisation des formulaires
    this.resetForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
    });

    this.otpForm = this.formBuilder.group({
      otp: ['', [Validators.required, Validators.pattern(/^\d{6}$/)]], // Code OTP à 6 chiffres
    });

    this.changePasswordForm = this.formBuilder.group({
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]],
    }, { validators: this.passwordsMatchValidator });
  }

  // Getter pour les champs de formulaire
  get f() {
    if (this.step === 1) return this.resetForm.controls;
    if (this.step === 2) return this.otpForm.controls;
    return this.changePasswordForm.controls;
  }

  // Validation des mots de passe correspondants
  passwordsMatchValidator(group: FormGroup) {
    const password = group.get('password')?.value;
    const confirmPassword = group.get('confirmPassword')?.value;
    return password === confirmPassword ? null : { notMatching: true };
  }

  onVerifyEmail(): void {
    this.submitted = true;
  
    // Vérifie si le formulaire est invalide
    if (this.resetForm.invalid) {
      return;
    }
  
    this.loading = true;
    this.success = null;
    this.error = null;
  
    // Appelle le service pour vérifier l'email
    this.passwordResetService.verifyEmail(this.f.email.value)
      .subscribe({
        next: (response) => {
          console.log('Réponse du backend :', response);
          this.success = response; // Affiche la réponse du serveur
          this.step = 2;           // Passe à l'étape suivante
        },
        error: (err) => {
          console.error('Erreur :', err.message);
          this.error = err.message || 'Une erreur est survenue. Veuillez réessayer.';
        },
        complete: () => {
          this.loading = false;
        }
      });
  }
  
  
  
  onVerifyOtp(): void {
    this.submitted = true;

    if (this.otpForm.invalid) {
        return;
    }

    this.loading = true;
    this.success = null;
    this.error = null;

    const otp = this.f.otp.value;
    const email = this.resetForm.get('email')?.value;

    this.passwordResetService.verifyOtp(otp, email)
        .subscribe({
            next: (response) => {
                this.success = response.message; // Affiche le message JSON
                this.step = 3;
            },
            error: (err) => {
                this.error = err.message || "Le code OTP est incorrect ou a expiré.";
            },
            complete: () => {
                this.loading = false;
            }
        });
}



onChangePassword(): void {
  this.submitted = true;

  // Check if the form is valid before proceeding
  if (this.changePasswordForm.invalid) {
    return;
  }

  // Indicate that the form is loading
  this.loading = true;
  this.success = null;
  this.error = null;

  // Get email from the form
  const email = this.resetForm.get('email')?.value;

  // Create a ChangePasswordRequest object
  const changePasswordRequest: ChangePasswordRequest = {
    newPassword: this.changePasswordForm.get('password')?.value,
    confirmationPassword: this.changePasswordForm.get('confirmPassword')?.value
  };

  // Call the service to change the password
  this.passwordResetService.changePassword(email, changePasswordRequest)
    .subscribe({
      next: (message) => {
        this.success = message; // Use the success message from the backend
        this.step = 1; // Go back to the initial step after success
      },
      error: (err) => {
        this.error = err?.message || "Impossible de changer le mot de passe. Veuillez réessayer.";
      },
      complete: () => {
        this.loading = false; // Hide loading spinner
      }
    });
}

}
