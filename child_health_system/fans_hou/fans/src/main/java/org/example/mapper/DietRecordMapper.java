package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.pojo.DietRecord;

import java.util.List;
import java.util.Map;

@Mapper
public interface DietRecordMapper {
    
    /**
     * 分页查询饮食记录
     */
    @Select("""
        SELECT 
            dr.record_id,
            dr.child_id,
            c.name as child_name,
            dr.meal_type,
            dr.meal_time,
            dr.food_items,
            dr.amount,
            dr.notes,
            dr.created_at,
            dr.updated_at
        FROM diet_records dr
        LEFT JOIN children c ON dr.child_id = c.child_id
        WHERE (#{childId} IS NULL OR dr.child_id = #{childId})
        ORDER BY dr.meal_time DESC
        LIMIT #{start}, #{pageSize}
        """)
    List<Map<String, Object>> getDietRecordList(@Param("start") Integer start, 
                                               @Param("pageSize") Integer pageSize,
                                               @Param("childId") Long childId);
    
    /**
     * 查询记录总数
     */
    @Select("""
        SELECT COUNT(*) 
        FROM diet_records 
        WHERE (#{childId} IS NULL OR child_id = #{childId})
        """)
    Integer getDietRecordCount(@Param("childId") Long childId);
    
    /**
     * 添加饮食记录
     */
    @Insert("""
        INSERT INTO diet_records (
            child_id, meal_type, meal_time, food_items, amount, notes, created_at, updated_at
        ) VALUES (
            #{childId}, #{mealType}, #{mealTime}, #{foodItems}, #{amount}, #{notes}, #{createdAt}, #{updatedAt}
        )
        """)
    @Options(useGeneratedKeys = true, keyProperty = "recordId")
    void insert(DietRecord record);
    
    /**
     * 更新饮食记录
     */
    @Update("""
        UPDATE diet_records SET
            meal_type = #{mealType},
            meal_time = #{mealTime},
            food_items = #{foodItems},
            amount = #{amount},
            notes = #{notes},
            updated_at = #{updatedAt}
        WHERE record_id = #{recordId}
        """)
    void update(DietRecord record);
    
    /**
     * 删除饮食记录
     */
    @Delete("DELETE FROM diet_records WHERE record_id = #{recordId}")
    void delete(Long recordId);
    
    /**
     * 查询单条记录
     */
    @Select("""
        SELECT 
            dr.record_id,
            dr.child_id,
            c.name as child_name,
            dr.meal_type,
            dr.meal_time,
            dr.food_items,
            dr.amount,
            dr.notes,
            dr.created_at,
            dr.updated_at
        FROM diet_records dr
        LEFT JOIN children c ON dr.child_id = c.child_id
        WHERE dr.record_id = #{recordId}
        """)
    Map<String, Object> selectById(@Param("recordId") Long recordId);
    
    /**
     * 获取饮食统计信息
     */
    @Select("""
        SELECT 
            meal_type,
            COUNT(*) as count,
            DATE_FORMAT(meal_time, '%Y-%m') as month,
            DATE_FORMAT(meal_time, '%Y-%m-%d') as date
        FROM diet_records 
        WHERE child_id = #{childId}
        GROUP BY 
            meal_type, 
            DATE_FORMAT(meal_time, '%Y-%m'),
            DATE_FORMAT(meal_time, '%Y-%m-%d')
        ORDER BY date DESC, meal_type
        """)
    List<Map<String, Object>> getDietStatistics(@Param("childId") Long childId);
    
    /**
     * 获取最近一周的饮食记录
     */
    @Select("""
        SELECT 
            meal_type,
            meal_time,
            food_items,
            amount
        FROM diet_records 
        WHERE child_id = #{childId}
            AND meal_time >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)
        ORDER BY meal_time DESC
        """)
    List<Map<String, Object>> getWeeklyDietRecords(@Param("childId") Long childId);
} 