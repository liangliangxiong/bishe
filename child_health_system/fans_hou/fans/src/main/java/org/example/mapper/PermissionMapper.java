package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.pojo.Permission;
import java.util.List;

/**
 * 权限数据访问接口
 * 处理权限相关的数据库操作
 */
@Mapper
public interface PermissionMapper {
    /**
     * 查询所有权限记录
     * @return 权限列表
     */
    @Select("SELECT * FROM permissions")
    List<Permission> findAll();
    
    /**
     * 根据角色ID查询对应的权限
     * @param roleId 角色ID
     * @return 该角色的权限列表
     */
    @Select("SELECT p.* FROM permissions p " +
            "INNER JOIN role_permissions rp ON p.permission_id = rp.permission_id " +
            "WHERE rp.role_id = #{roleId} " +
            "ORDER BY p.parent_id, p.permission_id")
    List<Permission> findByRoleId(Integer roleId);
    
    /**
     * 查询用户所有菜单权限（用于构建导航菜单）
     * 注意：需要按照parent_id和permission_id排序，以便正确构建菜单树
     */
    @Select("WITH RECURSIVE menu_tree AS (" +
            "  SELECT p.* " +
            "  FROM permissions p " +
            "  INNER JOIN role_permissions rp ON p.permission_id = rp.permission_id " +
            "  INNER JOIN user u ON u.role_id = rp.role_id " +
            "  WHERE u.user_id = #{userId} " +
            "  UNION " +
            "  SELECT p.* " +
            "  FROM permissions p " +
            "  INNER JOIN menu_tree mt ON p.permission_id = mt.parent_id" +
            ") " +
            "SELECT DISTINCT * FROM menu_tree " +
            "ORDER BY CASE WHEN parent_id IS NULL THEN 0 ELSE 1 END, parent_id, permission_id")
    @Results({
        @Result(column = "permission_id", property = "permissionId"),
        @Result(column = "permission_name", property = "permissionName"),
        @Result(column = "permission_key", property = "permissionKey"),
        @Result(column = "menu_url", property = "menuUrl"),
        @Result(column = "parent_id", property = "parentId"),
        @Result(column = "icon", property = "icon"),
        @Result(column = "created_at", property = "createdAt"),
        @Result(column = "updated_at", property = "updatedAt")
    })
    List<Permission> findMenusByUserId(String userId);
    
    /**
     * 查询用户所有权限
     */
    @Select("SELECT DISTINCT p.* FROM permissions p " +
            "INNER JOIN role_permissions rp ON p.permission_id = rp.permission_id " +
            "INNER JOIN user u ON u.role_id = rp.role_id " +
            "WHERE u.user_id = #{userId} " +
            "ORDER BY p.permission_id")
    @Results({
        @Result(column = "permission_id", property = "permissionId"),
        @Result(column = "permission_name", property = "permissionName"),
        @Result(column = "permission_key", property = "permissionKey"),
        @Result(column = "menu_url", property = "menuUrl"),
        @Result(column = "parent_id", property = "parentId"),
        @Result(column = "icon", property = "icon"),
        @Result(column = "created_at", property = "createdAt"),
        @Result(column = "updated_at", property = "updatedAt")
    })
    List<Permission> findByUserId(String userId);
    
    /**
     * 添加新权限
     * @param permission 权限信息
     */
    @Insert("INSERT INTO permissions(permission_name, permission_key, menu_url, parent_id, icon) " +
            "VALUES(#{permissionName}, #{permissionKey}, #{menuUrl}, #{parentId}, #{icon})")
    void insert(Permission permission);
    
    /**
     * 更新权限信息
     * @param permission 更新的权限信息
     */
    @Update("UPDATE permissions SET " +
            "permission_name=#{permissionName}, " +
            "permission_key=#{permissionKey}, " +
            "menu_url=#{menuUrl}, " +
            "parent_id=#{parentId}, " +
            "icon=#{icon} " +
            "WHERE permission_id=#{permissionId}")
    void update(Permission permission);
    
    /**
     * 删除权限
     * @param id 要删除的权限ID
     */
    @Delete("DELETE FROM permissions WHERE permission_id=#{id}")
    void deleteById(Integer id);
    
    /**
     * 检查用户是否有指定权限
     */
    @Select("SELECT COUNT(*) FROM permissions p " +
            "INNER JOIN role_permissions rp ON p.permission_id = rp.permission_id " +
            "INNER JOIN user u ON u.role_id = rp.role_id " +
            "WHERE u.user_id = #{userId} AND p.permission_key = #{permissionKey}")
    int checkUserPermission(@Param("userId") String userId, @Param("permissionKey") String permissionKey);

} 