package org.example.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mapper.ChildMapper;
import org.example.mapper.TemperatureRecordMapper;
import org.example.pojo.Child;
import org.example.pojo.TemperatureRecord;
import org.example.service.TemperatureRecordService;
import org.example.utils.ThreadLocalUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.Timestamp;

@Slf4j
@Service
@RequiredArgsConstructor
public class TemperatureRecordServiceImpl implements TemperatureRecordService {
    private final TemperatureRecordMapper temperatureRecordMapper;
    private final ChildMapper childMapper;

    @Override
    @Transactional
    public void addTemperatureRecord(TemperatureRecord record) {
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

        // 设置创建和更新时间
        LocalDateTime now = LocalDateTime.now();
        record.setCreatedAt(now);
        record.setUpdatedAt(now);

        temperatureRecordMapper.insert(record);
    }

    @Override
    public Map<String, Object> getTemperatureRecordList(Integer page, Integer pageSize, Long childId) {
        // 获取当前用户信息
        Map<String, Object> userInfo = ThreadLocalUtil.get();
        String userId = (String) userInfo.get("id");
        Integer roleId = (Integer) userInfo.get("roleId");

        // 如果是家长且未指定childId，获取家长的第一个孩子
        if (roleId == 3 && childId == null) {
            List<Child> children = childMapper.selectByParentId(userId);
            if (children.isEmpty()) {
                throw new RuntimeException("未找到您的孩子信息");
            }
            childId = children.get(0).getChildId();
        }

        // 如果是家长，验证是否是自己的孩子
        if (roleId == 3 && childId != null) {
            Child child = childMapper.selectChildById(childId);
            if (child == null || !child.getParentId().equals(userId)) {
                throw new RuntimeException("无权查看其他家长的儿童记录");
            }
        }

        // 计算分页起始位置
        int start = (page - 1) * pageSize;
        
        // 查询数据
        List<Map<String, Object>> records = temperatureRecordMapper.selectList(start, pageSize, childId);
        Integer total = temperatureRecordMapper.selectCount(childId);
        
        // 封装结果
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("rows", records);
        
        return result;
    }

    @Override
    public Map<String, Object> getTemperatureRecordDetail(Long recordId) {
        // 查询记录详情
        Map<String, Object> record = temperatureRecordMapper.selectById(recordId);
        if (record == null) {
            throw new RuntimeException("体温记录不存在");
        }

        // 转换时间字段
        record.put("createdAt", ((Timestamp) record.get("createdAt")).toLocalDateTime());
        record.put("updatedAt", ((Timestamp) record.get("updatedAt")).toLocalDateTime());

        // 获取当前用户信息
        Map<String, Object> userInfo = ThreadLocalUtil.get();
        String userId = (String) userInfo.get("id");
        Integer roleId = (Integer) userInfo.get("roleId");

        // 如果是家长，验证是否是自己孩子的记录
        if (roleId == 3) {
            Child child = childMapper.selectChildById((Long) record.get("childId"));
            if (child == null || !child.getParentId().equals(userId)) {
                throw new RuntimeException("无权查看其他家长的儿童记录");
            }
        }

        return record;
    }

    @Override
    @Transactional
    public void deleteTemperatureRecord(Long recordId) {
        // 验证记录是否存在
        Map<String, Object> record = temperatureRecordMapper.selectById(recordId);
        if (record == null) {
            throw new RuntimeException("体温记录不存在");
        }

        // 获取当前用户信息
        Map<String, Object> userInfo = ThreadLocalUtil.get();
        String userId = (String) userInfo.get("id");
        Integer roleId = (Integer) userInfo.get("roleId");

        // 如果是家长，验证是否是自己孩子的记录
        if (roleId == 3) {
            Child child = childMapper.selectChildById((Long) record.get("childId"));
            if (child == null || !child.getParentId().equals(userId)) {
                throw new RuntimeException("无权删除其他家长的儿童记录");
            }
        }

        temperatureRecordMapper.delete(recordId);
    }

    @Override
    @Transactional
    public void updateTemperatureRecord(TemperatureRecord record) {
        // 验证记录是否存在
        Map<String, Object> existingRecord = temperatureRecordMapper.selectById(record.getRecordId());
        if (existingRecord == null) {
            throw new RuntimeException("体温记录不存在");
        }

        // 转换时间字段
        LocalDateTime createdAt = ((Timestamp) existingRecord.get("createdAt")).toLocalDateTime();
        record.setCreatedAt(createdAt);

        // 获取当前用户信息
        Map<String, Object> userInfo = ThreadLocalUtil.get();
        String userId = (String) userInfo.get("id");
        Integer roleId = (Integer) userInfo.get("roleId");

        // 如果是家长，验证是否是自己孩子的记录
        if (roleId == 3) {
            Child child = childMapper.selectChildById((Long) existingRecord.get("childId"));
            if (child == null || !child.getParentId().equals(userId)) {
                throw new RuntimeException("无权修改其他家长的儿童记录");
            }
        }

        // 设置更新时间
        record.setUpdatedAt(LocalDateTime.now());

        temperatureRecordMapper.update(record);
    }
} 