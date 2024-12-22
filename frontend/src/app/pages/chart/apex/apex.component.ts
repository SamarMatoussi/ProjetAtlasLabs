import { Component, OnInit } from '@angular/core';
import { ChartType, NoteGlobale } from './apex.model';
import { NoteService } from './note.service';

@Component({
  selector: 'app-apex',
  templateUrl: './apex.component.html',
  styleUrls: ['./apex.component.scss'],
})
export class ApexComponent implements OnInit {
  // Graphique des notes globales
  columnlabelChart: ChartType = {
    series: [],
    chart: {
      height: 350,
      type: 'bar',
      events: {
        dataPointSelection: (event, chartContext, config) => {
          const selectedIndex = config.dataPointIndex;
          const selectedCin = this.columnlabelChart.xaxis.categories[selectedIndex];
          const selectedNote = this.columnlabelChart.series[0].data[selectedIndex];
          const selectedAppreciation = this.notes[selectedIndex]?.appreciation;
  
          console.log('CIN sélectionné:', selectedCin);
          console.log('Note sélectionnée:', selectedNote);
          console.log('Appréciation sélectionnée:', selectedAppreciation);
  
          // Exemple : afficher les détails dans une alerte
          alert(
            `Détails pour CIN: ${selectedCin}\n` +
            `Note: ${selectedNote}\n` +
            `Appréciation: ${selectedAppreciation}`
          );
        },
      },
    },
    xaxis: {
      categories: [],
    },
    plotOptions: {
      bar: {
        dataLabels: {
          position: 'top',
        },
      },
    },
    dataLabels: {
      enabled: true,
      formatter: function (val: number) {
        return val.toFixed(2);
      },
      offsetY: -20,
      style: {
        fontSize: '12px',
        colors: ['#304758'],
      },
    },
    yaxis: {
      title: {
        text: 'Notes',
      },
    },
    title: {
      text: 'Notes Globales par CIN',
      align: 'center',
    },
  };

  // Graphique de classification par appréciation
  pieChart: ChartType = {
    series: [],
    chart: {
      type: 'pie',
      height: 350,
    },
    labels: [],
    title: {
      text: 'Répartition des Appréciations',
      align: 'center',
    },
  };

  notes: NoteGlobale[] = []; // Données du backend

  constructor(private noteService: NoteService) {}

  ngOnInit() {
    this._fetchData();
  }

  private _fetchData() {
    this.noteService.getNoteGlobale().subscribe((data) => {
      this.notes = data;
  
      // Préparer le graphique des notes globales
      const cins = this.notes.map((note) => note.cin.toString());
      const noteValues = this.notes.map((note) => note.noteGlobale);
      const appreciations = this.notes.map((note) => note.appreciation);
  
      this.columnlabelChart.series = [
        {
          name: 'Note Globale',
          data: noteValues,
        },
      ];
      this.columnlabelChart.xaxis.categories = cins;
  
      // Ajouter les appréciations dans les tooltips
      this.columnlabelChart.tooltip = {
        custom: ({ series, seriesIndex, dataPointIndex }) => {
          const appreciation = appreciations[dataPointIndex];
          const value = series[seriesIndex][dataPointIndex];
          return `<div class="apexcharts-tooltip">
                    <span>Note: ${value.toFixed(2)}</span><br>
                    <span>Appréciation: ${appreciation}</span>
                  </div>`;
        },
      };
  
      // Appliquer les couleurs en fonction des appréciations
      const appreciationColors = this._getAppreciationColors(appreciations);
  
      // Ajouter la logique des couleurs directement dans la configuration du graphique
      this.columnlabelChart.plotOptions.bar.colors = {
        ranges: appreciationColors.map((color, index) => ({
          from: index,
          to: index,
          color: color,
        })),
      };
  
      // Mettre à jour les options du graphique
      setTimeout(() => {
        this.columnlabelChart.chart = { ...this.columnlabelChart.chart };
      }, 0);  // Force la mise à jour du graphique
  
      // Préparer le graphique de classification
      this._preparePieChart();
    });
  }
  
  

  private _preparePieChart() {
    const appreciationCount: { [key: string]: number } = {};

    // Regrouper les données par appréciation
    this.notes.forEach((note) => {
      appreciationCount[note.appreciation] = (appreciationCount[note.appreciation] || 0) + 1;
    });

    // Configurer les données pour le graphique en camembert
    this.pieChart.series = Object.values(appreciationCount);
    this.pieChart.labels = Object.keys(appreciationCount);

    // Appliquer les couleurs au graphique en camembert
    const appreciationColors = this._getAppreciationColors(this.pieChart.labels);
    this.pieChart.colors = appreciationColors;
  }

  private _getAppreciationColors(appreciations: string[]): string[] {
    const colorMap: { [key: string]: string } = {
      Excellent: '#28a745',  // Vert
      Bien: '#007bff',       // Bleu
      Passable: '#fd7e14',   // Orange
      Mauvais: '#dc3545',    // Rouge
    };
  
    return appreciations.map((appreciation) => colorMap[appreciation] || '#6c757d'); // Couleur par défaut si non définie
  }
}  
