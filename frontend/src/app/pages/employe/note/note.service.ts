// evaluer.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Note } from './note.model';
import { NoteGlobale } from '../../chart/apex/apex.model';

@Injectable({
  providedIn: 'root'
})
export class EvaluerService {

  private baseUrl = environment.baseUrl + "/notes";  // URL de votre API

  constructor(private http: HttpClient) { }

  obtenirNotesParEmployeAuthentifie(): Observable<Note[]> {
    return this.http.get<Note[]>(`${this.baseUrl}/employe`);
  }
  
 
}
