import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UploadService {
  private baseUrl = `${environment.baseUrl}/evaluations`;  

  constructor(private http: HttpClient) {}

  // Méthode pour importer les notes
  importNotes(file: File): Observable<any> {
    const formData: FormData = new FormData();
    formData.append('file', file, file.name);

    return this.http.post<any>(`${this.baseUrl}/import`, formData).pipe(
    
    );
  }

 
}
