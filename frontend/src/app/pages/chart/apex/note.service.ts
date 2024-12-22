// evaluer.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { NoteGlobale } from '../../chart/apex/apex.model';
import { Note } from '../../employe/note/note.model';

@Injectable({
  providedIn: 'root'
})
export class NoteService {

  private baseUrl = environment.baseUrl + "/notes";  // URL de votre API

  constructor(private http: HttpClient) { }

  getNoteGlobale(): Observable<NoteGlobale[]> {
    const url = `${this.baseUrl}/globale/authenticated`; // Utilisez l'endpoint correct pour l'agent connecté
    return this.http.get<NoteGlobale[]>(url);
  }
  
  
   
}
