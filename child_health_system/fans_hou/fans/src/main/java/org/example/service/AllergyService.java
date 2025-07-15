package org.example.service;

import org.example.pojo.Allergy;
import java.util.Map;

public interface AllergyService {
    /**
     * 获取过敏记录列表
     */
    Map<String, Object> getAllergyList(Integer page, Integer pageSize, Long childId);
    
    /**
     * 添加过敏记录
     */
    void addAllergy(Allergy allergy);
    
    /**
     * 更新过敏记录
     */
    void updateAllergy(Allergy allergy);
    
    /**
     * 删除过敏记录
     */
    void deleteAllergy(Long allergyId);
} 