package tn.Backend.services;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.Backend.dto.*;
import tn.Backend.entites.*;
import tn.Backend.repository.NoteRepository;
import tn.Backend.repository.UserRepository;
import tn.Backend.exception.ResourceNotFound;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final ActivitesService activitesService;
    private final KpiService kpiService;
    private final AgentService agentService;

    @Autowired
    public NoteServiceImpl(NoteRepository noteRepository, UserRepository userRepository, ActivitesService activitesService, KpiService kpiService, AgentService agentService) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
        this.activitesService = activitesService;
        this.kpiService = kpiService;
        this.agentService = agentService;

    }
    @Override
    public List<NoteDto> getAllNotes() {
        List<Note> allNotes = noteRepository.findAll();
        return allNotes.stream()
                .map(NoteDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<NoteGlobaleDto> getNoteGlobaleByAuthenticatedAgent() {
        // Récupérer l'agent connecté
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User agent = userRepository.findByEmail(currentUsername)
                .filter(user -> user.getRole() == Role.AGENT)
                .orElseThrow(() -> new RuntimeException("Agent non trouvé ou non autorisé"));

        // Retourner les notes globales pour cet agent
        return noteRepository.getNoteGlobaleByAgent(agent.getId());
    }
    @Override
    public NoteDto getNote(Long employeCin , Long kpiId , Authentication authentication) throws ResourceNotFound {
        Agent agent = (Agent) userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFound("Agent non trouvé"));

        // Rechercher l'employé par CIN
        Employe employe = (Employe) userRepository.findByCin(employeCin)
                .orElseThrow(() -> new ResourceNotFound("Employé non trouvé"));

        Note note = noteRepository.findByEmployeIdAndAgentIdAndKpiId(employe.getId(), agent.getId(), kpiId)
                .orElseThrow(() -> new ResourceNotFound("Note non trouvée"));

        return NoteDto.fromEntity(note);
    }

    @Override
    public NoteDto getNoteByEmployeAndKpi(Long employeId, Long kpiId) throws ResourceNotFound {
        Note note = noteRepository.findByEmployeIdAndKpiId(employeId  , kpiId)
                .orElseThrow(() -> new ResourceNotFound("Note non trouvé"));
        return NoteDto.fromEntity(note);
    }


    @Override
    public Resource exportExcel() throws ResourceNotFound, IOException {
        List<ActivitesDto> activiteList = activitesService.getAllActivites();
        List<UserDto> employeDtoList = agentService.getAllEmployes();

        // Créer un nouveau classeur Excel
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Liste des evaluations");

        // Style pour les cellules fusionnées (activités)
        CellStyle mergedCellStyle = workbook.createCellStyle();
        mergedCellStyle.setAlignment(HorizontalAlignment.CENTER);
        mergedCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        mergedCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        mergedCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Style pour les cellules simples (KPI, CIN, notes)
        CellStyle defaultCellStyle = workbook.createCellStyle();
        defaultCellStyle.setAlignment(HorizontalAlignment.CENTER);

        // Créer la première ligne pour les en-têtes d'activités
        Row activiteRow = sheet.createRow(0);

        // Créer une cellule pour "Employé CIN" à la position A1
        Cell headerCell = activiteRow.createCell(0);
        headerCell.setCellValue("Employé CIN");
        headerCell.setCellStyle(mergedCellStyle); // Appliquer le style si nécessaire

        // Création de la ligne pour les KPI (ligne 1)
        Row kpiRow = sheet.createRow(1);
        kpiRow.createCell(0); // Première colonne vide pour le CIN

        int colIndex = 1;
        for (ActivitesDto activite : activiteList) {
            // Obtenir les KPI associés à cette activité
            List<KpiDto> kpiList = kpiService.getAllKpisByActivite(activite.getId());

            // Fusion des cellules pour l'activité (autant de colonnes que de KPI)
            if (kpiList.size() > 1) {
                sheet.addMergedRegion(new CellRangeAddress(0, 0, colIndex, colIndex + kpiList.size() - 1));
            }

            // Créer une cellule pour l'activité et appliquer le style
            Cell activiteCell = activiteRow.createCell(colIndex);
            activiteCell.setCellValue(activite.getName());
            activiteCell.setCellStyle(mergedCellStyle);

            // Ajouter les KPI sous chaque activité
            for (KpiDto kpi : kpiList) {
                Cell kpiCell = kpiRow.createCell(colIndex++);
                kpiCell.setCellValue(kpi.getNameKpi());
                kpiCell.setCellStyle(defaultCellStyle);
            }
        }

        // Remplir les lignes avec CIN des employés et leurs notes pour chaque KPI
        int rowIndex = 2; // Début à la ligne 2 pour laisser de la place aux en-têtes
        for (UserDto employe : employeDtoList) {
            Row row = sheet.createRow(rowIndex++);
            // Première colonne : CIN de l'employé
            Cell cinCell = row.createCell(0);
            cinCell.setCellValue(employe.getCin());

            colIndex = 1;
            for (ActivitesDto activite : activiteList) {
                List<KpiDto> kpiList = kpiService.getAllKpisByActivite(activite.getId());
                for (KpiDto kpi : kpiList) {
                    try {
                        // Obtenir la note de l'employé pour ce KPI
                        NoteDto noteDto = this.getNoteByEmployeAndKpi(employe.getId(), kpi.getId());
                        Cell noteCell = row.createCell(colIndex++);
                        noteCell.setCellValue(noteDto != null ? ""+noteDto.getNote() : "N/A");
                    } catch (ResourceNotFound e) {
                        // Si la note n'est pas trouvée, afficher "Note non trouvée"
                        Cell noteCell = row.createCell(colIndex++);
                        noteCell.setCellValue("Note non trouvée");
                    }
                }
            }
        }

        // Ajuster automatiquement la largeur des colonnes
        for (int i = 0; i < colIndex; i++) {
            sheet.autoSizeColumn(i);
        }

        // Écrire le classeur dans un flux
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        // Retourner la ressource contenant le fichier Excel
        return new ByteArrayResource(out.toByteArray());
    }



    @Override
    @Transactional
    public NoteDto ajouterNote(NoteDto noteDto , Authentication authentication) throws ResourceNotFound {
        // Find the employee by CIN
        Employe employe = (Employe) userRepository.findByCin(noteDto.getEmployeCin())
                .orElseThrow(() -> new ResourceNotFound("Employé introuvable avec le CIN " + noteDto.getEmployeCin()));


        Agent agent =(Agent) userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFound("Agent non trouvé")) ;

        // Convert DTO to Note entity
        Note note = NoteDto.toEntity(noteDto, employe);
        note.setAgentId(agent.getId());
        note.setDateDeNotation(LocalDate.now());
        // Save the note
        Note savedNote = noteRepository.save(note);

        // Convert the saved entity to DTO and return
        return NoteDto.fromEntity(savedNote);
    }

   /* @Override
    public List<NoteDto> obtenirNotesParEmployeCin(Long cin) {
        // Retrieve notes associated with the employee via CIN
        List<Note> notes = noteRepository.findByEmployeCin(cin);

        // Convert Note entities to DTOs and return the list
        return notes.stream()
                .map(NoteDto::fromEntity)
                .collect(Collectors.toList());
    }
*/
   @Override
   public List<NoteDto> obtenirNotesParEmployeAuthentifie() {
       // Récupérer l'employé authentifié à partir de l'utilisateur connecté
       String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
       User user = userRepository.findByEmail(currentUsername)
               .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

       // Vérifier que l'utilisateur est bien un employé
       if (user.getRole() != Role.EMPLOYE) {
           throw new RuntimeException("L'utilisateur connecté n'est pas un employé");
       }

       Employe employe = (Employe) user;

       // Récupérer les notes associées à cet employé
       List<Note> notes = noteRepository.findByEmployeId(employe.getId());

       // Convertir les entités Note en DTOs et retourner la liste
       return notes.stream()
               .map(NoteDto::fromEntity)
               .collect(Collectors.toList());
   }

    @Override
    @Transactional
    public NoteDto mettreAJourNoteByCin(Long cin, NoteDto noteDto) throws ResourceNotFound {
        // Find the existing note by CIN (Assuming CIN is unique to a note)
        Note noteExistante = noteRepository.findByEmployeCin(cin)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFound("Note non trouvée pour CIN: " + cin));

        // Find the employee by CIN
        Employe employe = (Employe) userRepository.findUserByCin(noteDto.getEmployeCin())
                .orElseThrow(() -> new ResourceNotFound("Employé non trouvé"));

        // Update the note's information
        //noteExistante.setScore(noteDto.getScore());
        //noteExistante.setCommentaire(noteDto.getCommentaire());
        noteExistante.setDateDeNotation(noteDto.getDateDeNotation());
        noteExistante.setEmploye(employe);

        // Save the updated note
        Note noteMiseAJour = noteRepository.save(noteExistante);
         // Return the updated DTO
        return NoteDto.fromEntity(noteMiseAJour);
    }
    @Override
    @Transactional
    public void supprimerNoteByCin(Long cin) throws ResourceNotFound {
        // Find the notes by CIN
        List<Note> notes = noteRepository.findByEmployeCin(cin);

        // If no notes found, throw exception
        if (notes.isEmpty()) {
            throw new ResourceNotFound("Note non trouvée pour le CIN donné");
        }

        // Delete all notes with the given CIN
        noteRepository.deleteAll(notes);
    }

    @Override
    @Transactional
    public void supprimerNote(Long id) throws ResourceNotFound {
        // Check if the note exists before trying to delete it
        if (!noteRepository.existsById(id)) {
            throw new ResourceNotFound("Note non trouvée");
        }
        // Delete a note by its ID
        noteRepository.deleteById(id);
    }

    @Override
    public NoteDto mettreAJourNote(Long id, NoteDto noteDto) throws ResourceNotFound {
        // Find the existing note
        Note noteExistante = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Note non trouvée"));

        // Find the employee by CIN
        Employe employe = (Employe) userRepository.findUserByCin(noteDto.getEmployeCin())
                .orElseThrow(() -> new ResourceNotFound("Employé non trouvé"));

        // Update the note's information
        noteExistante.setAppreciation(noteDto.getAppreciation());
        noteExistante.setNote(noteDto.getNote());
        noteExistante.setDateDeNotation(noteDto.getDateDeNotation());
        noteExistante.setEmploye(employe);

        // Save the updated note
        Note noteMiseAJour = noteRepository.save(noteExistante);

        // Return the updated DTO
        return NoteDto.fromEntity(noteMiseAJour);
    }
     /*@Override
    public Resource exportExcel() throws ResourceNotFound, IOException {

        List<ActivitesDto> activiteList = activitesService.getAllActivites();
        System.out.println("Activites List size: " + activiteList.size());
        List<UserDto> employeDtoList = agentService.getAllEmployes();
        System.out.println("employe List size: " + employeDtoList.size());
        employeDtoList.forEach(e ->
        {
            System.out.println("id " + e.getId());
        });

        // Créer un nouveau classeur Excel
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Liste des evaluations");

        // Style pour les cellules fusionnées (activités)
        CellStyle mergedCellStyle = workbook.createCellStyle();
        mergedCellStyle.setAlignment(HorizontalAlignment.CENTER);
        mergedCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        mergedCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        mergedCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Style pour les cellules simples (KPI, CIN, notes)
        CellStyle defaultCellStyle = workbook.createCellStyle();
        defaultCellStyle.setAlignment(HorizontalAlignment.CENTER);

        // Création des deux premières lignes (activités et KPI)
        Row activiteRow = sheet.createRow(0);
        Row kpiRow = sheet.createRow(1);

        // Première colonne vide pour CIN des employés
        activiteRow.createCell(0);
        kpiRow.createCell(0);

        int colIndex = 1;
        for (ActivitesDto activite : activiteList) {
            // Obtenir les KPI associés à cette activité
            List<KpiDto> kpiList = kpiService.getAllKpisByActivite(activite.getId());

            // Fusion des cellules pour l'activité (autant de colonnes que de KPI)
            if (kpiList.size() > 1) {
                sheet.addMergedRegion(new CellRangeAddress(0, 0, colIndex, colIndex + kpiList.size() - 1));
            }

            // Créer une cellule pour l'activité et appliquer le style
            Cell activiteCell = activiteRow.createCell(colIndex);
            activiteCell.setCellValue(activite.getName());
            activiteCell.setCellStyle(mergedCellStyle);

            // Ajouter les KPI sous chaque activité
            for (KpiDto kpi : kpiList) {
                Cell kpiCell = kpiRow.createCell(colIndex++);
                kpiCell.setCellValue(kpi.getNameKpi());
                kpiCell.setCellStyle(defaultCellStyle);
            }
        }

        // Remplir les lignes avec CIN des employés et leurs notes pour chaque KPI
        int rowIndex = 2;
        for (UserDto employe : employeDtoList) {
            Row row = sheet.createRow(rowIndex++);
            // Première colonne : CIN de l'employé
            Cell cinCell = row.createCell(0);
            cinCell.setCellValue(employe.getCin());

            colIndex = 1;
            for (ActivitesDto activite : activiteList) {
                List<KpiDto> kpiList = kpiService.getAllKpisByActivite(activite.getId());
                for (KpiDto kpi : kpiList) {
                    System.out.println("id emp " + employe.getId());
                    // Obtenir la note de l'employé pour ce KPI
                    NoteDto noteDto = this.getNoteByEmployeAndKpi(employe.getId(), kpi.getId());
                    Cell noteCell = row.createCell(colIndex++);
                    noteCell.setCellValue(noteDto != null ? ""+noteDto.getNote() : "N/A");
                }
            }
        }

        // Ajuster automatiquement la largeur des colonnes
        for (int i = 0; i < colIndex; i++) {
            sheet.autoSizeColumn(i);
        }

        // Écrire le classeur dans un flux
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        // Retourner la ressource contenant le fichier Excel
        return new ByteArrayResource(out.toByteArray());
    }
*/
   /* @Override
    public Resource exportExcel() throws ResourceNotFound, IOException {
        List<ActivitesDto> activiteList = activitesService.getAllActivites();
        List<UserDto> employeDtoList = agentService.getAllEmployes();

        // Créer un nouveau classeur Excel
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Liste des evaluations");

        // Style pour les cellules fusionnées (activités)
        CellStyle mergedCellStyle = workbook.createCellStyle();
        mergedCellStyle.setAlignment(HorizontalAlignment.CENTER);
        mergedCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        mergedCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        mergedCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Style pour les cellules simples (KPI, CIN, notes)
        CellStyle defaultCellStyle = workbook.createCellStyle();
        defaultCellStyle.setAlignment(HorizontalAlignment.CENTER);

        // Création des deux premières lignes (activités et KPI)
        Row activiteRow = sheet.createRow(0);
        Row kpiRow = sheet.createRow(1);

        // Première colonne vide pour CIN des employés
        activiteRow.createCell(0);
        kpiRow.createCell(0);

        int colIndex = 1;
        for (ActivitesDto activite : activiteList) {
            // Obtenir les KPI associés à cette activité
            List<KpiDto> kpiList = kpiService.getAllKpisByActivite(activite.getId());

            // Fusion des cellules pour l'activité (autant de colonnes que de KPI)
            if (kpiList.size() > 1) {
                sheet.addMergedRegion(new CellRangeAddress(0, 0, colIndex, colIndex + kpiList.size() - 1));
            }

            // Créer une cellule pour l'activité et appliquer le style
            Cell activiteCell = activiteRow.createCell(colIndex);
            activiteCell.setCellValue(activite.getName());
            activiteCell.setCellStyle(mergedCellStyle);

            // Ajouter les KPI sous chaque activité
            for (KpiDto kpi : kpiList) {
                Cell kpiCell = kpiRow.createCell(colIndex++);
                kpiCell.setCellValue(kpi.getNameKpi());
                kpiCell.setCellStyle(defaultCellStyle);
            }
        }

        // Remplir les lignes avec CIN des employés et leurs notes pour chaque KPI
        int rowIndex = 2;
        for (UserDto employe : employeDtoList) {
            Row row = sheet.createRow(rowIndex++);
            // Première colonne : CIN de l'employé
            Cell cinCell = row.createCell(0);
            cinCell.setCellValue(employe.getCin());

            colIndex = 1;
            for (ActivitesDto activite : activiteList) {
                List<KpiDto> kpiList = kpiService.getAllKpisByActivite(activite.getId());
                for (KpiDto kpi : kpiList) {
                    try {
                        // Obtenir la note de l'employé pour ce KPI
                        NoteDto noteDto = this.getNoteByEmployeAndKpi(employe.getId(), kpi.getId());
                        Cell noteCell = row.createCell(colIndex++);
                        noteCell.setCellValue(noteDto != null ? ""+noteDto.getNote() : "N/A");
                    } catch (ResourceNotFound e) {
                        // Si la note n'est pas trouvée, afficher "Note non trouvée"
                        Cell noteCell = row.createCell(colIndex++);
                        noteCell.setCellValue("Note non trouvée");
                    }
                }
            }
        }

        // Ajuster automatiquement la largeur des colonnes
        for (int i = 0; i < colIndex; i++) {
            sheet.autoSizeColumn(i);
        }

        // Écrire le classeur dans un flux
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        // Retourner la ressource contenant le fichier Excel
        return new ByteArrayResource(out.toByteArray());
    }*/
}
