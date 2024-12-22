import { Component, OnInit } from '@angular/core';
import Swal from 'sweetalert2'; // Importation de SweetAlert
import { EvaluerService } from '../../agentList/Evaluer/evaluer.service';
import { Note } from '../../agentList/Evaluer/note';
import { Router } from '@angular/router';
import { KpiService } from '../gestionKpi/kpi.service';

@Component({
  selector: 'app-note-upload',
  templateUrl: './NoteUpload.component.html',
  styleUrls: ['./NoteUpload.component.scss']
})
export class NoteUploadComponent implements OnInit {
  files: File[] = [];
  fileName: string | null = null;
  noteImportMessage: string | null = null;
  notes: Note[] = []; 
  showNotes: boolean = false;
  kpiMap = new Map<number, string>(); // Map pour stocker les noms des KPI

  constructor(private evaluerService: EvaluerService , public service: KpiService, private router: Router) {}

  ngOnInit(): void {
    this.loadNotes(); 
    this.loadKpis(); 
    this.showNotes = false;

  }
  loadKpis(): void {
    this.service.getAllKpis().subscribe(
      (kpis) => {
        this.kpiMap = new Map(kpis.map(kpi => [kpi.id, kpi.nameKpi]));
        console.log('KPI Map:', this.kpiMap);
      },
      (error) => {
        console.error('Erreur lors du chargement des KPI:', error);
      }
    );
  }
  // Méthode appelée lors de la sélection d'un fichier
  onSelect(event: any): void {
    console.log('Fichier sélectionné:', event.addedFiles);
    this.files.push(...event.addedFiles);
    const file: File = event.addedFiles[0];
    this.fileName = file.name;
    console.log('Nom du fichier:', this.fileName);
  }

  // Méthode pour retirer un fichier sélectionné
  onRemove(): void {
    console.log('Fichier retiré');
    this.fileName = null;
    this.files = [];
  }

  // Méthode pour charger les notes depuis le backend
  loadNotes(): void {
    this.evaluerService.getAllNotes().subscribe(
      (data) => {
        this.notes = data.map(note => ({
          ...note,
          nameKpi: this.kpiMap.get(note.kpiId) || 'Nom inconnu' // Ajouter le nameKpi
        }));
        console.log('Notes chargées avec les noms des KPI:', this.notes);
      },
      (error) => {
        console.error('Erreur lors du chargement des notes:', error);
      }
    );
  }

  // Méthode pour importer un fichier
  onUpload(): void {
    if (this.files.length === 0) {
      console.log('Aucun fichier à importer');
      this.noteImportMessage = 'Veuillez sélectionner un fichier à importer.';
      return;
    }
    console.log("Fichier en cours d'importation:", this.files[0]);
    this.evaluerService.ajouterNote(this.files[0]).subscribe(
      (response) => {
        console.log('Réponse du serveur:', response);
        this.noteImportMessage = 'Fichier Excel de notes importé avec succès!';

        // Affichage de l'alerte de succès
        Swal.fire({
          title: 'Succès!',
          text: 'Le fichier a été importé avec succès.',
          icon: 'success',
          confirmButtonText: 'Ok'
        });

        // Recharger la liste des notes après l'importation
        this.loadNotes();
      },
      (error) => {
        console.error("Erreur lors de l'importation du fichier:", error);
        this.noteImportMessage = `Erreur lors de l'importation du fichier`;

        Swal.fire({
          title: 'Succès!',
          text: `Le fichier a été importé avec succès.`,
          icon: 'success',
    confirmButtonText: 'Ok'
      }).then(() => {
        // Recharger la liste des notes même en cas d'erreur
        this.loadNotes();

        // Rediriger vers la page principale après l'importation
        this.router.navigate(['/UploadsComponent']); // Remplacez par l'URL correcte de votre page
      });
    }
  );
}
getGroupedNotes(): any[] {
  const grouped = this.notes.reduce((acc, note) => {
    if (!acc[note.employeCin]) {
      acc[note.employeCin] = [];
    }
    acc[note.employeCin].push(note);
    return acc;
  }, {});

  // Retourner un tableau de groupes, chaque groupe étant une liste de notes
  return Object.entries(grouped).map(([employeCin, notes]) => ({
    employeCin,
    notes
  }));
}

 
  showNotesSection(): void {
    this.showNotes = true;
  }

  goToNext(): void {
    console.log('Affichage de la liste des notes déclenché');
    this.showNotes = true; // Active l'affichage
    this.loadNotes(); // Charge les notes
  }
}
