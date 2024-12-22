import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './signup/signup.component';
import { Recoverpwd2Component } from './recoverpwd2/recoverpwd2.component';
import { PasswordResetComponent } from './passwordreset/passwordreset.component';
import { ForgetPasswordComponent } from './forget-password/forget-password.component';

const routes: Routes = [
    {
        path: 'login',
        component: LoginComponent
    },
    {
        path: 'signup',
        component: RegisterComponent
    },
    {
        path: 'reset-password',
        component: PasswordResetComponent
    },
    {
        path: 'recoverpwd-2',
        component: Recoverpwd2Component
    },
    {
        path: 'forget-password',
        component: ForgetPasswordComponent
    },
   
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class AuthRoutingModule { }
