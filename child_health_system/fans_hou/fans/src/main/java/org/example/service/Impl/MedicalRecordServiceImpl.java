package org.example.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mapper.ChildMapper;
import org.example.mapper.MedicalRecordMapper;
import org.example.mapper.MedicationRecordMapper;
import org.example.pojo.Child;
import org.example.pojo.MedicalRecord;
import org.example.service.MedicalRecordService;
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
public class MedicalRecordServiceImpl implements MedicalRecordService {
    
    private final MedicalRecordMapper medicalRecordMapper;
    private final ChildMapper childMapper;
    private final MedicationRecordMapper medicationRecordMapper;
    
    @Override
    public Map<String, Object> getMedicalRecordList(Integer page, Integer pageSize, Long childId) {
        // 计算分页起始位置
        int start = (page - 1) * pageSize;
        
        // 查询数据
        List<Map<String, Object>> records = medicalRecordMapper.getMedicalRecordList(start, pageSize, childId);
        Integer total = medicalRecordMapper.getMedicalRecordCount(childId);
        
        // 查询每条就医记录关联的用药记录
        for (Map<String, Object> record : records) {
            Long recordId = ((Number) record.get("record_id")).longValue();
            List<Map<String, Object>> medications = medicationRecordMapper.getMedicationsByMedicalId(recordId);
            record.put("medications", medications);
        }
        
        // 封装结果
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("rows", records);
        
        return result;
    }
    
    @Override
    @Transactional
    public void addMedicalRecord(MedicalRecord record) {
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
        
        // 如果是医生，设置医生ID
        if (roleId == 2) {
            record.setDoctorId(userId);
        }
        
        // 设置创建和更新时间
        LocalDateTime now = LocalDateTime.now();
        record.setCreatedAt(now);
        record.setUpdatedAt(now);
        
        medicalRecordMapper.insert(record);
    }
    
    @Override
    @Transactional
    public void updateMedicalRecord(MedicalRecord record) {
        // 验证记录是否存在
        Map<String, Object> existingRecord = medicalRecordMapper.selectById(record.getRecordId());
        if (existingRecord == null) {
            throw new RuntimeException("就医记录不存在");
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
        record.setChildId(childId);
        
        // 如果是家长，验证是否是自己孩子的记录
        if (roleId == 3) {
            Child child = childMapper.selectChildById(childId);
            if (!child.getParentId().equals(userId)) {
                throw new RuntimeException("无权修改其他家长的儿童记录");
            }
        }
        
        // 如果是医生，验证是否是自己的记录
        if (roleId == 2) {
            String doctorId = (String) existingRecord.get("doctor_id");
            if (!userId.equals(doctorId)) {
                throw new RuntimeException("无权修改其他医生的记录");
            }
            record.setDoctorId(userId);
        }
        
        // 设置更新时间
        record.setUpdatedAt(LocalDateTime.now());
        
        medicalRecordMapper.update(record);
    }
    
    @Override
    @Transactional
    public void deleteMedicalRecord(Long recordId) {
        // 验证记录是否存在
        Map<String, Object> record = medicalRecordMapper.selectById(recordId);
        if (record == null) {
            throw new RuntimeException("就医记录不存在");
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
        
        // 如果是医生，验证是否是自己的记录
        if (roleId == 2) {
            String doctorId = (String) record.get("doctor_id");
            if (!userId.equals(doctorId)) {
                throw new RuntimeException("无权删除其他医生的记录");
            }
        }
        
        // 先删除关联的用药记录
        medicationRecordMapper.deleteByMedicalRecordId(recordId);
        
        // 再删除就医记录
        medicalRecordMapper.delete(recordId);
    }
} 