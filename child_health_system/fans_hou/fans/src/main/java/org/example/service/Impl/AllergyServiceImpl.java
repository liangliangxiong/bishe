package org.example.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mapper.AllergyMapper;
import org.example.mapper.ChildMapper;
import org.example.pojo.Allergy;
import org.example.pojo.Child;
import org.example.service.AllergyService;
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
public class AllergyServiceImpl implements AllergyService {
    
    private final AllergyMapper allergyMapper;
    private final ChildMapper childMapper;
    
    @Override
    public Map<String, Object> getAllergyList(Integer page, Integer pageSize, Long childId) {
        // 计算分页起始位置
        int start = (page - 1) * pageSize;
        
        // 查询数据
        List<Map<String, Object>> records = allergyMapper.getAllergyList(start, pageSize, childId);
        Integer total = allergyMapper.getAllergyCount(childId);
        
        // 封装结果
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("rows", records);
        
        return result;
    }
    
    @Override
    @Transactional
    public void addAllergy(Allergy allergy) {
        // 验证儿童信息
        Child child = childMapper.selectChildById(allergy.getChildId());
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
        allergy.setCreatedAt(now);
        allergy.setUpdatedAt(now);
        
        allergyMapper.insert(allergy);
    }
    
    @Override
    @Transactional
    public void updateAllergy(Allergy allergy) {
        // 验证记录是否存在
        Map<String, Object> existingRecord = allergyMapper.selectById(allergy.getAllergyId());
        if (existingRecord == null) {
            throw new RuntimeException("过敏记录不存在");
        }
        
        // 获取当前用户信息
        Map<String, Object> userInfo = ThreadLocalUtil.get();
        String userId = (String) userInfo.get("id");
        Integer roleId = (Integer) userInfo.get("roleId");
        
        // 获取 childId
        Object childIdObj = existingRecord.get("child_id");
        if (childIdObj == null) {
            throw new RuntimeException("记录数据异常：未找到关联的儿童ID");
        }
        Long childId = ((Number) childIdObj).longValue();
        allergy.setChildId(childId);
        
        // 如果是家长，验证是否是自己孩子的记录
        if (roleId == 3) {
            Child child = childMapper.selectChildById(childId);
            if (!child.getParentId().equals(userId)) {
                throw new RuntimeException("无权修改其他家长的儿童记录");
            }
        }
        
        // 设置更新时间
        allergy.setUpdatedAt(LocalDateTime.now());
        
        allergyMapper.update(allergy);
    }
    
    @Override
    @Transactional
    public void deleteAllergy(Long allergyId) {
        // 验证记录是否存在
        Map<String, Object> record = allergyMapper.selectById(allergyId);
        if (record == null) {
            throw new RuntimeException("过敏记录不存在");
        }
        
        // 获取当前用户信息
        Map<String, Object> userInfo = ThreadLocalUtil.get();
        String userId = (String) userInfo.get("id");
        Integer roleId = (Integer) userInfo.get("roleId");
        
        // 获取 childId
        Object childIdObj = record.get("child_id");
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
        
        allergyMapper.delete(allergyId);
    }
} 