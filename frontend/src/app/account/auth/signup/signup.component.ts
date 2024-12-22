import { Component, OnInit } from "@angular/core";
import { UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { ActivatedRoute, Router } from "@angular/router";
import { RegisterRequest } from "src/app/core/models/register-request";
import { AuthenticationService } from "src/app/core/services/auth.service";

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss']
})
export class RegisterComponent implements OnInit{
  signupForm: UntypedFormGroup;
  submitted:any = false;
  error:any = '';
  successmsg:any = false;
  year: number = new Date().getFullYear();  
  authreques:RegisterRequest=new RegisterRequest()
  
  constructor(private servauth:AuthenticationService, private router: Router,private formBuilder: UntypedFormBuilder,private route: ActivatedRoute) { }
  
  // convenience getter for easy access to form fields
  get f() { return this.signupForm.controls; }

  ngOnInit() {
    this.signupForm = this.formBuilder.group({
      firstname: ['', Validators.required],
      lastname: ['', Validators.required],
      phone: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
      role: ['AGENT'],

    });
  }
  regitre() {
    this.submitted = true;
  
    // Stop here if form is invalid
    if (this.signupForm.invalid) {
      return;
    }
    // Proceed with form submission
    this.servauth.register(this.signupForm.value).subscribe(
      data => {
        this.successmsg = true;
        this.router.navigate(['/account/login']);
      },
      error => {
        this.error = error ? error : '';
      }
    );
  }
  
  }