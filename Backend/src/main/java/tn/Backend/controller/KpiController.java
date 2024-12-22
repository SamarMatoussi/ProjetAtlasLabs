package tn.Backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.Backend.dto.KpiDto;
import tn.Backend.dto.KpiWithParametrageDto;
import tn.Backend.services.KpiService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/kpi")
public class KpiController {

    private final KpiService kpiService;

    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT','EMPLOYE')")
    @GetMapping("allKPIs")
    public ResponseEntity<List<KpiDto>> getAllKpis() {
        List<KpiDto> kpis = kpiService.getAllKpis();
        return new ResponseEntity<>(kpis, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/liste/{activiteId}")
    public List<KpiDto> getAllKpis(@PathVariable Long activiteId , @RequestParam("page") int page , @RequestParam("page") int pageSize) {
        return kpiService.getAllKpisByActivite(activiteId);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/kpi/{id}")
    public KpiDto getKpiById(@PathVariable Long id) {
        return kpiService.getKpiById(id)
                .orElseThrow(() -> new RuntimeException("KPI not found with id " + id));
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/addkpi")
    public KpiDto createKpi(@RequestBody KpiDto kpiDto) {
        return kpiService.saveOrUpdateKpi(kpiDto);
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/updatekpi/{id}")
    public KpiDto updateKpi(@PathVariable Long id, @RequestBody KpiDto kpiDto) {
        kpiDto.setId(id);
        return kpiService.saveOrUpdateKpi(kpiDto);
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/deleteKpi/{id}")
    public void deleteKpi(@PathVariable Long id) {
        kpiService.deleteKpi(id);
    }

    @PreAuthorize("hasAnyAuthority('AGENT', 'ADMIN')")
    @GetMapping("/listeByActivite/{activiteId}")
    public List<KpiDto> getKpisByActivite(@PathVariable Long activiteId ) {
        return kpiService.getAllKpisByActivite(activiteId);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/listeWithParametrage/{activiteId}")
    public List<KpiWithParametrageDto> getAllKpiWithParametrageByActivite(@PathVariable Long activiteId) {
        return kpiService.getAllKpiWithParametrageByActivite(activiteId);
    }

    /*@PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/kpis/{kpiId}/options")
    public List<String> getOptionsByKpiId(@PathVariable Long kpiId) {
        return kpiService.getOptionsByKpiId(kpiId);
    }*/



}
