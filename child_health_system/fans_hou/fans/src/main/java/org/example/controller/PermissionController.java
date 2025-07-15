package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pojo.Permission;
import org.example.pojo.Result;
import org.example.service.PermissionService;
import org.example.utils.ThreadLocalUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 权限管理控制器
 * 处理权限相关的请求，包括权限的增删改查
 */
@Slf4j
@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
public class PermissionController {
    
    private final PermissionService permissionService;
    
    /**
     * 获取所有权限列表
     * @return 权限列表
     */
    @GetMapping("/list")
    public Result<List<Permission>> list() {
        log.info("开始获取权限列表");
        List<Permission> permissions = permissionService.getAllPermissions();
        log.info("获取到的权限列表: {}", permissions);
        return Result.success(permissions);
    }
    
    /**
     * 获取当前登录用户的权限列表
     */
    @GetMapping("/user")
    public Result<List<Permission>> getUserPermissions() {
        try {
            List<Permission> permissions = permissionService.getCurrentUserPermissions();
            log.info("获取到的用户权限列表: {}", permissions);
            return Result.success(permissions);
        } catch (Exception e) {
            log.error("获取用户权限失败", e);
            return Result.error("获取权限失败：" + e.getMessage());
        }
    }
    
    /**
     * 添加新权限
     */
    @PostMapping
    public Result<Void> add(@RequestBody Permission permission) {
        permissionService.addPermission(permission);
        return Result.success();
    }
    
    /**
     * 更新权限信息
     */
    @PutMapping
    public Result<Void> update(@RequestBody Permission permission) {
        permissionService.updatePermission(permission);
        return Result.success();
    }
    
    /**
     * 删除权限
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        permissionService.deletePermission(id);
        return Result.success();
    }
    
    /**
     * 获取当前用户的导航菜单树
     * @return 菜单树
     */
    @GetMapping("/menu/tree")
    public Result<List<Permission>> getMenuTree() {
        try {
            List<Permission> menuTree = permissionService.getCurrentUserMenuTree();
            log.info("获取到的菜单树: {}", menuTree);
            return Result.success(menuTree);
        } catch (Exception e) {
            log.error("获取菜单树失败", e);
            return Result.error("获取菜单失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取指定用户的导航菜单树
     * @param userId 用户ID
     * @return 菜单树
     */
    @GetMapping("/menu/tree/{userId}")
    public Result<List<Permission>> getUserMenuTree(@PathVariable String userId) {
        try {
            List<Permission> menuTree = permissionService.getUserMenuTree(userId);
            log.info("获取到用户 {} 的菜单树: {}", userId, menuTree);
            return Result.success(menuTree);
        } catch (Exception e) {
            log.error("获取用户菜单树失败", e);
            return Result.error("获取菜单失败：" + e.getMessage());
        }
    }
} 