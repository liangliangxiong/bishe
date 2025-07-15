package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.pojo.MedicationRecord;
import java.util.List;
import java.util.Map;

@Mapper
public interface MedicationRecordMapper {
    
    /**
     * 查询就医记录关联的用药记录
     */
    @Select("""
        SELECT 
            record_id,
            medical_record_id,
            medicine_name,
            dosage,
            frequency,
            start_date,
            end_date,
            notes,
            created_at,
            updated_at
        FROM medication_records 
        WHERE medical_record_id = #{medicalRecordId}
        ORDER BY start_date
        """)
    List<Map<String, Object>> getMedicationsByMedicalId(@Param("medicalRecordId") Long medicalRecordId);
    
    /**
     * 添加用药记录
     */
    @Insert("""
        INSERT INTO medication_records (
            medical_record_id, medicine_name, dosage, frequency,
            start_date, end_date, notes, created_at, updated_at
        ) VALUES (
            #{medicalRecordId}, #{medicineName}, #{dosage}, #{frequency},
            #{startDate}, #{endDate}, #{notes}, #{createdAt}, #{updatedAt}
        )
        """)
    @Options(useGeneratedKeys = true, keyProperty = "recordId")
    void insert(MedicationRecord record);
    
    /**
     * 更新用药记录
     */
    @Update("""
        UPDATE medication_records SET
            medicine_name = #{medicineName},
            dosage = #{dosage},
            frequency = #{frequency},
            start_date = #{startDate},
            end_date = #{endDate},
            notes = #{notes},
            updated_at = #{updatedAt}
        WHERE record_id = #{recordId}
        """)
    void update(MedicationRecord record);
    
    /**
     * 删除用药记录
     */
    @Delete("DELETE FROM medication_records WHERE record_id = #{recordId}")
    void delete(Long recordId);
    
    /**
     * 删除就医记录关联的所有用药记录
     */
    @Delete("DELETE FROM medication_records WHERE medical_record_id = #{medicalRecordId}")
    void deleteByMedicalRecordId(@Param("medicalRecordId") Long medicalRecordId);
    
    /**
     * 查询单条用药记录
     */
    @Select("""
        SELECT 
            record_id,
            medical_record_id,
            medicine_name,
            dosage,
            frequency,
            start_date,
            end_date,
            notes,
            created_at,
            updated_at
        FROM medication_records 
        WHERE record_id = #{recordId}
        """)
    Map<String, Object> selectById(@Param("recordId") Long recordId);
} 