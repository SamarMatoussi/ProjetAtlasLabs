import { Component, OnInit } from '@angular/core';
import { NoteService } from '../../chart/apex/note.service';
import { ProfileService } from '../../contacts/profile/profile.service';
import { NoteGlobale } from '../../chart/apex/apex.model';
import { ConfigService } from '../../../core/services/config.service';

@Component({
  selector: 'app-default',
  templateUrl: './default.component.html',
  styleUrls: ['./default.component.scss'],
})
export class DefaultComponent implements OnInit {
  userProfile: any;
  columnlabelChart: any = {
    series: [],
    chart: {
      height: 350,
      type: 'bar',
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

  pieChart: any = {
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

  notes: NoteGlobale[] = [];

  constructor(
    private noteService: NoteService,
    private profileService: ProfileService,
    private configService: ConfigService
  ) {}

  ngOnInit() {
    this.fetchData();
    this.profileService.getProfile().subscribe((profile) => {
      this.userProfile = profile;
    });
  }

  private fetchData() {
    this.noteService.getNoteGlobale().subscribe((data) => {
      this.notes = data;

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

      this.preparePieChart();
    });
  }

  private preparePieChart() {
    const appreciationCount: { [key: string]: number } = {};

    this.notes.forEach((note) => {
      appreciationCount[note.appreciation] = (appreciationCount[note.appreciation] || 0) + 1;
    });

    this.pieChart.series = Object.values(appreciationCount);
    this.pieChart.labels = Object.keys(appreciationCount);
  }
}
