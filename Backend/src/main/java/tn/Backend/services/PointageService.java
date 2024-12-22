package tn.Backend.services;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface PointageService {
    void savePointageToDatabase(MultipartFile file);
    Map<Long, Map<String, Long>> getDaysByMonthForAllPersons();

    // New method to count present and absent days
    Map<Long, Map<String, Map<String, Long>>> getPresenceAbsenceByMonthForAllPersons();
}
