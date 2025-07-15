package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.pojo.Allergy;
import java.util.List;
import java.util.Map;

@Mapper
public interface AllergyMapper {
    
    /**
     * 分页查询过敏记录
     */
    @Select("""
        SELECT 
            a.allergy_id,
            a.child_id,
            c.name as child_name,
            a.allergy_name,
            a.severity,
            a.diagnosis_date,
            a.notes,
            a.created_at,
            a.updated_at
        FROM allergies a
        LEFT JOIN children c ON a.child_id = c.child_id
        WHERE (#{childId} IS NULL OR a.child_id = #{childId})
        ORDER BY a.diagnosis_date DESC
        LIMIT #{start}, #{pageSize}
        """)
    List<Map<String, Object>> getAllergyList(@Param("start") Integer start, 
                                           @Param("pageSize") Integer pageSize,
                                           @Param("childId") Long childId);
    
    /**
     * 查询记录总数
     */
    @Select("""
        SELECT COUNT(*) 
        FROM allergies 
        WHERE (#{childId} IS NULL OR child_id = #{childId})
        """)
    Integer getAllergyCount(@Param("childId") Long childId);
    
    /**
     * 添加过敏记录
     */
    @Insert("""
        INSERT INTO allergies (
            child_id, allergy_name, severity, diagnosis_date,
            notes, created_at, updated_at
        ) VALUES (
            #{childId}, #{allergyName}, #{severity}, #{diagnosisDate},
            #{notes}, #{createdAt}, #{updatedAt}
        )
        """)
    @Options(useGeneratedKeys = true, keyProperty = "allergyId")
    void insert(Allergy allergy);
    
    /**
     * 更新过敏记录
     */
    @Update("""
        UPDATE allergies SET
            allergy_name = #{allergyName},
            severity = #{severity},
            diagnosis_date = #{diagnosisDate},
            notes = #{notes},
            updated_at = #{updatedAt}
        WHERE allergy_id = #{allergyId}
        """)
    void update(Allergy allergy);
    
    /**
     * 删除过敏记录
     */
    @Delete("DELETE FROM allergies WHERE allergy_id = #{allergyId}")
    void delete(Long allergyId);
    
    /**
     * 查询单条记录
     */
    @Select("""
        SELECT 
            a.allergy_id,
            a.child_id,
            c.name as child_name,
            a.allergy_name,
            a.severity,
            a.diagnosis_date,
            a.notes,
            a.created_at,
            a.updated_at
        FROM allergies a
        LEFT JOIN children c ON a.child_id = c.child_id
        WHERE a.allergy_id = #{allergyId}
        """)
    Map<String, Object> selectById(@Param("allergyId") Long allergyId);
} 