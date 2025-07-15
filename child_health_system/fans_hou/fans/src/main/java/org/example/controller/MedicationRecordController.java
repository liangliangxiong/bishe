package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pojo.MedicationRecord;
import org.example.pojo.Result;
import org.example.service.MedicationRecordService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/medication-records")
@RequiredArgsConstructor
public class MedicationRecordController {
    
    private final MedicationRecordService medicationRecordService;
    
    @GetMapping("/medical/{medicalRecordId}")
    public Result<List<Map<String, Object>>> getMedicationList(@PathVariable Long medicalRecordId) {
        return Result.success(medicationRecordService.getMedicationList(medicalRecordId));
    }
    
    @PostMapping
    public Result<Void> addMedicationRecord(@Validated(MedicationRecord.Add.class) @RequestBody MedicationRecord record) {
        medicationRecordService.addMedicationRecord(record);
        return Result.success();
    }
    
    @PutMapping("/{recordId}")
    public Result<Void> updateMedicationRecord(
            @PathVariable Long recordId,
            @Validated(MedicationRecord.Update.class) @RequestBody MedicationRecord record) {
        record.setRecordId(recordId);
        medicationRecordService.updateMedicationRecord(record);
        return Result.success();
    }
    
    @DeleteMapping("/{recordId}")
    public Result<Void> deleteMedicationRecord(@PathVariable Long recordId) {
        medicationRecordService.deleteMedicationRecord(recordId);
        return Result.success();
    }
} 