package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pojo.Result;
import org.example.pojo.Vaccination;
import org.example.service.VaccinationService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/vaccinations")
@RequiredArgsConstructor
public class VaccinationController {
    
    private final VaccinationService vaccinationService;
    
    @GetMapping
    public Result<Map<String, Object>> getVaccinationList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long childId) {
        return Result.success(vaccinationService.getVaccinationList(page, pageSize, childId));
    }
    
    @PostMapping
    public Result<Void> addVaccination(@Validated(Vaccination.Add.class) @RequestBody Vaccination vaccination) {
        vaccinationService.addVaccination(vaccination);
        return Result.success();
    }
    
    @PutMapping("/{vaccinationId}")
    public Result<Void> updateVaccination(
            @PathVariable Long vaccinationId,
            @Validated(Vaccination.Update.class) @RequestBody Vaccination vaccination) {
        vaccination.setVaccinationId(vaccinationId);
        vaccinationService.updateVaccination(vaccination);
        return Result.success();
    }
    
    @DeleteMapping("/{vaccinationId}")
    public Result<Void> deleteVaccination(@PathVariable Long vaccinationId) {
        vaccinationService.deleteVaccination(vaccinationId);
        return Result.success();
    }
} 