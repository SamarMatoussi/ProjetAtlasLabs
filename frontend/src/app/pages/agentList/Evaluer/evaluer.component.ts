import { Component, OnInit } from '@angular/core';
import { GestionEmployeService } from '../employe/list.service';
import { employe } from '../employe/list.model';
import { ActivatedRoute } from '@angular/router';
import { ActiviteService } from '../../adminList/activites/activite.service';
import { Activite } from '../../adminList/activites/activite.model';
import { KpiService } from '../../adminList/gestionKpi/kpi.service';
import { TableKpi } from '../../adminList/gestionKpi/kpi.model';
import { catchError, map, Observable, of, throwError } from 'rxjs';
import { EvaluerService } from './evaluer.service';
import { Note } from './note';
import * as XLSX from 'xlsx';
import { HttpErrorResponse } from '@angular/common/http';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-noter',
  templateUrl: './evaluer.component.html',
  styleUrls: ['./evaluer.component.scss']
})


export class EvaluerComponent implements OnInit {

  selectValue: string[] = [];
  stateValue: string[] = [];
  employeCin: number = -1; 
  isLoading: boolean = false;

  activiteList:Activite[] = [] ;
  kpiList: TableKpi[] = [];
  selectedKpiId: number ;
  note : number ;
  appreciation : string ;
  typeKpi:string ;
  texte : string ;
  optionList:string[] = ["Licence en Informatique" , "Awi"]

  loadingOptions: boolean = false;
  selectedKpiIndex: number = 0; 
  noteObj:Note = new Note() ; 
  constructor( private route: ActivatedRoute,
                private  activiteService:ActiviteService ,
                  private kpiService: KpiService,
                private evaluerService: EvaluerService )
  { 
  }

  ngOnInit() {
    const cinParam = this.route.snapshot.paramMap.get('employeCin');
    if (cinParam) {
      this.employeCin = +cinParam;
    } else {
      console.error('CIN de l\'employé introuvable dans les paramètres de l\'URL.');
    }
    this.getActivities();
  }
  

  getNote(kpiId : number)
  {
    this.evaluerService.getNote(this.employeCin , kpiId ).subscribe(res => {
      this.noteObj = res
    }  , error => {
      console.error(error)
    } , () => {
      this.appreciation = this.noteObj.appreciation ;
      this.note = this.noteObj.note ;
    }
  )
  }
  selectKpi(kpi: TableKpi , index: number) {
    this.selectedKpiIndex = index;
    this.selectedKpiId = kpi.id;
    console.log('selected kpi ' , this.selectedKpiId)
    this.typeKpi = kpi.type;
    this.getNote(this.selectedKpiId)
    //this.fillOptionList();
  }

  getActivities() {
    this.activiteService.getListeActivite().subscribe(
      res => {
        this.activiteList = res;
        if (this.activiteList.length > 0) {
          this.getKpiListByActivite(this.activiteList[0].id);
        }
      },
      error => {
        console.error('Erreur lors de la récupération des activités', error);
      }
    );
  }
  
  
  getKpiListByActivite(activiteId: number) {
    this.isLoading = true;
    this.activiteService.getKpisByActivite(activiteId).subscribe(
      res => {
        this.kpiList = res;
        if (this.kpiList.length > 0) {
          this.selectedKpiId = this.kpiList[0].id;
          this.typeKpi = this.kpiList[0].type;
          this.getNote(this.selectedKpiId);
        }
        this.isLoading = false;
      },
      error => {
        console.error('Erreur lors de la récupération des KPIs', error);
        this.isLoading = false;
      }
    );
  }

  getAppreciationByKpiIdAndNote() {
    if (this.selectedKpiId && this.note) {
      this.kpiService.getAppreciationByKpiIdAndNote(this.selectedKpiId, this.note).subscribe(
        res => {
          this.appreciation = res;
        },
        error => {
          console.error('Erreur lors de la récupération de l\'appréciation', error);
        }
      );
    }
  }
  

  /*
  fillOptionList() {
    if (this.selectedKpiId) {
      this.loadingOptions = true;
      this.kpiService.getOptionsByKpiId(this.selectedKpiId).subscribe(
        res => {
          this.optionList = res.length ? res : ['Aucune option disponible'];
          this.loadingOptions = false;
        },
        error => {
          console.error('Erreur lors du chargement des options', error);
          this.optionList = ['Erreur lors du chargement des options'];
          this.loadingOptions = false;
        }
      );
    }
  }
  */
  

  getAppreciationByKpiIdAndTexte()
  { 
    this.appreciation = "" ;
    if(this.texte != "") {
      this.kpiService.getAppreciationByKpiIdAndTexte(this.selectedKpiId , this.texte).subscribe(
        res => {
          this.appreciation = res
        } , error => {
          console.error(error)
        }
      )
    }
    
   
  }

  saveNote() {
    this.noteObj.employeCin = this.employeCin;  // Utiliser employeCin
    this.noteObj.kpiId = this.selectedKpiId;
    this.noteObj.note = this.note;
    this.noteObj.appreciation = this.appreciation;

    this.evaluerService.ajouterNote(this.noteObj).subscribe(
      res => {
        console.log('res ', res);
        Swal.fire({
          icon: 'success',
          title: 'Succès',
          text: 'La note a été ajoutée avec succès !',
          confirmButtonText: 'OK'
        });
      },
      error => {
        console.error(error);
        Swal.fire({
          icon: 'error',
          title: 'Erreur',
          text: 'Une erreur est survenue lors de l\'ajout de la note.',
          confirmButtonText: 'Réessayer'
        });
      }
    );
  }
  


 
  
  getNoteForKpi(employeCin: number, kpiId: number): Observable<Note> {
    return this.evaluerService.getNote(employeCin, kpiId).pipe(
      catchError((error) => {
        console.error('Erreur lors de la récupération de la note', error);
        return of({ note: 'Non évalué', appreciation: '' } as unknown as Note);
      })
    );
  }
  
  
  
  
  
  
}
