package org.example.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mapper.ChildMapper;
import org.example.mapper.DietRecordMapper;
import org.example.pojo.Child;
import org.example.pojo.DietRecord;
import org.example.service.DietRecordService;
import org.example.utils.ThreadLocalUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DietRecordServiceImpl implements DietRecordService {
    
    private final DietRecordMapper dietRecordMapper;
    private final ChildMapper childMapper;
    
    @Override
    public Map<String, Object> getDietRecordList(Integer page, Integer pageSize, Long childId) {
        // 计算分页起始位置
        int start = (page - 1) * pageSize;
        
        // 查询数据
        List<Map<String, Object>> records = dietRecordMapper.getDietRecordList(start, pageSize, childId);
        Integer total = dietRecordMapper.getDietRecordCount(childId);
        
        // 封装结果
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("rows", records);
        
        return result;
    }
    
    @Override
    @Transactional
    public void addDietRecord(DietRecord record) {
        // 验证儿童信息
        Child child = childMapper.selectChildById(record.getChildId());
        if (child == null) {
            throw new RuntimeException("儿童信息不存在");
        }
        
        // 获取当前用户信息
        Map<String, Object> userInfo = ThreadLocalUtil.get();
        String userId = (String) userInfo.get("id");
        Integer roleId = (Integer) userInfo.get("roleId");
        
        // 如果是家长，验证是否是自己的孩子
        if (roleId == 3 && !child.getParentId().equals(userId)) {
            throw new RuntimeException("无权为其他家长的儿童添加记录");
        }
        
        // 设置创建和更新时间
        LocalDateTime now = LocalDateTime.now();
        record.setCreatedAt(now);
        record.setUpdatedAt(now);
        
        dietRecordMapper.insert(record);
    }

    @Override
    @Transactional
    public void updateDietRecord(DietRecord record) {
        // 验证记录是否存在
        Map<String, Object> existingRecord = dietRecordMapper.selectById(record.getRecordId());
        if (existingRecord == null) {
            throw new RuntimeException("饮食记录不存在");
        }

        // 获取当前用户信息
        Map<String, Object> userInfo = ThreadLocalUtil.get();
        String userId = (String) userInfo.get("id");
        Integer roleId = (Integer) userInfo.get("roleId");

        // 获取原记录的 childId 用于权限验证
        Object childIdObj = existingRecord.get("child_id");  // 注意：这里使用 child_id 而不是 childId
        if (childIdObj == null) {
            throw new RuntimeException("记录数据异常：未找到关联的儿童ID");
        }
        Long childId = ((Number) childIdObj).longValue();
        record.setChildId(childId);  // 设置 childId，确保更新时使用原来的值

        // 如果是家长，验证是否是自己孩子的记录
        if (roleId == 3) {
            Child child = childMapper.selectChildById(childId);
            if (!child.getParentId().equals(userId)) {
                throw new RuntimeException("无权修改其他家长的儿童记录");
            }
        }

        // 设置更新时间
        record.setUpdatedAt(LocalDateTime.now());
        
        dietRecordMapper.update(record);
    }

    @Override
    @Transactional
    public void deleteDietRecord(Long recordId) {
        // 验证记录是否存在
        Map<String, Object> record = dietRecordMapper.selectById(recordId);
        if (record == null) {
            throw new RuntimeException("饮食记录不存在");
        }

        // 获取当前用户信息
        Map<String, Object> userInfo = ThreadLocalUtil.get();
        String userId = (String) userInfo.get("id");
        Integer roleId = (Integer) userInfo.get("roleId");

        // 获取 childId
        Object childIdObj = record.get("child_id");  // 修改这里：使用 child_id 而不是 childId
        if (childIdObj == null) {
            throw new RuntimeException("记录数据异常：未找到关联的儿童ID");
        }
        Long childId = ((Number) childIdObj).longValue();

        // 如果是家长，验证是否是自己孩子的记录
        if (roleId == 3) {
            Child child = childMapper.selectChildById(childId);
            if (!child.getParentId().equals(userId)) {
                throw new RuntimeException("无权删除其他家长的儿童记录");
            }
        }

        dietRecordMapper.delete(recordId);
    }

    @Override
    public Map<String, Object> getDietStatistics(Long childId) {
        // 验证儿童信息
        Child child = childMapper.selectChildById(childId);
        if (child == null) {
            throw new RuntimeException("儿童信息不存在");
        }

        // 获取当前用户信息
        Map<String, Object> userInfo = ThreadLocalUtil.get();
        String userId = (String) userInfo.get("id");
        Integer roleId = (Integer) userInfo.get("roleId");

        // 如果是家长，验证是否是自己的孩子
        if (roleId == 3 && !child.getParentId().equals(userId)) {
            throw new RuntimeException("无权查看其他家长的儿童记录");
        }

        // 获取统计数据并处理
        List<Map<String, Object>> statisticsList = dietRecordMapper.getDietStatistics(childId);
        Map<String, Object> statisticsMap = new HashMap<>();
        Map<String, Map<String, Integer>> monthlyStats = new HashMap<>();
        Map<String, Map<String, Integer>> dailyStats = new HashMap<>();
        
        // 处理统计数据
        for (Map<String, Object> stat : statisticsList) {
            String mealType = (String) stat.get("meal_type");
            String month = (String) stat.get("month");
            String date = (String) stat.get("date");
            Integer count = ((Number) stat.get("count")).intValue();
            
            // 按月统计
            monthlyStats.computeIfAbsent(mealType, k -> new HashMap<>())
                        .put(month, count);
            
            // 按日统计
            dailyStats.computeIfAbsent(mealType, k -> new HashMap<>())
                      .put(date, count);
        }
        
        statisticsMap.put("monthly", monthlyStats);
        statisticsMap.put("daily", dailyStats);
        
        // 获取最近一周的记录
        List<Map<String, Object>> weeklyRecords = dietRecordMapper.getWeeklyDietRecords(childId);
        
        // 组装结果
        Map<String, Object> result = new HashMap<>();  // 创建 result Map
        result.put("statistics", statisticsMap);
        result.put("weeklyRecords", weeklyRecords);
        
        return result;
    }

    // ... 其他方法实现
} 