package org.example.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Update;
import org.example.pojo.TemperatureRecord;

import java.util.List;
import java.util.Map;

@Mapper
public interface TemperatureRecordMapper {
    @Insert("INSERT INTO temperature_records (child_id, temperature, measure_time, measure_position, notes, created_at, updated_at) " +
            "VALUES (#{childId}, #{temperature}, #{measureTime}, #{measurePosition}, #{notes}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "recordId")
    void insert(TemperatureRecord record);

    @Select("<script>" +
            "SELECT tr.record_id as recordId, " +
            "tr.child_id as childId, " +
            "c.name as childName, " +
            "tr.temperature, " +
            "tr.measure_time as measureTime, " +
            "tr.measure_position as measurePosition, " +
            "tr.notes, " +
            "tr.created_at as createdAt, " +
            "tr.updated_at as updatedAt " +
            "FROM temperature_records tr " +
            "LEFT JOIN children c ON tr.child_id = c.child_id " +
            "<where>" +
            "<if test='childId != null'>" +
            "AND tr.child_id = #{childId} " +
            "</if>" +
            "</where>" +
            "ORDER BY tr.measure_time DESC " +
            "LIMIT #{start}, #{pageSize}" +
            "</script>")
    List<Map<String, Object>> selectList(@Param("start") Integer start, 
                                       @Param("pageSize") Integer pageSize, 
                                       @Param("childId") Long childId);

    @Select("<script>" +
            "SELECT COUNT(*) FROM temperature_records " +
            "<where>" +
            "<if test='childId != null'>" +
            "AND child_id = #{childId} " +
            "</if>" +
            "</where>" +
            "</script>")
    Integer selectCount(@Param("childId") Long childId);

    @Select("SELECT " +
            "tr.record_id as recordId, " +
            "tr.child_id as childId, " +
            "c.name as childName, " +
            "tr.temperature, " +
            "tr.measure_time as measureTime, " +
            "tr.measure_position as measurePosition, " +
            "tr.notes, " +
            "tr.created_at as createdAt, " +
            "tr.updated_at as updatedAt " +
            "FROM temperature_records tr " +
            "LEFT JOIN children c ON tr.child_id = c.child_id " +
            "WHERE tr.record_id = #{recordId}")
    Map<String, Object> selectById(@Param("recordId") Long recordId);

    @Delete("DELETE FROM temperature_records WHERE record_id = #{recordId}")
    void delete(@Param("recordId") Long recordId);

    @Update("UPDATE temperature_records SET " +
            "temperature = #{temperature}, " +
            "measure_time = #{measureTime}, " +
            "measure_position = #{measurePosition}, " +
            "notes = #{notes}, " +
            "updated_at = #{updatedAt} " +
            "WHERE record_id = #{recordId}")
    void update(TemperatureRecord record);
} 