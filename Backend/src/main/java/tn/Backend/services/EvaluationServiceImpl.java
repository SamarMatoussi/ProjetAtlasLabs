package tn.Backend.services;

import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.Backend.dto.EvaluationDto;
import tn.Backend.entites.Evaluation;
import tn.Backend.entites.Kpi;
import tn.Backend.entites.User;
import tn.Backend.repository.EvaluationRepository;
import tn.Backend.repository.KpiRepository;
import tn.Backend.repository.UserRepository;

import java.io.IOException;
import java.util.*;

@Service
public class EvaluationServiceImpl implements EvaluationService {

    @Autowired
    private EvaluationRepository evaluationRepository;

    @Autowired
    private KpiRepository kpiRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public void importEvaluationData(MultipartFile file) throws IOException {
        List<Evaluation> evaluationsToSave = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            // Sauter la première ligne (entêtes)
            if (rowIterator.hasNext()) rowIterator.next();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                // Vérification de la cellule CIN
                Cell cinCell = row.getCell(0);
                if (cinCell == null || cinCell.getCellType() != CellType.STRING) {
                    System.err.println("CIN manquant ou invalide pour la ligne " + row.getRowNum());
                    continue; // Passer à la ligne suivante si CIN est manquant ou invalide
                }

                String cin = cinCell.getStringCellValue().trim();

                // Vérifier si le CIN est valide (numérique et de longueur correcte)
                if (cin.isEmpty() || !cin.matches("\\d{8}")) {  // Vérifie si le CIN contient exactement 8 chiffres
                    System.err.println("CIN non valide pour la ligne " + row.getRowNum());
                    continue; // Passer à la ligne suivante si CIN est invalide
                }

                Optional<User> employe = userRepository.findUserByCin(Long.valueOf(cin));

                if (employe.isPresent()) {
                    // Parcourir les colonnes pour récupérer les KPI et leurs notes
                    for (int i = 1; i < row.getPhysicalNumberOfCells(); i++) {
                        Cell cell = row.getCell(i);
                        if (cell == null || cell.getCellType() == CellType.BLANK) {
                            continue; // Ignorer les cellules vides
                        }

                        // Lire les entêtes pour obtenir le nom du KPI
                        Row headerRow = sheet.getRow(0);
                        Cell kpiCell = headerRow.getCell(i);
                        if (kpiCell == null || kpiCell.getCellType() != CellType.STRING) {
                            continue; // Ignorer si la cellule KPI est vide ou mal formatée
                        }

                        String kpiName = kpiCell.getStringCellValue().trim();
                        Optional<Kpi> kpi = kpiRepository.findByNameKpi(kpiName);

                        if (kpi.isPresent()) {
                            Evaluation evaluation = new Evaluation();
                            evaluation.setEmploye(employe.get());
                            evaluation.setKpi(kpi.get());

                            // Traitement de la note (numeric ou string)
                            try {
                                if (cell.getCellType() == CellType.NUMERIC) {
                                    evaluation.setNote((int) cell.getNumericCellValue());
                                } else if (cell.getCellType() == CellType.STRING) {
                                    evaluation.setNote(Integer.parseInt(cell.getStringCellValue().trim()));
                                }
                            } catch (NumberFormatException e) {
                                System.err.println("Note invalide pour le KPI '" + kpiName + "' à la ligne " + row.getRowNum());
                                evaluation.setNote(0); // Si la note est invalide, on l'initialise à 0
                            }

                            evaluationsToSave.add(evaluation);
                        } else {
                            System.err.println("KPI non trouvé : " + kpiName);
                        }
                    }
                } else {
                    System.err.println("Employé non trouvé pour le CIN : " + cin);
                }
            }

            // Sauvegarde en lot
            evaluationRepository.saveAll(evaluationsToSave);

        } catch (IOException e) {
            throw new IOException("Erreur lors de l'importation du fichier Excel", e);
        }
    }



    @Override
    public List<EvaluationDto> getAllEvaluations() {
        List<Evaluation> evaluations = evaluationRepository.findAll();
        List<EvaluationDto> evaluationDtos = new ArrayList<>();

        for (Evaluation evaluation : evaluations) {
            EvaluationDto dto = new EvaluationDto();
            dto.setEmployeId(evaluation.getEmploye().getId());
            dto.setKpiId(evaluation.getKpi().getId());
            dto.setNote(evaluation.getNote());
            dto.setAppreciation(evaluation.getAppreciation());
            evaluationDtos.add(dto);
        }

        return evaluationDtos;
    }
}
