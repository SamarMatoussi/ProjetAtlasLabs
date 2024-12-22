import { Injectable, PipeTransform } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { BehaviorSubject, Observable, of, Subject } from 'rxjs';
import { debounceTime, delay, switchMap, tap, catchError, map } from 'rxjs/operators';
import { TableKpi, SearchResult, ParametrageKpi, KpiWithParametrage } from './kpi.model';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { SortDirection } from './kpi-sortable.directive';

interface State {
    page: number;
    pageSize: number;
    searchTerm: string;
    sortColumn: string;
    sortDirection: SortDirection;
    startIndex: number;
    endIndex: number;
    totalRecords: number;
}

const compare = (v1: any, v2: any) => (v1 < v2 ? -1 : v1 > v2 ? 1 : 0);

function sort(tables: TableKpi[], column: string, direction: string): TableKpi[] {
    if (direction === '' || column === '') {
        return tables;
    } else {
        return [...tables].sort((a, b) => {
            const res = compare(a[column], b[column]);
            return direction === 'asc' ? res : -res;
        });
    }
}

function matches(table: TableKpi, term: string, pipe: PipeTransform) {
    return (
        table.nameKpi?.toLowerCase().includes(term.toLowerCase()) ||
        table.label?.toLowerCase().includes(term.toLowerCase()) ||
        table.description?.toLowerCase().includes(term.toLowerCase()) ||
        table.activite?.name?.toLowerCase().includes(term.toLowerCase()) ||
        table.parametrages?.some(p => p.name?.toLowerCase().includes(term.toLowerCase()))
    );
}


@Injectable({
    providedIn: 'root'
})
export class KpiService {
    private _loading$ = new BehaviorSubject<boolean>(true);
    private _search$ = new Subject<void>();
    private _tables$ = new BehaviorSubject<TableKpi[]>([]);
    private _total$ = new BehaviorSubject<number>(0);
    private _state: State = {
        page: 1,
        pageSize: 10,
        searchTerm: '',
        sortColumn: '',
        sortDirection: '',
        startIndex: 0,
        endIndex: 9,
        totalRecords: 0
    };

    private kpiBaseUrl = environment.baseUrl + "/kpi";
    private parametrageKpiBaseUrl = environment.baseUrl + "/parametragekpi";

    tables$ = this._tables$.asObservable();
    total$ = this._total$.asObservable();

    constructor(private pipe: DecimalPipe, private http: HttpClient) {
       /* this._search$.pipe(
            tap(() => this._loading$.next(true)),
            debounceTime(200),
            switchMap(() => this._search()),
            delay(200),
            tap(() => this._loading$.next(false))
        ).subscribe(result => {
            this._tables$.next(result.tables);
            this._total$.next(result.total);
        });
        this._search$.next();*/
    }

    addKpi(tableKpi: TableKpi): Observable<TableKpi> {
        return this.http.post<TableKpi>(`${this.kpiBaseUrl}/addkpi`, tableKpi)
            .pipe(
                map(response => response as TableKpi),
                catchError(error => {
                    console.error('Error adding KPI', error);
                    return of(null);
                })
            );
    }
    addParametrageKpi(parametrageKpi: ParametrageKpi): Observable<ParametrageKpi> {
        return this.http.post<ParametrageKpi>(`${this.parametrageKpiBaseUrl}/addparametrageKpi`, parametrageKpi)
          .pipe(
            catchError(this.handleError<ParametrageKpi>('addParametrageKpi'))
          );
      }
      
      private handleError<T>(operation = 'operation', result?: T) {
        return (error: any): Observable<T> => {
          console.error(`${operation} failed: ${error.message}`);
          return of(result as T);
        };
    }

    getAllKpis(): Observable<TableKpi[]> {
        return this.http.get<TableKpi[]>(`${this.kpiBaseUrl}/allKPIs`)
            .pipe(
                map(response => response as TableKpi[]),
                catchError(error => {
                    console.error('Error fetching all KPI list', error);
                    return of([]); // Retourner un tableau vide en cas d'erreur
                })
            );
    }
    
    getKpilisteByActivite(activiteId:number , page: number): Observable<TableKpi[]> {
        const startIndex = (page - 1) * this._state.pageSize;
        const endIndex = startIndex + this._state.pageSize;

        return this.http.get<TableKpi[]>(`${this.kpiBaseUrl}/liste/${activiteId}?page=${page}&pageSize=${this._state.pageSize}`)
            .pipe(
                map(response => response as TableKpi[]),
                tap(() => {
                    this._state.startIndex = startIndex;
                    this._state.endIndex = endIndex;
                }),
                catchError(error => {
                    console.error('Error fetching KPI list', error);
                    return of([]);
                })
            );
    }

