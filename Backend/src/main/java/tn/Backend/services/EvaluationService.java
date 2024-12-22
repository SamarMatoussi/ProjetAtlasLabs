package tn.Backend.services;

import org.springframework.web.multipart.MultipartFile;
import tn.Backend.dto.EvaluationDto;
import tn.Backend.entites.Evaluation;

import java.io.IOException;
import java.util.List;

public interface EvaluationService {
    void importEvaluationData(MultipartFile file) throws IOException;
    List<EvaluationDto> getAllEvaluations();
}
