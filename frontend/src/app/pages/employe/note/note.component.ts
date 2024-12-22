import { Component, OnInit } from '@angular/core';
import { EvaluerService } from './note.service';
import { Note } from './note.model';
import { KpiService } from '../../adminList/gestionKpi/kpi.service';
import { TableKpi } from '../../adminList/gestionKpi/kpi.model';

@Component({
  selector: 'app-note',
  templateUrl: './note.component.html',
  styleUrls: ['./note.component.scss']
})
export class NoteEmployeComponent implements OnInit {
  notes: Note[] = [];
  currentPage: number = 1; // Page actuelle
  itemsPerPage: number = 9; // Nombre d'éléments par page
  isLoadingKpis = false; // Indicateur de chargement des KPI
  isLoadingNotes = false; // Indicateur de chargement des notes
  errorMessageKpi: string = '';
  errorMessageNotes: string = '';
  kpis: TableKpi[] = []; // Nouvelle variable pour stocker les KPI

  constructor(private evaluerService: EvaluerService, public service: KpiService) {}

  ngOnInit(): void {
    // Charger d'abord les KPI, puis récupérer les notes
    this.loadKpis();
  }

  loadKpis(): void {
    this.isLoadingKpis = true; // Indiquer le début du chargement des KPI
    this.service.getAllKpis().subscribe(
      (data) => {
        console.log('KPI récupérés avec succès:', data);  // Affiche les KPI récupérés
        this.kpis = data;  // Assigner les KPI à la variable 'kpis'
        this.isLoadingKpis = false;  // Arrêter le chargement

        // Une fois les KPI récupérés, appeler la méthode pour récupérer les notes
        this.getNotesForEmploye();
      },
      (error) => {
        console.error('Erreur lors de la récupération des KPI', error);  // Affiche l'erreur en cas de problème
        this.errorMessageKpi = 'Erreur lors de la récupération des KPI';
        this.isLoadingKpis = false;  // Arrêter le chargement
      }
    );
  }

  getNotesForEmploye(): void {
    if (this.kpis.length === 0) {
      console.warn('Les KPI n\'ont pas encore été chargés');
      return; // Empêcher l'appel si les KPI ne sont pas encore disponibles
    }

    this.isLoadingNotes = true;  // Indiquer le début du chargement des notes
    console.log('Tentative de récupération des notes de l\'employé authentifié...');
    
    this.evaluerService.obtenirNotesParEmployeAuthentifie().subscribe(
      (data) => {
        console.log('Notes récupérées avec succès:', data);  // Affiche les notes récupérées

        // Log des KPI pour vérifier leur correspondance avec kpiId
        console.log('KPI disponibles:', this.kpis);

        // Mappage des notes avec les noms des KPI
        this.notes = data.map(note => {
          console.log(`Traitement de la note pour le KPI avec ID ${note.kpiId}`);
          const kpi = this.kpis.find(k => k.id === note.kpiId);
          if (kpi) {
            note.kpiName = kpi.nameKpi;  // Ajouter le nom du KPI à la note
            console.log(`KPI trouvé pour le ID ${note.kpiId}: ${kpi.nameKpi}`);
          } else {
            console.warn(`KPI non trouvé pour le ID ${note.kpiId}`);
          }
          return note;
        });

        console.log('Notes après ajout des noms des KPI:', this.notes);  // Afficher les notes après la mise à jour
        this.isLoadingNotes = false; // Arrêter le chargement des notes
      },
      (error) => {
        console.error('Erreur lors de la récupération des notes', error);  // Affiche l'erreur en cas de problème
        this.errorMessageNotes = 'Erreur lors de la récupération des notes';
        this.isLoadingNotes = false;  // Arrêter le chargement
      }
    );
}


  // Méthode pour récupérer les notes de la page actuelle
  getPaginatedNotes(): Note[] {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    return this.notes.slice(startIndex, endIndex);
  }

  
}

