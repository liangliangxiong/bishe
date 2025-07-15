package org.example.service;

import org.example.pojo.DietRecord;
import java.util.Map;

public interface DietRecordService {
    /**
     * 获取饮食记录列表
     */
    Map<String, Object> getDietRecordList(Integer page, Integer pageSize, Long childId);
    
    /**
     * 添加饮食记录
     */
    void addDietRecord(DietRecord record);
    
    /**
     * 更新饮食记录
     */
    void updateDietRecord(DietRecord record);
    
    /**
     * 删除饮食记录
     */
    void deleteDietRecord(Long recordId);

    /**
     * 获取儿童饮食统计信息
     * 包括各餐次数量统计、每日平均次数等
     */
    Map<String, Object> getDietStatistics(Long childId);
} 