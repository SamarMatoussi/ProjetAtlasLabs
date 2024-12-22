package tn.Backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.Backend.services.PointageService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/pointage")
public class PointageController {

    private final PointageService service;
    private static final Logger logger = LoggerFactory.getLogger(PointageController.class);

    public PointageController(PointageService service) {
        this.service = service;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadData(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            logger.warn("No file uploaded");
            return ResponseEntity.badRequest().body("No file uploaded");
        }

        try {
            service.savePointageToDatabase(file);
            logger.info("Data uploaded and saved to database successfully");
            return ResponseEntity.ok("Data uploaded and saved to database successfully");
        } catch (IllegalArgumentException e) {
            logger.error("File processing failed: Unsupported file type or invalid data in file", e);
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("File processing failed: Unsupported file type or invalid data in file");
        } catch (Exception e) {
            logger.error("An error occurred while processing the file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the file");
        }
    }
    @GetMapping("/days-by-month")
    public ResponseEntity<Map<Long, Map<String, Long>>> getDaysByMonthForAllPersons() {
        Map<Long, Map<String, Long>> result = service.getDaysByMonthForAllPersons();
        return ResponseEntity.ok(result);
    }
    @GetMapping("/presence-absence-by-month")
    public ResponseEntity<Map<Long, Map<String, Map<String, Long>>>> getPresenceAbsenceByMonthForAllPersons() {
        Map<Long, Map<String, Map<String, Long>>> result = service.getPresenceAbsenceByMonthForAllPersons();
        return ResponseEntity.ok(result);
    }

}
