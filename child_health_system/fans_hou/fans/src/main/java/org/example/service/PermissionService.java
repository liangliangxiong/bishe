package org.example.service;

import org.example.pojo.Permission;
import java.util.List;

/**
 * 权限服务接口
 * 定义权限相关的业务操作
 */
public interface PermissionService {
    /**
     * 获取指定用户的所有权限
     * @param userId 用户ID
     * @return 权限列表
     */
    List<Permission> getUserPermissions(String userId);
    
    /**
     * 获取当前登录用户的所有权限
     * @return 权限列表
     */
    List<Permission> getCurrentUserPermissions();
    
    /**
     * 获取系统中所有权限
     * @return 所有权限列表
     */
    List<Permission> getAllPermissions();
    
    /**
     * 添加新权限
     * @param permission 权限信息
     */
    void addPermission(Permission permission);
    
    /**
     * 更新权限信息
     * @param permission 更新的权限信息
     */
    void updatePermission(Permission permission);
    
    /**
     * 删除权限
     * @param id 要删除的权限ID
     */
    void deletePermission(Integer id);


    List<Permission> getCurrentUserMenuTree();

    List<Permission> getUserMenuTree(String userId);
}