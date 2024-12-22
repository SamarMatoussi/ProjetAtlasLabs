import { HttpClient, HttpErrorResponse } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Router } from "@angular/router";
import { environment } from "src/environments/environment";
import { Observable, throwError } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { ChangePasswordRequest } from "../models/changePasswordRequest";

@Injectable({
    providedIn: 'root'
})
export class PasswordresetService {
    private baseUrl = environment.baseUrl + "/users";

    constructor(private router: Router, private http: HttpClient) { }
    changePassword(email: string, changePasswordRequest: ChangePasswordRequest): Observable<any> {
        const requestBody = {
          newPassword: changePasswordRequest.newPassword,
          confirmationPassword: changePasswordRequest.confirmationPassword
        };
      
        return this.http.post(`${this.baseUrl}/changePassword/${email}`, requestBody)
          .pipe(
            map((response: any) => response.message), // Extraire le message de la réponse
            catchError((error) => {
              console.error('Error changing password', error);
              return throwError(error);
            })
          );
      }
      
    
    
    
    verifyEmail(email: string): Observable<string> {
        return this.http.post(`${this.baseUrl}/verifyMail/${email}`, {}, { responseType: 'text' }).pipe(
          catchError((error: HttpErrorResponse) => {
            console.error('Erreur lors de la vérification de l\'email', error);
            let errorMessage = 'Une erreur est survenue';
            if (error.error && typeof error.error === 'string') {
              errorMessage = error.error;
            } else if (error.message) {
              errorMessage = error.message;
            }
            return throwError(() => new Error(errorMessage));
          })
        );
      }
            
      
      

      verifyOtp(otp: string, email: string): Observable<any> {
        return this.http.post(`${this.baseUrl}/verifyOtp/${otp}/${email}`, {}, { responseType: 'text' })
            .pipe(
                catchError((error) => {
                    console.error('Erreur lors de la vérification de l\'OTP', error);
                    return throwError(error);
                })
            );
    }
    
    
    
    
    

    // Méthode de gestion des erreurs
    private handleError(error: HttpErrorResponse) {
        let errorMessage = 'Unknown error!';
        if (error.error instanceof ErrorEvent) {
            // Erreur côté client
            errorMessage = `Error: ${error.error.message}`;
        } else {
            // Erreur côté serveur
            errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
        }
        console.error(errorMessage);
        return throwError(() => new Error(errorMessage));
    }
}
