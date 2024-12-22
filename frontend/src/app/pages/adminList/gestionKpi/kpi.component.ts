import { Component, OnInit, ViewChildren, QueryList, ViewChild, TemplateRef } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { map, Observable, tap } from 'rxjs';
import { KpiType, KpiWithParametrage, ParametrageKpi, TableKpi } from './kpi.model';
import { KpiService } from './kpi.service';
import { KpiSortableDirective, SortEvent } from './kpi-sortable.directive';
import { BsModalRef, BsModalService } from 'ngx-bootstrap/modal';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActiviteService } from '../activites/activite.service';
import { Activite } from '../activites/activite.model';
import { ActivatedRoute } from '@angular/router';
import Swal from 'sweetalert2';
import { switchMap } from 'rxjs/operators';


@Component({
  selector: 'app-kpi',
  templateUrl: './kpi.component.html',
  styleUrls: ['./kpi.component.scss'],
  providers: [KpiService, DecimalPipe]
})
export class KpiComponent implements OnInit {
  modalRef?: BsModalRef<unknown>;
  submitted = false;
  currentPage: number = 1;
  hideme: boolean[] = [];
  tables$: Observable<TableKpi[]>;
  total$: Observable<number>;
  selected: any;
  isCollapsed = true;

  kpiForm: FormGroup;
  parametrageKpiForm: FormGroup;
  listeKpi: TableKpi[] = [];
  activites: Activite[] = [];
  appreciationOptions: string[] = ['Mauvais', 'Passable', 'Bien', 'Excellent'];
  parametrageKpi: ParametrageKpi[] = [];
  activiteId: number = -1;
  kpiId: number = -1;
  selectedKpiType: string;
  KpiType: KpiType;
  kpiTypeOptions = Object.values(KpiType);
  kpisWithParametrage: KpiWithParametrage[] = [];
  loading: boolean = false;

  @ViewChildren(KpiSortableDirective) headers: QueryList<KpiSortableDirective>;
  @ViewChild('addKPIS') addKPIS?: TemplateRef<any>;
  @ViewChild('addParametrageKpis') addParametrageKpis?: TemplateRef<any>;
  @ViewChild('updateParametrageKpis') updateParametrageKpis?: TemplateRef<any>;
  @ViewChild('updateKpis') updateKpis?: TemplateRef<any>;

  constructor(
    public service: KpiService,
    private modalService: BsModalService,
    private formBuilder: FormBuilder,
    private activiteService: ActiviteService,
    private route: ActivatedRoute, 

  ) {
    this.tables$ = service.tables$;
    this.total$ = service.total$;
    this.selectedKpiType = KpiType.NUMERIQUE; 

    this.kpiForm = this.formBuilder.group({
      id: [null],
      nameKpi: ['', Validators.required],
      label: ['', Validators.required],
      description: ['', Validators.required],
      activiteId: [null],
      type: [null, Validators.required],
    });

    this.parametrageKpiForm = this.formBuilder.group({
      id: [null],
      name: ['', Validators.required],
      description: ['', Validators.required],
      appreciation: ['', Validators.required],
      min: [null],
      max: [null],
      utilite: [''],
      kpiId: [null]
    });
  }

  ngOnInit() {
    this.activiteId = +this.route.snapshot.paramMap.get('activiteId') || -1;
   // this.kpiId = +this.route.snapshot.paramMap.get('kpiId') || -1;
    this.loadData();

  }

  private loadData() {
    //this.getkpiliste();
    this.getParametrageKpiliste();
    this.fetchKpisWithParametrage();
  }

  openAddModal() {
    this.submitted = false;
    this.kpiForm.reset();
    this.selectedKpiType = '';
    this.modalRef = this.modalService.show(this.addKPIS, { class: 'modal-md' });
  }

  openParametrageKpiModal(kpi:TableKpi) {
    this.submitted = false;
    this.parametrageKpiForm.reset();
    this.selectedKpiType = kpi.type; // Assurez-vous que le type est récupéré à partir de l'objet KPI
    this.kpiId = kpi.id; // Assurez-vous d'assigner l'ID du KPI ici


    this.modalRef = this.modalService.show(this.addParametrageKpis, { class: 'modal-lg' });
  }

  openUpdateModal(data: Activite) {
    this.submitted = false;
    this.parametrageKpiForm.patchValue(data);
    this.modalRef = this.modalService.show(this.updateParametrageKpis, { class: 'modal-md' });
  }

  openUpdatekpiModal(data: Activite) {
    this.submitted = false;
    this.kpiForm.patchValue(data);
    this.modalRef = this.modalService.show(this.updateKpis, { class: 'modal-md' });
  }



    onKpiTypeChange(value: string) {
      this.selectedKpiType = value as KpiType; // Typage avec l'énumération
      console.log(`Type de KPI sélectionné : ${this.selectedKpiType}`);
  }
  
  

  close() {
    this.modalRef?.hide();
  }

 
  changeValue(index: number) {
    this.hideme[index] = !this.hideme[index];
  }

  addkpi() {
    this.submitted = true;
    if (this.kpiForm.invalid) {
      return;
    }

    this.kpiForm.get('activiteId')?.setValue(this.activiteId);
    this.service.addKpi(this.kpiForm.value).pipe(
      tap(() => this.getkpiliste()),
      tap(() => this.modalRef?.hide())
    ).subscribe({
      error: (err) => console.error('Erreur lors de l\'ajout du KPI:', err)
    });
  }

