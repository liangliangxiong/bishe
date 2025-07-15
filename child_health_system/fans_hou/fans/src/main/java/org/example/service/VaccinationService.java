package org.example.service;

import org.example.pojo.Vaccination;
import java.util.Map;

public interface VaccinationService {
    /**
     * 获取接种记录列表
     */
    Map<String, Object> getVaccinationList(Integer page, Integer pageSize, Long childId);
    
    /**
     * 添加接种记录
     */
    void addVaccination(Vaccination vaccination);
    
    /**
     * 更新接种记录
     */
    void updateVaccination(Vaccination vaccination);
    
    /**
     * 删除接种记录
     */
    void deleteVaccination(Long vaccinationId);
} 