import { NgModule } from "@angular/core";
import { LoginComponent } from "./login/login.component";
import { RegisterComponent } from "./signup/signup.component";
import { PasswordResetComponent } from "./passwordreset/passwordreset.component";
import { Recoverpwd2Component } from "./recoverpwd2/recoverpwd2.component";
import { CommonModule } from "@angular/common";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { CarouselModule } from "ngx-owl-carousel-o";
import { AuthRoutingModule } from "./auth-routing";
import { UIModule } from "src/app/shared/ui/ui.module";
import { AlertModule } from 'ngx-bootstrap/alert';
import { ForgetPasswordComponent } from './forget-password/forget-password.component';

@NgModule({
  declarations: [
    LoginComponent,
    RegisterComponent,
    PasswordResetComponent,
    Recoverpwd2Component,
    ForgetPasswordComponent,
    
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    AlertModule.forRoot(),
    UIModule,
    AuthRoutingModule,  // Assurez-vous que ce module est bien importé
    CarouselModule
  ]
})
export class AuthModule { }
