package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.pojo.Child;
import java.util.List;

@Mapper
public interface ChildMapper {
    /**
     * 分页查询儿童列表
     */
    List<Child> selectChildList(@Param("offset") Integer offset,
                              @Param("pageSize") Integer pageSize,
                              @Param("query") String query);

    /**
     * 查询儿童总数
     */
    Integer selectChildCount(@Param("query") String query);

    /**
     * 查询所有儿童列表
     */
    @Select("SELECT * FROM children ORDER BY created_at DESC")
    @Results({
            @Result(column = "child_id", property = "childId"),
            @Result(column = "parent_id", property = "parentId"),
            @Result(column = "gender", property = "gender", typeHandler = org.example.handler.GenderEnumTypeHandler.class),
            @Result(column = "blood_type", property = "bloodType", typeHandler = org.apache.ibatis.type.EnumTypeHandler.class),
            @Result(column = "birth_date", property = "birthDate"),
            @Result(column = "created_at", property = "createdAt"),
            @Result(column = "updated_at", property = "updatedAt")
    })
    List<Child> selectAllChildren();

    /**
     * 根据家长ID查询儿童列表
     */
    @Select("SELECT * FROM children WHERE parent_id = #{parentId} ORDER BY created_at DESC")
    @Results({
            @Result(column = "child_id", property = "childId"),
            @Result(column = "parent_id", property = "parentId"),
            @Result(column = "gender", property = "gender", typeHandler = org.example.handler.GenderEnumTypeHandler.class),
            @Result(column = "blood_type", property = "bloodType", typeHandler = org.apache.ibatis.type.EnumTypeHandler.class),
            @Result(column = "birth_date", property = "birthDate"),
            @Result(column = "created_at", property = "createdAt"),
            @Result(column = "updated_at", property = "updatedAt")
    })
    List<Child> selectChildrenByParentId(String parentId);

    /**
     * 根据ID查询儿童
     */
    @Select("SELECT * FROM children WHERE child_id = #{childId}")
    @Results({
            @Result(column = "child_id", property = "childId"),
            @Result(column = "parent_id", property = "parentId"),
            @Result(column = "gender", property = "gender", typeHandler = org.example.handler.GenderEnumTypeHandler.class),
            @Result(column = "blood_type", property = "bloodType", typeHandler = org.apache.ibatis.type.EnumTypeHandler.class),
            @Result(column = "birth_date", property = "birthDate"),
            @Result(column = "created_at", property = "createdAt"),
            @Result(column = "updated_at", property = "updatedAt")
    })
    Child selectChildById(Long childId);

    /**
     * 添加儿童信息
     */
    @Insert("""
        INSERT INTO children (parent_id, name, gender, birth_date, blood_type, created_at, updated_at) 
        VALUES (#{parentId}, #{name}, #{gender.code}, #{birthDate}, #{bloodType.code}, #{createdAt}, #{updatedAt})
        """)
    @Options(useGeneratedKeys = true, keyProperty = "childId")
    void insertChild(Child child);

    /**
     * 更新儿童信息
     */
    @Update("""
        UPDATE children SET 
        name = #{name}, 
        gender = #{gender.code}, 
        birth_date = #{birthDate}, 
        blood_type = #{bloodType.code}, 
        updated_at = #{updatedAt} 
        WHERE child_id = #{childId}
        """)
    void updateChild(Child child);

    /**
     * 删除儿童信息
     */
    @Delete("DELETE FROM children WHERE child_id = #{childId}")
    void deleteChild(Long childId);

    /**
     * 根据家长ID查询儿童列表
     */
    @Select("SELECT * FROM children WHERE parent_id = #{parentId}")
    List<Child> selectByParentId(@Param("parentId") String parentId);
}