  addParametrageKpi() {
    this.submitted = true;
    if (this.parametrageKpiForm.valid) {
        if (this.kpiId === -1) {
            console.error('KPI ID is not valid:', this.kpiId);
            Swal.fire('Erreur', 'ID de KPI non valide.', 'error');
            return;
        }
        this.parametrageKpiForm.get('kpiId')?.setValue(this.kpiId);
        this.service.addParametrageKpi(this.parametrageKpiForm.value).pipe(
            tap(() => this.getkpiliste()),
            tap(() => this.modalRef?.hide())
        ).subscribe({
            error: (err) => console.error('Erreur lors de l\'ajout de ParametrageKpi:', err)
        });
    }
}

  getkpiliste() {
    this.service.getAllKpiWithParametrage(this.activiteId).pipe(
      tap(kpiData => {
        this.kpisWithParametrage = kpiData;
        this.hideme = Array(this.kpisWithParametrage.length).fill(true);
      }),
      switchMap(() => this.service.getParametrageKpilisteByKpi(this.kpiId)),
      tap(paramData => {
        this.kpisWithParametrage.forEach(kpi => {
          // Accéder à l'id à travers kpi.kpi
          kpi.parametrages = paramData.filter(param => param.kpiId === kpi.kpi.id);
        });
        
        // Adapter les kpisWithParametrage en TableKpi
        const adaptedKpis: TableKpi[] = this.kpisWithParametrage.map(kpi => ({
          id: kpi.kpi.id, // Assurez-vous que vous avez un champ id dans Kpi
          nameKpi: kpi.kpi.nameKpi,
          label: kpi.kpi.label, // Assurez-vous que label est présent dans Kpi
          description: kpi.kpi.description, // Assurez-vous que description est présent dans Kpi
          // Ajoutez d'autres propriétés si nécessaire
        }));
        
        this.service.updateTables(adaptedKpis);
        this.service.updateTotal(adaptedKpis.length);
      })
    ).subscribe();
}

updateTable() {
  this.getkpiliste();
}


  getParametrageKpiliste() {
    this.service.getParametrageKpilisteByKpi(this.kpiId).subscribe({
      next: (data) => {
        this.parametrageKpi = data.map(param => {
          const kpi = this.listeKpi.find(k => k.id === param.kpiId);
          return { ...param, kpi }; // Associe le KPI au param
        });
      },
      error: (err) => console.error('Erreur lors de la récupération des données:', err)
    });
  }
  

  updateparemetrageKpi() {
    const idToUpdate = this.parametrageKpiForm.get('id')?.value;
    this.submitted = true;
    if (this.parametrageKpiForm.valid && idToUpdate) {
      this.service.updateParametrageKpi(idToUpdate, this.parametrageKpiForm.value).pipe(
        tap(() => this.getParametrageKpiliste()),
        tap(() => this.modalRef?.hide())
      ).subscribe({
        error: (err) => console.error('Erreur lors de la mise à jour:', err)
      });
    }
  }
  
  fetchKpisWithParametrage(): void {
    this.loading = true;
    this.service.getAllKpiWithParametrage(this.activiteId).subscribe({
      next: (data) => {
        this.kpisWithParametrage = data;
      },
      error: (error) => {
        console.error('Error fetching KPIs with parametrages', error);
        Swal.fire('Erreur', 'Une erreur est survenue lors de la récupération des KPI.', 'error');
      },
      complete: () => {
        this.loading = false;
      }
    });
}


  updateKpi() {
    const idToUpdate = this.kpiForm.get('id')?.value;
    this.submitted = true;
    if (this.kpiForm.valid && idToUpdate) {
      this.service.updatekpi(idToUpdate, this.kpiForm.value).pipe(
        tap(() => this.getkpiliste()),
        tap(() => this.modalRef?.hide())
      ).subscribe({
        error: (err) => console.error('Erreur lors de la mise à jour:', err)
      });
    }
  }
  

  deleteKpi(id: number) {
    Swal.fire({
      title: 'Êtes-vous sûr ?',
      text: 'Vous ne pourrez pas récupérer ce fichier !',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Oui, supprimez-le !',
      cancelButtonText: 'Non, gardez-le'
    }).then((result) => {
      if (result.isConfirmed) {
        this.service.deleteKPI(id).pipe(
          tap(() => this.getkpiliste()),
          tap(() => Swal.fire('Supprimé !', 'Votre fichier a été supprimé.', 'success'))
        ).subscribe();
      }
    });
  }

  deleteParametrageKpi(id: number) {
    Swal.fire({
      title: 'Êtes-vous sûr ?',
      text: 'Vous ne pourrez pas récupérer ce fichier !',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Oui, supprimez-le !',
      cancelButtonText: 'Non, gardez-le'
    }).then((result) => {
      if (result.isConfirmed) {
        this.service.deleteParametrageKPI(id).pipe(
          tap(() => this.getParametrageKpiliste()),
          tap(() => Swal.fire('Supprimé !', 'Votre fichier a été supprimé.', 'success'))
        ).subscribe();
      }
    });
  }
  
  


  onSort({ column, direction }: SortEvent) {
    this.headers.forEach(header => {
      if (header.appKpiSortable !== column) {
        header.direction = '';
      }
    });
    this.service.sortColumn = column;
    this.service.sortDirection = direction;
  }
}