    getParametrageKpilisteByKpi(kpiId:number): Observable<ParametrageKpi[]> {
        return this.http.get<ParametrageKpi[]>(`${this.parametrageKpiBaseUrl}/liste/${kpiId}`)
            .pipe(
                map(response => response as ParametrageKpi[]),
                catchError(error => {
                    console.error('Error fetching Parametrage KPI list', error);
                    return of([]);
                })
            );
        }

    
        updateParametrageKpi(id: number,parametrageKpi: ParametrageKpi): Observable<ParametrageKpi> {
            return this.http.put<ParametrageKpi>(`${this.parametrageKpiBaseUrl}/updateParametrageKpi/${id}`, parametrageKpi)
              .pipe(
                catchError(this.handleError<ParametrageKpi>('updateParametrageKpi'))
              );
        }

        updatekpi(id: number, kpi: TableKpi): Observable<TableKpi> {
            return this.http.put<TableKpi>(`${this.kpiBaseUrl}/updatekpi/${id}`, kpi)
              .pipe(
                map((response: any) => response as TableKpi)
              );  
        }
        
        deleteKPI(id: number): Observable<any> {
            return this.http.delete(`${this.kpiBaseUrl}/deleteKpi/${id}`);
        } 
        deleteParametrageKPI(id: number): Observable<any> {
            return this.http.delete(`${this.parametrageKpiBaseUrl}/delete/${id}`);
        } 

      /*  getOptionsByKpiId(kpiId: number): Observable<string[]> {
            return this.http.get<string[]>(`${this.kpiBaseUrl}/kpis/${kpiId}/options`);
          }*/
          

   /* addParametrageKpi(parametrageKpi: ParametrageKpi): Observable<ParametrageKpi> {
        return this.http.post<ParametrageKpi>(`${this.parametrageKpiBaseUrl}/addparametrageKpi`, parametrageKpi)
            .pipe(
                map(response => response as ParametrageKpi),
                catchError(error => {
                    console.error('Error adding Parametrage KPI', error);
                    return of(null);
                })
            );
    }*/
          
              

     
  
    

    updateTables(tables: TableKpi[]) {
        this._tables$.next(tables);
    }

    updateTotal(total: number) {
        this._total$.next(total);
    }

    get loading$() { return this._loading$.asObservable(); }
    get page() { return this._state.page; }
    get pageSize() { return this._state.pageSize; }
    get searchTerm() { return this._state.searchTerm; }
    get startIndex() { return this._state.startIndex; }
    get endIndex() { return this._state.endIndex; }
    get totalRecords() { return this._state.totalRecords; }

    set page(page: number) { this._set({ page }); }
    set pageSize(pageSize: number) { this._set({ pageSize }); }
    set startIndex(startIndex: number) { this._set({ startIndex }); }
    set endIndex(endIndex: number) { this._set({ endIndex }); }
    set totalRecords(totalRecords: number) { this._set({ totalRecords }); }
    set searchTerm(searchTerm: string) { this._set({ searchTerm }); }
    set sortColumn(sortColumn: string) { this._set({ sortColumn }); }
    set sortDirection(sortDirection: SortDirection) { this._set({ sortDirection }); }

    private _set(patch: Partial<State>) {
        Object.assign(this._state, patch);
        this._search$.next();
    }

   /* private _search(): Observable<SearchResult> {
        const { sortColumn, sortDirection, pageSize, page, searchTerm } = this._state;

        return this.getKpilisteByActivite(1 , page).pipe(
            switchMap(kpiData => this.getParametrageKpilisteByKpi(1).pipe(
                map(parametrageData => {
                    const tables = kpiData.map(kpi => ({
                        ...kpi,
                        parametrages: parametrageData.filter(param => param.kpiId === kpi.id)
                    }));

                    const filteredTables = tables
                        .filter(table => matches(table, searchTerm, this.pipe))
                        .slice(this._state.startIndex, this._state.endIndex);

                    return {
                        tables: sort(filteredTables, sortColumn, sortDirection),
                        total: tables.length
                    };
                }),
                catchError(error => {
                    console.error('Error fetching combined KPI and Parametrage data', error);
                    return of({ tables: [], total: 0 });
                })
            ))
        );
    }*/
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

        getAppreciationByKpiIdAndNote(kpiId : number , note:number)
        {
            return this.http.get(`${this.parametrageKpiBaseUrl}/note/appericiation/${kpiId}?note=${note}`  ,  { responseType: 'text' })
            
                         
        }
        getAppreciationByKpiIdAndTexte(kpiId : number , texte:string)
        {
            return this.http.get(`${this.parametrageKpiBaseUrl}/texte/appericiation/${kpiId}?texte=${texte}`  ,  { responseType: 'text' })
            
                         
        }
       
        getAllKpiWithParametrage(activiteId: number): Observable<KpiWithParametrage[]> {
            return this.http.get<KpiWithParametrage[]>(`${this.kpiBaseUrl}/listeWithParametrage/${activiteId}`)
                .pipe(
                    map(response => response as KpiWithParametrage[]),
                    catchError(error => {
                        console.error('Error fetching KPI with Parametrage list', error);
                        return of([]); 
                    })
                );
        }
        
    }
