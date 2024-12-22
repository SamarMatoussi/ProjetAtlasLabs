package tn.Backend.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.Backend.dto.KpiDto;
import tn.Backend.dto.KpiWithParametrageDto;
import tn.Backend.dto.ParametrageKpiDto;
import tn.Backend.entites.Activites;
import tn.Backend.entites.Kpi;
import tn.Backend.repository.ActivitesRepository;
import tn.Backend.repository.KpiRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class KpiServiceImpl implements KpiService {

    @Autowired
    private KpiRepository kpiRepository;
    @Autowired
    private ActivitesRepository activitesRepository;
    @Autowired
    private ParametrageKpiService parametrageKpiService;


    @Override
    public List<KpiDto> getAllKpis() {
        // Récupère tous les KPI depuis le repository
        List<Kpi> kpiList = kpiRepository.findAll();

        // Mappe chaque KPI à son DTO
        return kpiList.stream()
                .map(KpiDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<KpiDto> getAllKpisByActivite(Long activiteId) {
        List<Kpi> kpi = kpiRepository.findAllByActivitesId(activiteId);
        return kpi.stream()
                .map(KpiDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<KpiDto> getKpiById(Long id) {
        return kpiRepository.findById(id)
                .map(KpiDto::fromEntity);
    }

    @Override
    public KpiDto saveOrUpdateKpi(KpiDto kpiDto) {
        if (kpiDto == null) {
            throw new IllegalArgumentException("KpiDto must not be null");
        }

        Optional<Activites> activites = activitesRepository.findById(kpiDto.getActiviteId());
        if (activites.isPresent()) {
            Activites activite = activites.get();
            Kpi kpi = KpiDto.toEntity(kpiDto);
            kpi.setActivites(activite);
            Kpi savedKpi = kpiRepository.save(kpi);
            return KpiDto.fromEntity(savedKpi);
        } else {
            // Handle the case where the Activites entity is not found
            throw new EntityNotFoundException("Activites not found with id: " + kpiDto.getActiviteId());
        }
    }
    /*public List<String> getOptionsByKpiId(Long kpiId) {
        // Remplacez cela par une véritable logique de récupération d'options
        return kpiRepository.findOptionsByKpiId(kpiId);
    }*/


    @Override
    public void deleteKpi(Long id) {
        kpiRepository.deleteById(id);
    }

    @Override
    public List<KpiWithParametrageDto> getAllKpiWithParametrageByActivite(Long activiteId) {
        // Vérifiez si l'ID d'activité est valide
        if (activiteId == null) {
            throw new IllegalArgumentException("L'ID d'activité ne peut pas être nul.");
        }

        // Récupérez tous les KPI associés à l'ID d'activité
        List<Kpi> kpiList = kpiRepository.findAllByActivitesId(activiteId);

        // Mappez chaque KPI à son DTO avec ses paramétrages
        return kpiList.stream()
                .map(kpi -> {
                    List<ParametrageKpiDto> parametrages = parametrageKpiService.getAllParametrageKpisByKpi(kpi.getId());
                    KpiWithParametrageDto dto = new KpiWithParametrageDto();
                    dto.setKpi(KpiDto.fromEntity(kpi));
                    dto.setParametrages(parametrages);
                    return dto;
                })
                .collect(Collectors.toList());
    }




}
