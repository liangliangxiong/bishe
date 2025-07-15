package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pojo.Allergy;
import org.example.pojo.Result;
import org.example.service.AllergyService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/allergies")
@RequiredArgsConstructor
public class AllergyController {
    
    private final AllergyService allergyService;
    
    @GetMapping
    public Result<Map<String, Object>> getAllergyList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long childId) {
        return Result.success(allergyService.getAllergyList(page, pageSize, childId));
    }
    
    @PostMapping
    public Result<Void> addAllergy(@Validated(Allergy.Add.class) @RequestBody Allergy allergy) {
        allergyService.addAllergy(allergy);
        return Result.success();
    }
    
    @PutMapping("/{allergyId}")
    public Result<Void> updateAllergy(
            @PathVariable Long allergyId,
            @Validated(Allergy.Update.class) @RequestBody Allergy allergy) {
        allergy.setAllergyId(allergyId);
        allergyService.updateAllergy(allergy);
        return Result.success();
    }
    
    @DeleteMapping("/{allergyId}")
    public Result<Void> deleteAllergy(@PathVariable Long allergyId) {
        allergyService.deleteAllergy(allergyId);
        return Result.success();
    }
} 