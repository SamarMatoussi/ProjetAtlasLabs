/* eslint-disable @typescript-eslint/adjacent-overload-signatures */
import {Injectable, PipeTransform} from '@angular/core';
import {BehaviorSubject, Observable, of, Subject, throwError} from 'rxjs';

//import {JobListdata} from './data';
import {DecimalPipe} from '@angular/common';
import {catchError, debounceTime, delay, map, switchMap, tap} from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { Router } from '@angular/router';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Note } from './note';


  
@Injectable({providedIn: 'root'})
export class EvaluerService {
 
  private baseUrl=environment.baseUrl+"/notes"

  constructor(private router:Router,private http:HttpClient) { }
  
  

 ajouterNote(data:any)
 {
  return this.http.post(`${this.baseUrl}/addNote` , data)

 }


 getNote(employeCin: number, kpiId: number) {
  return this.http.get<Note>(`${this.baseUrl}/getNote/${employeCin}/${kpiId}`);
}

 exportExcel(): Observable<Blob> {
  const backendUrl = this.baseUrl+"/exportExcel";
  return this.http.get(backendUrl, { responseType: 'blob' });
}

getAllNotes(): Observable<Note[]> {
  return this.http.get<Note[]>(`${this.baseUrl}/liste`);
}
  
}


