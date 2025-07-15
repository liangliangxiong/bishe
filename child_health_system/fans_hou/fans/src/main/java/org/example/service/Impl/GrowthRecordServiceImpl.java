package org.example.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mapper.ChildMapper;
import org.example.mapper.GrowthRecordMapper;
import org.example.pojo.Child;
import org.example.pojo.GrowthRecord;
import org.example.service.GrowthRecordService;
import org.example.utils.ThreadLocalUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GrowthRecordServiceImpl implements GrowthRecordService {
    private final GrowthRecordMapper growthRecordMapper;
    private final ChildMapper childMapper;

    @Override
    public Map<String, Object> getGrowthRecordList(Integer page, Integer pageSize, String query) {
        // 计算分页起始位置
        int start = (page - 1) * pageSize;
        
        // 查询数据
        List<Map<String, Object>> records = growthRecordMapper.getGrowthRecordList(start, pageSize, query);
        Integer total = growthRecordMapper.getGrowthRecordCount(query);
        
        // 封装结果
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("rows", records);
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getGrowthRecordsByChildId(Long childId) {
        // 验证儿童信息是否存在
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
            throw new RuntimeException("无权查看其他家长的儿童信息");
        }

        return growthRecordMapper.selectByChildId(childId);
    }

    @Override
    public Map<String, Object> getGrowthRecordById(Long recordId) {
        Map<String, Object> record = growthRecordMapper.selectById(recordId);
        if (record == null) {
            throw new RuntimeException("生长记录不存在");
        }

        // 获取当前用户信息
        Map<String, Object> userInfo = ThreadLocalUtil.get();
        String userId = (String) userInfo.get("id");
        Integer roleId = (Integer) userInfo.get("roleId");

        // 如果是家长，验证是否是自己孩子的记录
        if (roleId == 3) {
            Child child = childMapper.selectChildById((Long) record.get("childId"));
            if (!child.getParentId().equals(userId)) {
                throw new RuntimeException("无权查看其他家长的儿童记录");
            }
        }

        return record;
    }

    @Override
    @Transactional
    public void addGrowthRecord(GrowthRecord record) {
        // 验证儿童信息是否存在
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

        // 计算BMI (仅用于显示，不会存入数据库)
        if (record.getHeight() != null && record.getWeight() != null) {
            BigDecimal heightInMeters = record.getHeight().divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
            BigDecimal bmi = record.getWeight().divide(heightInMeters.multiply(heightInMeters), 1, RoundingMode.HALF_UP);
            record.setBmi(bmi);
        }

        // 设置记录人和时间
        record.setRecordedBy(userId);
        LocalDateTime now = LocalDateTime.now();
        record.setCreatedAt(now);
        record.setUpdatedAt(now);

        growthRecordMapper.insert(record);
    }

    @Override
    @Transactional
    public void updateGrowthRecord(GrowthRecord record) {
        // 验证记录是否存在
        Map<String, Object> existingRecord = growthRecordMapper.selectById(record.getRecordId());
        if (existingRecord == null) {
            throw new RuntimeException("生长记录不存在");
        }

        // 设置childId
        record.setChildId((Long) existingRecord.get("childId"));

        // 获取当前用户信息
        Map<String, Object> userInfo = ThreadLocalUtil.get();
        String userId = (String) userInfo.get("id");
        Integer roleId = (Integer) userInfo.get("roleId");

        // 如果是家长，验证是否是自己孩子的记录
        if (roleId == 3) {
            Child child = childMapper.selectChildById(record.getChildId());
            if (!child.getParentId().equals(userId)) {
                throw new RuntimeException("无权修改其他家长的儿童记录");
            }
        }

        // 设置更新时间
        record.setUpdatedAt(LocalDateTime.now());

        growthRecordMapper.update(record);
    }

    @Override
    @Transactional
    public void deleteGrowthRecord(Long recordId) {
        // 验证记录是否存在
        Map<String, Object> record = growthRecordMapper.selectById(recordId);
        if (record == null) {
            throw new RuntimeException("生长记录不存在");
        }

        // 获取当前用户信息
        Map<String, Object> userInfo = ThreadLocalUtil.get();
        String userId = (String) userInfo.get("id");
        Integer roleId = (Integer) userInfo.get("roleId");

        // 如果是家长，验证是否是自己孩子的记录
        if (roleId == 3) {
            Child child = childMapper.selectChildById((Long) record.get("childId"));
            if (!child.getParentId().equals(userId)) {
                throw new RuntimeException("无权删除其他家长的儿童记录");
            }
        }

        growthRecordMapper.delete(recordId);
    }
} 