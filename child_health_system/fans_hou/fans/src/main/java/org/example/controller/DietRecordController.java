package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pojo.DietRecord;
import org.example.pojo.Result;
import org.example.service.DietRecordService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/diet-records")
@RequiredArgsConstructor
public class DietRecordController {
    
    private final DietRecordService dietRecordService;
    
    @GetMapping
    public Result<Map<String, Object>> getDietRecordList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long childId) {
        return Result.success(dietRecordService.getDietRecordList(page, pageSize, childId));
    }
    
    @PostMapping
    public Result<Void> addDietRecord(@Validated(DietRecord.Add.class) @RequestBody DietRecord record) {
        dietRecordService.addDietRecord(record);
        return Result.success();
    }
    
    @PutMapping("/{recordId}")
    public Result<Void> updateDietRecord(
            @PathVariable Long recordId,
            @Validated(DietRecord.Update.class) @RequestBody DietRecord record) {
        record.setRecordId(recordId);
        dietRecordService.updateDietRecord(record);
        return Result.success();
    }

    @DeleteMapping("/{recordId}")
    public Result<Void> deleteDietRecord(@PathVariable Long recordId) {
        dietRecordService.deleteDietRecord(recordId);
        return Result.success();
    }
    
    @GetMapping("/statistics/{childId}")
    public Result<Map<String, Object>> getDietStatistics(@PathVariable Long childId) {
        return Result.success(dietRecordService.getDietStatistics(childId));
    }
    
    // ... 其他接口
} 