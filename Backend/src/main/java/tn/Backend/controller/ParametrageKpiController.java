package tn.Backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.Backend.dto.ParametrageKpiDto;

import tn.Backend.services.ParametrageKpiService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/parametragekpi")
@RequiredArgsConstructor
public class ParametrageKpiController {

    private final ParametrageKpiService parametrageKpiService;


    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/liste/{kpiId}")
    public List<ParametrageKpiDto> getAllParametrageKpis(@PathVariable Long kpiId) {
        return parametrageKpiService.getAllParametrageKpisByKpi(kpiId);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/parametrageKpi/{id}")
    public ParametrageKpiDto getParametrageKpiById(@PathVariable Long id) {
        return parametrageKpiService.getParametrageKpiById(id)
                .orElseThrow(() -> new RuntimeException("ParametrageKpi not found with id " + id));
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/addparametrageKpi")
    public ParametrageKpiDto createParametrageKpi(@RequestBody ParametrageKpiDto parametrageKpiDto) {
        return parametrageKpiService.saveOrUpdateParametrageKpi(parametrageKpiDto);
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/updateParametrageKpi/{id}")
    public ParametrageKpiDto updateParametrageKpi(@PathVariable Long id, @RequestBody ParametrageKpiDto parametrageKpiDto) {
        parametrageKpiDto.setId(id);
        return parametrageKpiService.saveOrUpdateParametrageKpi(parametrageKpiDto);
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public void deleteParametrageKpi(@PathVariable Long id) {
        parametrageKpiService.deleteParametrageKpi(id);
    }


    @GetMapping("/note/appericiation/{kpiId}")
    public String getAppreciationByKpiIdAndNote(@PathVariable Long kpiId , @RequestParam double note) {

        return parametrageKpiService.getAppreciationByKpiIdAndNote(kpiId , note);
    }

    @GetMapping("/texte/appericiation/{kpiId}")
    public String getAppreciationByKpiIdAndTexte(@PathVariable Long kpiId , @RequestParam String texte) {

        return parametrageKpiService.getAppreciationByKpiIdAndTexte(kpiId , texte);
    }

}
