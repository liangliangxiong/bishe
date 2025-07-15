package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.pojo.Vaccination;
import java.util.List;
import java.util.Map;

@Mapper
public interface VaccinationMapper {
    
    /**
     * 分页查询接种记录
     */
    @Select("""
        SELECT 
            v.vaccination_id,
            v.child_id,
            c.name as child_name,
            v.vaccine_name,
            v.vaccination_date,
            v.next_due_date,
            v.doctor_id,
            u.real_name as doctor_name,
            v.notes,
            v.created_at,
            v.updated_at
        FROM vaccinations v
        LEFT JOIN children c ON v.child_id = c.child_id
        LEFT JOIN user u ON v.doctor_id = u.user_id
        WHERE (#{childId} IS NULL OR v.child_id = #{childId})
        ORDER BY v.vaccination_date DESC
        LIMIT #{start}, #{pageSize}
        """)
    List<Map<String, Object>> getVaccinationList(@Param("start") Integer start, 
                                                @Param("pageSize") Integer pageSize,
                                                @Param("childId") Long childId);
    
    /**
     * 查询记录总数
     */
    @Select("""
        SELECT COUNT(*) 
        FROM vaccinations 
        WHERE (#{childId} IS NULL OR child_id = #{childId})
        """)
    Integer getVaccinationCount(@Param("childId") Long childId);
    
    /**
     * 添加接种记录
     */
    @Insert("""
        INSERT INTO vaccinations (
            child_id, vaccine_name, vaccination_date, next_due_date,
            doctor_id, notes, created_at, updated_at
        ) VALUES (
            #{childId}, #{vaccineName}, #{vaccinationDate}, #{nextDueDate},
            #{doctorId}, #{notes}, #{createdAt}, #{updatedAt}
        )
        """)
    @Options(useGeneratedKeys = true, keyProperty = "vaccinationId")
    void insert(Vaccination vaccination);
    
    /**
     * 更新接种记录
     */
    @Update("""
        UPDATE vaccinations SET
            vaccine_name = #{vaccineName},
            vaccination_date = #{vaccinationDate},
            next_due_date = #{nextDueDate},
            doctor_id = #{doctorId},
            notes = #{notes},
            updated_at = #{updatedAt}
        WHERE vaccination_id = #{vaccinationId}
        """)
    void update(Vaccination vaccination);
    
    /**
     * 删除接种记录
     */
    @Delete("DELETE FROM vaccinations WHERE vaccination_id = #{vaccinationId}")
    void delete(Long vaccinationId);
    
    /**
     * 查询单条记录
     */
    @Select("""
        SELECT 
            v.vaccination_id,
            v.child_id,
            c.name as child_name,
            v.vaccine_name,
            v.vaccination_date,
            v.next_due_date,
            v.doctor_id,
            u.real_name as doctor_name,
            v.notes,
            v.created_at,
            v.updated_at
        FROM vaccinations v
        LEFT JOIN children c ON v.child_id = c.child_id
        LEFT JOIN user u ON v.doctor_id = u.user_id
        WHERE v.vaccination_id = #{vaccinationId}
        """)
    Map<String, Object> selectById(@Param("vaccinationId") Long vaccinationId);
} 