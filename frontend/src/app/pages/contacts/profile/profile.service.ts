import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, Observable, throwError } from 'rxjs';
import { environment } from 'src/environments/environment';
import { UserProfile } from './profile.model';


@Injectable({
  providedIn: 'root',
})
export class ProfileService {
  private baseUrl = environment.baseUrl + '/users';

  constructor(private http: HttpClient) {}

  // Récupérer le profil de l'utilisateur
  getProfile(): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.baseUrl}/me`, {
      headers: this.getAuthHeaders(),
    }).pipe(
      catchError(this.handleError)
    );
  }

  // Mettre à jour le profil de l'utilisateur
  updateProfile(id: number, userProfile: UserProfile): Observable<UserProfile> {
    return this.http.put<UserProfile>(`${this.baseUrl}/${id}`, userProfile, {
      headers: this.getAuthHeaders(),
    }).pipe(
      catchError(this.handleError)
    );
  }

  // Ajouter les en-têtes d'authentification
  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
    });
  }

  // Gestion des erreurs
  private handleError(error: any): Observable<never> {
    console.error('An error occurred:', error);
    return throwError('Something went wrong; please try again later.');
  }
}
