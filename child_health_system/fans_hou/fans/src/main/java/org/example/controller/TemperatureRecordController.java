package org.example.controller;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pojo.Result;
import org.example.pojo.TemperatureRecord;
import org.example.service.TemperatureRecordService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/temperature-records")
@RequiredArgsConstructor
public class TemperatureRecordController {
    private final TemperatureRecordService temperatureRecordService;

    @PostMapping
    public Result<Void> addTemperatureRecord(@RequestBody @Validated(TemperatureRecord.Add.class) TemperatureRecord record) {
        log.info("添加体温记录: {}", record);
        temperatureRecordService.addTemperatureRecord(record);
        return Result.success();
    }

    @GetMapping
    public Result<Map<String, Object>> getTemperatureRecordList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long childId) {
        log.info("获取体温记录列表: page={}, pageSize={}, childId={}", page, pageSize, childId);
        return Result.success(temperatureRecordService.getTemperatureRecordList(page, pageSize, childId));
    }

    @GetMapping("/{recordId}")
    public Result<Map<String, Object>> getTemperatureRecordDetail(@PathVariable Long recordId) {
        log.info("获取体温记录详情: recordId={}", recordId);
        return Result.success(temperatureRecordService.getTemperatureRecordDetail(recordId));
    }

    @DeleteMapping("/{recordId}")
    public Result<Void> deleteTemperatureRecord(@PathVariable Long recordId) {
        log.info("删除体温记录: recordId={}", recordId);
        temperatureRecordService.deleteTemperatureRecord(recordId);
        return Result.success();
    }

    @PutMapping("/{recordId}")
    public Result<Void> updateTemperatureRecord(@PathVariable Long recordId, @RequestBody @Valid TemperatureRecord record) {
        log.info("更新体温记录: recordId={}, record={}", recordId, record);
        record.setRecordId(recordId);
        temperatureRecordService.updateTemperatureRecord(record);
        return Result.success();
    }
} 