package org.example.service;

import org.example.pojo.MedicationRecord;
import java.util.List;
import java.util.Map;

public interface MedicationRecordService {
    /**
     * 获取就医记录关联的用药记录列表
     */
    List<Map<String, Object>> getMedicationList(Long medicalRecordId);
    
    /**
     * 添加用药记录
     */
    void addMedicationRecord(MedicationRecord record);
    
    /**
     * 更新用药记录
     */
    void updateMedicationRecord(MedicationRecord record);
    
    /**
     * 删除用药记录
     */
    void deleteMedicationRecord(Long recordId);
} 