/* eslint-disable @typescript-eslint/adjacent-overload-signatures */
import {Injectable, PipeTransform} from '@angular/core';
import {BehaviorSubject, Observable, of, Subject, throwError} from 'rxjs';

import {GestionUtilisateur} from './list.model';
//import {JobListdata} from './data';
import {DecimalPipe} from '@angular/common';
import {catchError, debounceTime, delay, map, switchMap, tap} from 'rxjs/operators';
import {SortColumn, SortDirection} from './list-sortable.directive';
import { environment } from 'src/environments/environment';
import { Router } from '@angular/router';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';

interface SearchResult {
  gestionUtilisateur: GestionUtilisateur[];
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
export class GestionUtilisateurService {
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
  private gestionUtilisateur$ = new BehaviorSubject<GestionUtilisateur[]>([]);
  private _total$ = new BehaviorSubject<number>(0);

  content?: any;
  products?: any;
  private baseUrl=environment.baseUrl+"/admin"
  jobList$: Observable<GestionUtilisateur[]>;
  total$: Observable<number>;
  constructor(private router:Router,private http:HttpClient) { }
  getUserliste(): Observable<GestionUtilisateur[]> {
    return this.http.get<GestionUtilisateur[]>(`${this.baseUrl}/usersexceptadmin`)
    .pipe(
      map((response:any) => response as GestionUtilisateur[])
    );
  }
  addUser(gestionUser:GestionUtilisateur):Observable<GestionUtilisateur> {
    return this.http.post<GestionUtilisateur>(`${this.baseUrl}/addagent`,gestionUser)
    .pipe(
      map((response:any) => response as GestionUtilisateur)
    );  
}

userbyid(id: number):Observable<GestionUtilisateur> {
  return this.http.get<GestionUtilisateur>(`${this.baseUrl}/users/${id}`)
  .pipe(
    map((response:any) => response as GestionUtilisateur)
  );  
}

 updateUser(id: number, gestionUser: GestionUtilisateur): Observable<GestionUtilisateur> {
   return this.http.put<GestionUtilisateur>(`${this.baseUrl}/updateuser/${id}`, gestionUser)
     .pipe(
       map((response: any) => response as GestionUtilisateur)
     );  
 }
 

delateUser(id: number): Observable<any> {
  return this.http.delete(`${this.baseUrl}/delete/${id}`);
}
revokeAccount(cin: number, activate: boolean): Observable<any> {
  return this.http.put<any>(`${this.baseUrl}/revokeaccount?cin=${cin}&activate=${activate}`, {});
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
  return of({ gestionUtilisateur: [], total: 0 }); // Remplacer [] et 0 par les données réelles obtenues du backend
}
}


