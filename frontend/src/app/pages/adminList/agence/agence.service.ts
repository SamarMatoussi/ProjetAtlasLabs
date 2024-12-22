import {Injectable, PipeTransform} from '@angular/core';
import {BehaviorSubject, Observable, of, Subject, throwError} from 'rxjs';
import {GestionAgence} from './agence.model';
import {DecimalPipe} from '@angular/common';
import {catchError, debounceTime, delay, map, switchMap, tap} from 'rxjs/operators';
import {SortColumn, SortDirection} from './agence-sortable.directive';
import { environment } from 'src/environments/environment';
import { Router } from '@angular/router';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';

interface SearchResult {
  gestionAgence: GestionAgence[];
  total: number;
  }
  
  interface State {
  page: number;
  pageSize: number;
  searchTerm: string;
  sortColumn: string;
  sortDirection: string;
  startIndex: number;
  endIndex: number;
  totalRecords: number;
  status: string;
  type: string;
  date: string;
}
  @Injectable({providedIn: 'root'})
 export class GestionAgenceService {
  state: State = {
    page: 1,
    pageSize: 10,
    searchTerm: '',
    sortColumn: '',
    sortDirection: '',
    startIndex: 0,
    endIndex: 9,
    totalRecords: 0,
    status: '',
    type: '',
    date: '',
  };



  private _loading$ = new BehaviorSubject<boolean>(true);
  private _search$ = new Subject<void>();
  private gestionAgence$ = new BehaviorSubject<GestionAgence[]>([]);
  private _total$ = new BehaviorSubject<number>(0);

  content?: any;
  products?: any;
  private baseUrl=environment.baseUrl+"/agence"
  jobList$: Observable<GestionAgence[]>;
  total$: Observable<number>;
  constructor(private router:Router,private http:HttpClient) { }
  getAgenceliste(): Observable<GestionAgence[]> {
    return this.http.get<GestionAgence[]>(`${this.baseUrl}/lister`)
    .pipe(
      map((response:any) => response as GestionAgence[])
    );
  }
  addAgence(gestionAgence:GestionAgence):Observable<GestionAgence> {
    return this.http.post<GestionAgence>(`${this.baseUrl}/addagence`,gestionAgence)
    .pipe(
      map((response:any) => response as GestionAgence)
    );  
  }

 agencebyid(id: number):Observable<GestionAgence> {
  return this.http.get<GestionAgence>(`${this.baseUrl}/agence/${id}`)
  .pipe(
    map((response:any) => response as GestionAgence)
  );  
 }

 updateAgence(id: number, gestionAgence: GestionAgence): Observable<GestionAgence> {
   return this.http.put<GestionAgence>(`${this.baseUrl}/updateagence/${id}`, gestionAgence)
     .pipe(
       map((response: any) => response as GestionAgence)
     );  
 }
 



 deleteAgence(id: number): Observable<any> {
  return this.http.delete(`${this.baseUrl}/deleteagence/${id}`);
 }

set page(page: number) {
  this._set({ page });
}

set pageSize(pageSize: number) {
  this._set({ pageSize });
}

set searchTerm(searchTerm: string) {
  this._set({ searchTerm });
}

set sortColumn(sortColumn: string) {
  this._set({ sortColumn });
}

set sortDirection(sortDirection: string) {
  this._set({ sortDirection });
}

set startIndex(startIndex: number) {
  this._set({ startIndex });
}

set endIndex(endIndex: number) {
  this._set({ endIndex });
}

set totalRecords(totalRecords: number) {
  this._set({ totalRecords });
}

set status(status: string) {
  this._set({ status });
}

set type(type: string) {
  this._set({ type });
}

set date(date: string) {
  this._set({ date });
}

private _set(patch: Partial<State>) {
  Object.assign(this.state, patch);
  this._search$.next();
}

private _search(): Observable<SearchResult> {
  const { sortColumn, sortDirection, pageSize, page, searchTerm, status, type, date } = this.state;

  // Construire la requête HTTP en fonction de l'état actuel (_state)
  // Utiliser this.http.get, this.http.post, this.http.put, etc. pour interagir avec le backend

  // Retourner les données filtrées et paginées
  return of({ gestionAgence: [], total: 0 }); // Remplacer [] et 0 par les données réelles obtenues du backend
}
}


