import { Component, OnInit, ViewChild, TemplateRef } from '@angular/core';
import { Observable } from 'rxjs';
import { BsModalService, BsModalRef } from 'ngx-bootstrap/modal';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import Swal from 'sweetalert2';
import { employe } from './list.model';
import { GestionEmployeService } from './list.service';
import { Router } from '@angular/router';
import { EvaluerService } from '../evaluation/evaluation.service';

@Component({
  selector: 'app-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.scss'],
  providers: [GestionEmployeService]
})

export class EmployeNoterComponent implements OnInit {
  agentForm: FormGroup;
  listegestionEmploye!: employe[];
  gestionEmployebyid: employe = {
    firstname: '',
    lastname: '',
    phone: '',
    cin: 0,
    email: '',
    password: '',
    role: '',
    type: undefined,
    type_color: undefined,
    status_color: undefined,
    status: 'success'
  };
  gestionEmploye!: Observable<employe[]>;
  total: Observable<number>;
  submitted = false;
  selectedStatut = '';
  selectedRole = '';

  @ViewChild('addContent') addContent: TemplateRef<any>;
  @ViewChild('updateContent') updateContent: TemplateRef<any>;
  @ViewChild('viewContent') viewContent: TemplateRef<any>;

  modalRef: BsModalRef<unknown>;
  currentPage: any;
  selectedAgent: employe | undefined;

  constructor(private modalService: BsModalService,
     public serviceEmp: GestionEmployeService,
      private formBuilder: FormBuilder, private router:Router,private evaluerService:EvaluerService
  ) {
    this.total = serviceEmp.total$;
    this.agentForm = this.formBuilder.group({
      id: [null],
      firstname: [''],
      lastname: [''],
      phone: [''],
      email: [''],
      password: [''],
      cin: ['']
    });
  }

  openViewModal(data: employe) {
    this.selectedAgent = data;
    this.modalRef = this.modalService.show(this.viewContent, { class: 'modal-md' });
  }

  openAddModal() {
    this.submitted = false;
    this.modalRef = this.modalService.show(this.addContent, { class: 'modal-md' });
  }

  openUpdateModal(data: employe) {
    this.submitted = false;
    this.agentForm.patchValue(data);
    this.modalRef = this.modalService.show(this.updateContent, { class: 'modal-md' });
  }

  ngOnInit(): void {
    this.getemployeliste();
  }
  close() {
    this.modalRef?.hide();
  }

  getemployeliste() {
    this.serviceEmp.getEmployeliste().subscribe({
      next: (data) => {
        this.listegestionEmploye = data;
      },
      error: console.error
    });
  }
  searchEmploye(searchTerm: string): void {
    // Vérifiez d'abord si le terme de recherche est vide
    if (!searchTerm.trim()) {
      // Si le terme de recherche est vide, réinitialisez la liste avec la liste complète
      this.getemployeliste();
      return;
    }
  
    // Filtrer la liste des utilisateurs en fonction du terme de recherche
    this.listegestionEmploye = this.listegestionEmploye.filter(user =>
      user.firstname.toLowerCase().includes(searchTerm.toLowerCase()) ||
      user.lastname.toLowerCase().includes(searchTerm.toLowerCase()) ||
      user.phone.toLowerCase().includes(searchTerm.toLowerCase()) ||
      user.email.toLowerCase().includes(searchTerm.toLowerCase()) ||
      user.cin.toString().includes(searchTerm.toLowerCase())
    );
  }
  
  employedetail(id: number): void {
    this.serviceEmp.employebyid(id)
      .subscribe({
        next: (data) => {
          this.gestionEmployebyid = data;
          console.log("TESTTTT");
          console.log(data);
          if ('isEnabled' in data) {
            this.gestionEmployebyid.isEnabled = data.isEnabled;
          }
        },
        error: console.error
      });
  }
  

  exportToExcel() {
    this.evaluerService.exportExcel().subscribe(
      res => {
        this.downloadFile(res, 'Evaluation list.xlsx', 'xlsx');
        Swal.fire({
          icon: 'success',
          title: 'Téléchargement réussi!',
          text: 'Le fichier Excel a été téléchargé avec succès.',
        });
      },
      error => {
        Swal.fire({
          icon: 'error',
          title: 'Erreur!',
          text: 'Il y a eu un problème lors du téléchargement du fichier Excel. Veuillez réessayer.',
        });
      }
    );
  }
  
  private downloadFile(data: Blob, filename: string, fileType: string) {
    // Map file types to corresponding MIME types
    const mimeTypes: { [key: string]: string } = {
      'csv': 'text/csv',
      'xlsx': 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
      'pdf': 'application/pdf',
      // Add more file types as needed
    };
  
    // Determine the MIME type based on the file type
    const mimeType = mimeTypes[fileType] || 'application/octet-stream';
  
    // Create a blob with the appropriate MIME type
    const blob = new Blob([data], { type: mimeType });
  
    // Create a link element, set its href and download attributes, and trigger a click event
    const link = document.createElement('a');
    link.href = window.URL.createObjectURL(blob);
    link.download = filename;
  
    // Append the link to the document and trigger a click event
    document.body.appendChild(link);
    link.click();
  
    // Clean up
    document.body.removeChild(link);
    window.URL.revokeObjectURL(link.href);
  }
  pageChanged(event: any) {
    this.currentPage = event.page;
  }

  noteEmployer(id:number)
  {
    this.router.navigate(['adminList/note', id])
  }
}