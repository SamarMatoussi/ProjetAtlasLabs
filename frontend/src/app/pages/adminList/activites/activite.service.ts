import {Injectable, PipeTransform} from '@angular/core';
import {BehaviorSubject, Observable, of, Subject, throwError} from 'rxjs';

import {Activite} from './activite.model';
import {DecimalPipe} from '@angular/common';
import {catchError, debounceTime, delay, map, switchMap, tap} from 'rxjs/operators';
import {SortColumn, SortDirection} from './activite-sortable.directive';
import { environment } from 'src/environments/environment';
import { Router } from '@angular/router';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { TableKpi } from '../gestionKpi/kpi.model';

interface SearchResult {
  activite: Activite[];
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
export class ActiviteService {
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
  private activite$ = new BehaviorSubject<Activite[]>([]);
  private _total$ = new BehaviorSubject<number>(0);

  content?: any;
  products?: any;
  private baseUrl=environment.baseUrl+"/activite";
  private kpiBaseUrl = environment.baseUrl + "/kpi";
  jobList$: Observable<Activite[]>;
  total$: Observable<number>;
  constructor(private router:Router,private http:HttpClient) { }
  getListeActivite(): Observable<Activite[]> {
    return this.http.get<Activite[]>(`${this.baseUrl}/liste`)
    
  }
  addActivite(activite:Activite):Observable<Activite> {
    return this.http.post<Activite>(`${this.baseUrl}/addactivite`,activite)
    .pipe(
      map((response:any) => response as Activite)
    );  
}

activitebyid(id: number):Observable<Activite> {
  return this.http.get<Activite>(`${this.baseUrl}/activite/${id}`)
  .pipe(
    map((response:any) => response as Activite)
  );  
}

 updateActivite(id: number, activite: Activite): Observable<Activite> {
   return this.http.put<Activite>(`${this.baseUrl}/updateactivite/${id}`, activite)
     .pipe(
       map((response: any) => response as Activite)
     );  
 }
 



delateActivite(id: number): Observable<any> {
  return this.http.delete(`${this.baseUrl}/delete/${id}`);
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

  return of({ activite: [], total: 0 });
}

 getKpisByActivite(activiteId:number ): Observable<TableKpi[]> {
           
    
            return this.http.get<TableKpi[]>(`${this.kpiBaseUrl}/listeByActivite/${activiteId}`)
                .pipe(
                    map(response => response as TableKpi[]),
                   
                    catchError(error => {
                        console.error('Error fetching KPI list by Activite', error);
                        return of([]);
                    })
                );
        }
        // activite.service.ts
addPostesToActivite(activiteId: number, posteIds: number[]): Observable<Activite> {
  return this.http.post<Activite>(`${this.baseUrl}/${activiteId}/postes`, posteIds);
}

removePostesFromActivite(activiteId: number, posteIds: number[]): Observable<Activite> {
  return this.http.delete<Activite>(`${this.baseUrl}/${activiteId}/postes`, { body: posteIds });
}



}


