package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.pojo.MedicalRecord;

import java.util.List;
import java.util.Map;

@Mapper
public interface MedicalRecordMapper {
    
    /**
     * 分页查询就医记录
     */
    @Select("""
        SELECT 
            mr.record_id,
            mr.child_id,
            c.name as child_name,
            mr.doctor_id,
            u.real_name as doctor_name,
            mr.visit_date,
            mr.diagnosis,
            mr.treatment,
            mr.hospital_name,
            mr.department,
            mr.notes,
            mr.created_at,
            mr.updated_at
        FROM medical_records mr
        LEFT JOIN children c ON mr.child_id = c.child_id
        LEFT JOIN user u ON mr.doctor_id = u.user_id
        WHERE (#{childId} IS NULL OR mr.child_id = #{childId})
        ORDER BY mr.visit_date DESC
        LIMIT #{start}, #{pageSize}
        """)
    List<Map<String, Object>> getMedicalRecordList(@Param("start") Integer start, 
                                                  @Param("pageSize") Integer pageSize,
                                                  @Param("childId") Long childId);
    
    /**
     * 查询记录总数
     */
    @Select("""
        SELECT COUNT(*) 
        FROM medical_records 
        WHERE (#{childId} IS NULL OR child_id = #{childId})
        """)
    Integer getMedicalRecordCount(@Param("childId") Long childId);
    
    /**
     * 添加就医记录
     */
    @Insert("""
        INSERT INTO medical_records (
            child_id, doctor_id, visit_date, diagnosis, treatment, 
            hospital_name, department, notes, created_at, updated_at
        ) VALUES (
            #{childId}, #{doctorId}, #{visitDate}, #{diagnosis}, #{treatment},
            #{hospitalName}, #{department}, #{notes}, #{createdAt}, #{updatedAt}
        )
        """)
    @Options(useGeneratedKeys = true, keyProperty = "recordId")
    void insert(MedicalRecord record);
    
    /**
     * 更新就医记录
     */
    @Update("""
        UPDATE medical_records SET
            doctor_id = #{doctorId},
            visit_date = #{visitDate},
            diagnosis = #{diagnosis},
            treatment = #{treatment},
            hospital_name = #{hospitalName},
            department = #{department},
            notes = #{notes},
            updated_at = #{updatedAt}
        WHERE record_id = #{recordId}
        """)
    void update(MedicalRecord record);
    
    /**
     * 删除就医记录
     */
    @Delete("DELETE FROM medical_records WHERE record_id = #{recordId}")
    void delete(Long recordId);
    
    /**
     * 查询单条记录
     */
    @Select("""
        SELECT 
            mr.record_id,
            mr.child_id,
            c.name as child_name,
            mr.doctor_id,
            u.real_name as doctor_name,
            mr.visit_date,
            mr.diagnosis,
            mr.treatment,
            mr.hospital_name,
            mr.department,
            mr.notes,
            mr.created_at,
            mr.updated_at
        FROM medical_records mr
        LEFT JOIN children c ON mr.child_id = c.child_id
        LEFT JOIN user u ON mr.doctor_id = u.user_id
        WHERE mr.record_id = #{recordId}
        """)
    Map<String, Object> selectById(@Param("recordId") Long recordId);
} 