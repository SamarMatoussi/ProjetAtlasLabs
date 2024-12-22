import { Component, OnInit } from "@angular/core";
import { UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { ActivatedRoute, Router } from "@angular/router";
import { AuthenticationRequest } from "src/app/core/models/authentication-request";
import { AuthenticationService } from "src/app/core/services/auth.service";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  authRequest: AuthenticationRequest = new AuthenticationRequest();
  error: string = '';
  loginForm: UntypedFormGroup;
  forgotPasswordForm: UntypedFormGroup; // Formulaire pour la réinitialisation du mot de passe
  submitted: boolean = false;
  year: number = new Date().getFullYear();
  showPassword = false;

  constructor(
    private authService: AuthenticationService,
    private router: Router,
    private formBuilder: UntypedFormBuilder,
    private route: ActivatedRoute
  ) { }

  ngOnInit() {
    localStorage.clear();
    this.loginForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]],
      remember: [false] // valeur par défaut false
    });

    // Formulaire pour la demande de réinitialisation du mot de passe
    this.forgotPasswordForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]]
    });
  }

  // Accès facile aux contrôles de formulaire
  get f() { return this.loginForm.controls; }
  get fp() { return this.forgotPasswordForm.controls; }

  onSubmit() {
    this.submitted = true;

    // Stop si le formulaire est invalide
    if (this.loginForm.invalid) {
      return;
    }

    // Attribution des valeurs du formulaire à authRequest
    this.authRequest.email = this.f.email.value;
    this.authRequest.password = this.f.password.value;

    this.authService.login(this.authRequest).subscribe(
      res => {
        this.authService.setUserToken(res.accessToken);
        localStorage.setItem('role', res.role);

        // Vérifiez si "Remember me" est coché et stockez l'état
        if (this.f.remember.value) {
          localStorage.setItem('remember', 'true');
        } else {
          localStorage.removeItem('remember');
        }

        this.router.navigate(['/dashboard']);
      },
      error => {
        // Vérifier le message d'erreur spécifique si le compte est verrouillé
        if (error.message.includes('verrouillé')) {
          this.error = 'Le compte est verrouillé en raison de trop nombreuses tentatives de connexion.';
        } else {
          this.error = 'Email ou mot de passe incorrect';
        }
      }
    );
  }

  unlockAccount() {
    // Demander l'email à déverrouiller
    const email = this.f.email.value;
  
    if (!email || email.trim() === '') {
      this.error = 'Veuillez fournir un email valide pour déverrouiller.';
      return;
    }
  
    this.authService.unlockAccount(email).subscribe({
      next: (response) => {
        this.error = ''; // Réinitialisez les erreurs
        alert('Compte déverrouillé avec succès. Vous pouvez maintenant vous connecter.');
      },
      error: (err) => {
        this.error = 'Impossible de déverrouiller le compte. Vérifiez l\'email ou contactez l\'administrateur.';
      }
    });
  }

  
}
