package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.pojo.GrowthRecord;
import java.util.List;
import java.util.Map;

@Mapper
public interface GrowthRecordMapper {
    /**
     * 分页查询生长记录列表
     */
    List<Map<String, Object>> getGrowthRecordList(@Param("start") Integer start, 
                                                 @Param("pageSize") Integer pageSize, 
                                                 @Param("query") String query);

    /**
     * 查询生长记录总数
     */
    Integer getGrowthRecordCount(@Param("query") String query);

    /**
     * 根据儿童ID查询生长记录列表
     */
    @Select("SELECT " +
            "gr.record_id as recordId, " +
            "gr.child_id as childId, " +
            "c.name as childName, " +
            "gr.height, " +
            "gr.weight, " +
            "gr.bmi, " +
            "gr.measure_date as measureDate, " +
            "gr.recorded_by as recordedBy, " +
            "gr.created_at as createdAt, " +
            "gr.updated_at as updatedAt " +
            "FROM growth_records gr " +
            "LEFT JOIN children c ON gr.child_id = c.child_id " +
            "WHERE gr.child_id = #{childId} " +
            "ORDER BY gr.measure_date DESC")
    @Results({
            @Result(column = "recordId", property = "recordId"),
            @Result(column = "childId", property = "childId"),
            @Result(column = "childName", property = "childName"),
            @Result(column = "height", property = "height"),
            @Result(column = "weight", property = "weight"),
            @Result(column = "bmi", property = "bmi"),
            @Result(column = "measureDate", property = "measureDate"),
            @Result(column = "recordedBy", property = "recordedBy"),
            @Result(column = "createdAt", property = "createdAt"),
            @Result(column = "updatedAt", property = "updatedAt")
    })
    List<Map<String, Object>> selectByChildId(Long childId);

    /**
     * 根据ID查询生长记录
     */
    @Select("SELECT " +
            "gr.record_id as recordId, " +
            "gr.child_id as childId, " +
            "c.name as childName, " +
            "gr.height, " +
            "gr.weight, " +
            "gr.bmi, " +
            "gr.measure_date as measureDate, " +
            "gr.recorded_by as recordedBy, " +
            "gr.created_at as createdAt, " +
            "gr.updated_at as updatedAt " +
            "FROM growth_records gr " +
            "LEFT JOIN children c ON gr.child_id = c.child_id " +
            "WHERE gr.record_id = #{recordId}")
    @Results({
            @Result(column = "recordId", property = "recordId"),
            @Result(column = "childId", property = "childId"),
            @Result(column = "childName", property = "childName"),
            @Result(column = "height", property = "height"),
            @Result(column = "weight", property = "weight"),
            @Result(column = "bmi", property = "bmi"),
            @Result(column = "measureDate", property = "measureDate"),
            @Result(column = "recordedBy", property = "recordedBy"),
            @Result(column = "createdAt", property = "createdAt"),
            @Result(column = "updatedAt", property = "updatedAt")
    })
    Map<String, Object> selectById(Long recordId);

    /**
     * 添加生长记录
     */
    @Insert("INSERT INTO growth_records (child_id, height, weight, measure_date, recorded_by, created_at, updated_at) " +
            "VALUES (#{childId}, #{height}, #{weight}, #{measureDate}, #{recordedBy}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "recordId")
    void insert(GrowthRecord record);

    /**
     * 更新生长记录
     */
    @Update("UPDATE growth_records SET " +
            "height = #{height}, " +
            "weight = #{weight}, " +
            "measure_date = #{measureDate}, " +
            "updated_at = #{updatedAt} " +
            "WHERE record_id = #{recordId}")
    void update(GrowthRecord record);

    /**
     * 删除生长记录
     */
    @Delete("DELETE FROM growth_records WHERE record_id = #{recordId}")
    void delete(Long recordId);
} 