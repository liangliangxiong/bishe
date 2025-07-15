package org.example.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mapper.MedicalRecordMapper;
import org.example.mapper.MedicationRecordMapper;
import org.example.mapper.ChildMapper;
import org.example.pojo.MedicationRecord;
import org.example.pojo.Child;
import org.example.service.MedicationRecordService;
import org.example.utils.ThreadLocalUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MedicationRecordServiceImpl implements MedicationRecordService {
    
    private final MedicationRecordMapper medicationRecordMapper;
    private final MedicalRecordMapper medicalRecordMapper;
    private final ChildMapper childMapper;
    
    @Override
    public List<Map<String, Object>> getMedicationList(Long medicalRecordId) {
        // 验证就医记录是否存在
        Map<String, Object> medicalRecord = medicalRecordMapper.selectById(medicalRecordId);
        if (medicalRecord == null) {
            throw new RuntimeException("就医记录不存在");
        }
        
        // 获取当前用户信息
        Map<String, Object> userInfo = ThreadLocalUtil.get();
        String userId = (String) userInfo.get("id");
        Integer roleId = (Integer) userInfo.get("roleId");
        
        // 如果是医生，验证是否是自己的记录
        if (roleId == 2) {
            String doctorId = (String) medicalRecord.get("doctor_id");
            if (!userId.equals(doctorId)) {
                throw new RuntimeException("无权查看其他医生的记录");
            }
        }
        
        return medicationRecordMapper.getMedicationsByMedicalId(medicalRecordId);
    }
    
    @Override
    @Transactional
    public void addMedicationRecord(MedicationRecord record) {
        // 验证就医记录是否存在
        Map<String, Object> medicalRecord = medicalRecordMapper.selectById(record.getMedicalRecordId());
        if (medicalRecord == null) {
            throw new RuntimeException("就医记录不存在");
        }
        
        // 获取当前用户信息
        Map<String, Object> userInfo = ThreadLocalUtil.get();
        String userId = (String) userInfo.get("id");
        Integer roleId = (Integer) userInfo.get("roleId");
        
        // 如果是医生，验证是否是自己的记录
        if (roleId == 2) {
            String doctorId = (String) medicalRecord.get("doctor_id");
            if (!userId.equals(doctorId)) {
                throw new RuntimeException("无权为其他医生的记录添加用药信息");
            }
        }
        
        // 设置创建和更新时间
        LocalDateTime now = LocalDateTime.now();
        record.setCreatedAt(now);
        record.setUpdatedAt(now);
        
        medicationRecordMapper.insert(record);
    }
    
    @Override
    @Transactional
    public void updateMedicationRecord(MedicationRecord record) {
        // 验证用药记录是否存在
        Map<String, Object> medicationRecord = medicationRecordMapper.selectById(record.getRecordId());
        if (medicationRecord == null) {
            throw new RuntimeException("用药记录不存在");
        }
        
        // 获取关联的就医记录
        Long medicalRecordId = ((Number) medicationRecord.get("medical_record_id")).longValue();
        Map<String, Object> medicalRecord = medicalRecordMapper.selectById(medicalRecordId);
        if (medicalRecord == null) {
            throw new RuntimeException("关联的就医记录不存在");
        }
        
        // 获取当前用户信息
        Map<String, Object> userInfo = ThreadLocalUtil.get();
        String userId = (String) userInfo.get("id");
        Integer roleId = (Integer) userInfo.get("roleId");
        
        // 如果是医生，验证是否是自己的记录
        if (roleId == 2) {
            String doctorId = (String) medicalRecord.get("doctor_id");
            if (!userId.equals(doctorId)) {
                throw new RuntimeException("无权修改其他医生的用药记录");
            }
        }
        
        // 获取儿童ID
        Object childIdObj = medicalRecord.get("child_id");
        if (childIdObj == null) {
            throw new RuntimeException("记录数据异常：未找到关联的儿童ID");
        }
        Long childId = ((Number) childIdObj).longValue();
        
        // 如果是家长，验证是否是自己孩子的记录
        if (roleId == 3) {
            Child child = childMapper.selectChildById(childId);
            if (!child.getParentId().equals(userId)) {
                throw new RuntimeException("无权修改其他家长儿童的用药记录");
            }
        }
        
        // 保持原有的就医记录ID
        record.setMedicalRecordId(medicalRecordId);
        
        // 设置更新时间
        record.setUpdatedAt(LocalDateTime.now());
        
        medicationRecordMapper.update(record);
    }
    
    @Override
    @Transactional
    public void deleteMedicationRecord(Long recordId) {
        // 验证用药记录是否存在
        Map<String, Object> medicationRecord = medicationRecordMapper.selectById(recordId);
        if (medicationRecord == null) {
            throw new RuntimeException("用药记录不存在");
        }
        
        // 获取关联的就医记录
        Long medicalRecordId = ((Number) medicationRecord.get("medical_record_id")).longValue();
        Map<String, Object> medicalRecord = medicalRecordMapper.selectById(medicalRecordId);
        if (medicalRecord == null) {
            throw new RuntimeException("关联的就医记录不存在");
        }
        
        // 获取当前用户信息
        Map<String, Object> userInfo = ThreadLocalUtil.get();
        String userId = (String) userInfo.get("id");
        Integer roleId = (Integer) userInfo.get("roleId");
        
        // 如果是医生，验证是否是自己的记录
        if (roleId == 2) {
            String doctorId = (String) medicalRecord.get("doctor_id");
            if (!userId.equals(doctorId)) {
                throw new RuntimeException("无权删除其他医生的用药记录");
            }
        }
        
        // 获取儿童ID
        Object childIdObj = medicalRecord.get("child_id");
        if (childIdObj == null) {
            throw new RuntimeException("记录数据异常：未找到关联的儿童ID");
        }
        Long childId = ((Number) childIdObj).longValue();
        
        // 如果是家长，验证是否是自己孩子的记录
        if (roleId == 3) {
            Child child = childMapper.selectChildById(childId);
            if (!child.getParentId().equals(userId)) {
                throw new RuntimeException("无权删除其他家长儿童的用药记录");
            }
        }
        
        medicationRecordMapper.delete(recordId);
    }
} 