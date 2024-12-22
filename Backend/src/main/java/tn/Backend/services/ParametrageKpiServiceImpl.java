package tn.Backend.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.Backend.dto.ParametrageKpiDto;
import tn.Backend.entites.Kpi;
import tn.Backend.entites.KpiType;
import tn.Backend.entites.ParametrageKpi;
import tn.Backend.repository.KpiRepository;
import tn.Backend.repository.ParametrageKpiRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParametrageKpiServiceImpl implements ParametrageKpiService {

    private final ParametrageKpiRepository parametrageKpiRepository;
    private final KpiRepository kpiRepository;

    @Override
    public List<ParametrageKpiDto> getAllParametrageKpisByKpi(Long kpiId) {
        List<ParametrageKpi> parametrageKpis = parametrageKpiRepository.findAllByKpiId(kpiId);
        return parametrageKpis.stream()
                .map(ParametrageKpiDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ParametrageKpiDto> getParametrageKpiById(Long id) {
        return parametrageKpiRepository.findById(id)
                .map(ParametrageKpiDto::fromEntity);
    }

    @Transactional
    @Override
    public ParametrageKpiDto saveOrUpdateParametrageKpi(ParametrageKpiDto parametrageKpiDto) {
        if (parametrageKpiDto == null) {
            throw new IllegalArgumentException("ParametrageKpiDto must not be null");
        }

        Kpi associatedKpi = kpiRepository.findById(parametrageKpiDto.getKpiId())
                .orElseThrow(() -> new EntityNotFoundException("Kpi not found with id: " + parametrageKpiDto.getKpiId()));

        // Validate fields based on KpiType
        validateParametrageKpi(parametrageKpiDto, associatedKpi);

        ParametrageKpi parametrageKpi = ParametrageKpiDto.toEntity(parametrageKpiDto);
        parametrageKpi.setKpi(associatedKpi);
        ParametrageKpi savedParametrageKpi = parametrageKpiRepository.save(parametrageKpi);
        return ParametrageKpiDto.fromEntity(savedParametrageKpi);
    }

    private void validateParametrageKpi(ParametrageKpiDto parametrageKpiDto, Kpi associatedKpi) {
        if (associatedKpi.getType() == KpiType.NUMERIQUE) {
            if (parametrageKpiDto.getMax() == null || parametrageKpiDto.getMin() == null) {
                throw new IllegalArgumentException("Max and Min are required for NUMERIQUE type");
            }
            if (parametrageKpiDto.getMin() >= parametrageKpiDto.getMax()) {
                throw new IllegalArgumentException("Min must be less than Max for NUMERIQUE type");
            }
        } else if (associatedKpi.getType() == KpiType.STRING) {
            if (parametrageKpiDto.getUtilite() == null) {
                throw new IllegalArgumentException("Utilité is required for STRING type");
            }
        }
    }

    @Override
    public void deleteParametrageKpi(Long id) {
        if (!parametrageKpiRepository.existsById(id)) {
            throw new EntityNotFoundException("ParametrageKpi not found with id: " + id);
        }
        parametrageKpiRepository.deleteById(id);
    }

    @Override
    public String getAppreciationByKpiIdAndNote(Long kpiId, double note) {
        List<ParametrageKpi> parametrageKpiList = parametrageKpiRepository.findAllByKpiId(kpiId);

        return parametrageKpiList.stream()
                .filter(p -> p.isNumeric() && p.getMin() != null && p.getMax() != null)
                .filter(p -> note >= p.getMin() && note <= p.getMax())
                .map(p -> p.getAppreciation().toString())
                .findFirst()
                .orElse("Aucune appréciation trouvée pour la note donnée");
    }

    @Override
    public String getAppreciationByKpiIdAndTexte(Long kpiId, String texte) {
        System.out.println("texte " + texte);
        List<ParametrageKpi> parametrageKpiList = parametrageKpiRepository.findAllByKpiId(kpiId);
        parametrageKpiList.forEach(elem -> System.out.println(" name " + elem.getName() + "is string " + elem.isString()));
        return parametrageKpiList.stream()
                .filter(p -> p.isString() && p.getUtilite() != null)
                .filter(p -> p.getUtilite().equalsIgnoreCase(texte))
                .map(p -> p.getAppreciation().toString())
                .findFirst()
                .orElse("Aucune appréciation trouvée pour le texte donné");
    }


}
