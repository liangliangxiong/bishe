package org.example.service;

import org.example.pojo.MedicalRecord;
import java.util.Map;

public interface MedicalRecordService {
    /**
     * 获取就医记录列表
     */
    Map<String, Object> getMedicalRecordList(Integer page, Integer pageSize, Long childId);
    
    /**
     * 添加就医记录
     */
    void addMedicalRecord(MedicalRecord record);
    
    /**
     * 更新就医记录
     */
    void updateMedicalRecord(MedicalRecord record);
    
    /**
     * 删除就医记录
     */
    void deleteMedicalRecord(Long recordId);
} 