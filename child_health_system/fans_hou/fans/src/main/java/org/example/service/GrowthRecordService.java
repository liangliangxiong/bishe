package org.example.service;

import org.example.pojo.GrowthRecord;
import java.util.List;
import java.util.Map;

public interface GrowthRecordService {
    /**
     * 获取生长记录列表（分页）
     * 管理员和医生可以查看所有记录
     */
    Map<String, Object> getGrowthRecordList(Integer page, Integer pageSize, String query);

    /**
     * 根据儿童ID获取生长记录列表
     * 家长只能查看自己孩子的记录
     */
    List<Map<String, Object>> getGrowthRecordsByChildId(Long childId);

    /**
     * 获取生长记录详情
     */
    Map<String, Object> getGrowthRecordById(Long recordId);

    /**
     * 添加生长记录
     */
    void addGrowthRecord(GrowthRecord record);

    /**
     * 更新生长记录
     */
    void updateGrowthRecord(GrowthRecord record);

    /**
     * 删除生长记录
     */
    void deleteGrowthRecord(Long recordId);
} 