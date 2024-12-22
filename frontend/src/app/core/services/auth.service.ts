import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Router } from "@angular/router";
import { environment } from "src/environments/environment";
import { AuthenticationRequest } from "../models/authentication-request";
import { catchError, Observable, tap, throwError } from "rxjs";
import { AuthenticationResponse } from "../models/authentication-response";
import { RegisterRequest } from "../models/register-request";

@Injectable({
    providedIn: 'root'
  })
  export class AuthenticationService {
    private baseUrl=environment.baseUrl+"/auth"
    constructor(private router:Router,private http:HttpClient) { }
    isUserAuthenticated():boolean{
      if (localStorage.getItem ("accesstoken")){
        return true;
      }
      this.router.navigate(["/login"])
  return false;
    }
    login(authenticationRequest: AuthenticationRequest): Observable<AuthenticationResponse> {
      const url = this.baseUrl + "/authenticate";
      return this.http.post<AuthenticationResponse>(url, authenticationRequest)
        .pipe(
          catchError(error => {
            if (error.status === 403) { // Vérification de l'erreur 403 pour compte verrouillé
              return throwError(() => new Error("Votre compte est verrouillé en raison de trop nombreuses tentatives de connexion."));
            } else if (error.status === 401) { // Erreur 401 pour un mauvais login ou mot de passe
              return throwError(() => new Error("Nom d'utilisateur ou mot de passe incorrect."));
            } else {
              return throwError(() => new Error("Une erreur est survenue. Veuillez réessayer plus tard."));
            }
          })
        );
    }
    register(registerRequest: RegisterRequest):Observable<AuthenticationResponse>{
      const url=this.baseUrl+"/registerClient"
      return this.http.post<AuthenticationResponse>(url,registerRequest)
    }
    setUserToken (token: string){
      localStorage.setItem("accesstoken",token)
  
    }
    logout(): void {
      localStorage.removeItem("accesstoken");
      this.router.navigate(["/login"]);
    }
    getProfile(): Observable<any> {
      const url = `${this.baseUrl}/me`; // Endpoint pour récupérer les infos de l'utilisateur connecté
      return this.http.get<any>(url);
    }
    unlockAccount(email: string): Observable<string> {
      const params = new HttpParams().set('email', email);
      return this.http.patch<string>(`${this.baseUrl}/unlock`, {}, { params });
    }
    
    forgotPassword(email: string): Observable<string> {
      const url = `${this.baseUrl}/forgot-password`;
      const params = { email };
      return this.http.post(url, null, { params, responseType: 'text' }).pipe(
        catchError(error => {
          console.error('Erreur lors de forgotPassword:', error);
          return throwError(() => new Error('Impossible d’envoyer le code de réinitialisation.'));
        })
      );
    }
  
    resetPassword(token: string, newPassword: string): Observable<string> {
      const url = `${this.baseUrl}/reset-password`;
      const params = { token, newPassword };
    
      return this.http.post(url, null, { params, responseType: 'text' }).pipe( // Ajout de responseType: 'text'
        catchError(error => {
          console.error('Erreur lors de resetPassword:', error);
          return throwError(() => new Error('Impossible de réinitialiser le mot de passe.'));
        })
      );
    }
    
    
    
    
    
    
  }
