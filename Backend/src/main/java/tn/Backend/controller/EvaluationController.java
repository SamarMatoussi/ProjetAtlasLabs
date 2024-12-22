package tn.Backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.Backend.dto.EvaluationDto;
import tn.Backend.services.EvaluationService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/evaluations")
public class EvaluationController {

    private static final Logger log = LoggerFactory.getLogger(EvaluationController.class);

    @Autowired
    private EvaluationService evaluationService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/import")
    public ResponseEntity<String> importEvaluationData(@RequestParam("file") MultipartFile file) {
        log.info("Début de l'importation des données d'évaluation");
        try {
            log.debug("Fichier reçu : {}", file.getOriginalFilename());
            evaluationService.importEvaluationData(file);
            log.info("Importation des données d'évaluation réussie");
            return ResponseEntity.ok("Données d'évaluation importées avec succès !");
        } catch (IOException e) {
            log.error("Erreur lors de l'importation des données", e);
            return ResponseEntity.status(500).body("Erreur lors de l'importation des données : " + e.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/")
    public ResponseEntity<List<EvaluationDto>> getAllEvaluations() {
        log.info("Récupération de toutes les évaluations");
        List<EvaluationDto> evaluations = evaluationService.getAllEvaluations();
        if (evaluations.isEmpty()) {
            log.info("Aucune évaluation trouvée");
            return ResponseEntity.noContent().build();
        }
        log.info("Nombre d'évaluations récupérées : {}", evaluations.size());
        return ResponseEntity.ok(evaluations);
    }
}
