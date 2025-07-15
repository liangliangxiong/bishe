package org.example.service;

import org.example.pojo.TemperatureRecord;

import java.util.Map;

public interface TemperatureRecordService {
    void addTemperatureRecord(TemperatureRecord record);

    Map<String, Object> getTemperatureRecordList(Integer page, Integer pageSize, Long childId);

    Map<String, Object> getTemperatureRecordDetail(Long recordId);

    void deleteTemperatureRecord(Long recordId);

    void updateTemperatureRecord(TemperatureRecord record);
} 