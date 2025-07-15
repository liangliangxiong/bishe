package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pojo.MedicalRecord;
import org.example.pojo.Result;
import org.example.service.MedicalRecordService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/medical-records")
@RequiredArgsConstructor
public class MedicalRecordController {
    
    private final MedicalRecordService medicalRecordService;
    
    @GetMapping
    public Result<Map<String, Object>> getMedicalRecordList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long childId) {
        return Result.success(medicalRecordService.getMedicalRecordList(page, pageSize, childId));
    }
    
    @PostMapping
    public Result<Void> addMedicalRecord(@Validated(MedicalRecord.Add.class) @RequestBody MedicalRecord record) {
        medicalRecordService.addMedicalRecord(record);
        return Result.success();
    }
    
    @PutMapping("/{recordId}")
    public Result<Void> updateMedicalRecord(
            @PathVariable Long recordId,
            @Validated(MedicalRecord.Update.class) @RequestBody MedicalRecord record) {
        record.setRecordId(recordId);
        medicalRecordService.updateMedicalRecord(record);
        return Result.success();
    }
    
    @DeleteMapping("/{recordId}")
    public Result<Void> deleteMedicalRecord(@PathVariable Long recordId) {
        medicalRecordService.deleteMedicalRecord(recordId);
        return Result.success();
    }
} 