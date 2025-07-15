package org.example.service.Impl;

import lombok.RequiredArgsConstructor;
import org.example.mapper.PermissionMapper;
import org.example.pojo.Permission;
import org.example.service.PermissionService;
import org.example.utils.ThreadLocalUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 权限服务实现类
 * 实现权限相关的业务逻辑
 */
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    
    private static final Logger log = LoggerFactory.getLogger(PermissionServiceImpl.class);
    
    private final PermissionMapper permissionMapper;
    
    @Override
    public List<Permission> getUserPermissions(String userId) {
        return permissionMapper.findByUserId(userId);
    }
    
    @Override
    public List<Permission> getCurrentUserPermissions() {
        // 从ThreadLocal中获取当前登录用户信息
        Map<String, Object> map = ThreadLocalUtil.get();
        String userId = (String) map.get("id");
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }
        log.info("获取当前用户权限，userId: {}", userId);
        return getUserPermissions(userId);
    }
    
    @Override
    public List<Permission> getAllPermissions() {
        return permissionMapper.findAll();
    }
    
    @Override
    @Transactional
    public void addPermission(Permission permission) {
        permissionMapper.insert(permission);
    }
    
    @Override
    @Transactional
    public void updatePermission(Permission permission) {
        permissionMapper.update(permission);
    }
    
    @Override
    @Transactional
    public void deletePermission(Integer id) {
        permissionMapper.deleteById(id);
    }
    
    /**
     * 检查当前用户是否有指定权限
     */
    public boolean hasPermission(String permissionKey) {
        Map<String, Object> map = ThreadLocalUtil.get();
        String userId = (String) map.get("id");
        if (userId == null) {
            log.warn("用户未登录");
            return false;
        }
        
        // 获取用户所有权限
        List<Permission> permissions = getUserPermissions(userId);
        return permissions.stream()
                .anyMatch(p -> permissionKey.equals(p.getPermissionKey()));
    }
    
    @Override
    public List<Permission> getCurrentUserMenuTree() {
        Map<String, Object> map = ThreadLocalUtil.get();
        String userId = (String) map.get("id");
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }
        return getUserMenuTree(userId);
    }

    @Override
    public List<Permission> getUserMenuTree(String userId) {
        // 获取用户所有菜单权限（只包含menu_url不为空的权限）
        List<Permission> allMenus = permissionMapper.findMenusByUserId(userId);
        log.info("用户 {} 获取到的菜单权限数量: {}", userId, allMenus.size());
        
        if (allMenus.isEmpty()) {
            log.warn("用户 {} 没有任何菜单权限", userId);
            return new ArrayList<>();
        }
        
        // 构建菜单树
        List<Permission> menuTree = new ArrayList<>();
        Map<Integer, Permission> permissionMap = new HashMap<>();
        
        // 先把所有权限放入 map 中，方便查找
        for (Permission permission : allMenus) {
            log.debug("处理权限: id={}, name={}, parentId={}", 
                    permission.getPermissionId(), 
                    permission.getPermissionName(), 
                    permission.getParentId());
            permissionMap.put(permission.getPermissionId(), permission);
            permission.setChildren(new ArrayList<>());
        }
        
        // 构建树形结构
        for (Permission permission : allMenus) {
            if (permission.getParentId() == null || permission.getParentId() == 0) {
                // 这是一个根节点
                log.debug("添加根节点: {}", permission.getPermissionName());
                menuTree.add(permission);
            } else {
                // 这是一个子节点，找到其父节点并添加到children中
                Permission parent = permissionMap.get(permission.getParentId());
                if (parent != null) {
                    log.debug("添加子节点: {} 到父节点: {}", 
                            permission.getPermissionName(), 
                            parent.getPermissionName());
                    parent.getChildren().add(permission);
                } else {
                    log.warn("找不到权限 {} 的父节点 {}", 
                            permission.getPermissionName(), 
                            permission.getParentId());
                }
            }
        }
        
        // 对菜单树进行排序（如果需要）
        sortMenuTree(menuTree);
        
        log.info("用户 {} 最终构建的菜单树节点数量: {}", userId, menuTree.size());
        return menuTree;
    }
    
    /**
     * 递归对菜单树进行排序
     */
    private void sortMenuTree(List<Permission> menuTree) {
        if (menuTree == null || menuTree.isEmpty()) {
            return;
        }
        
        // 根据 permissionId 排序当前层级
        menuTree.sort((a, b) -> {
            if (a.getPermissionId() == null) return -1;
            if (b.getPermissionId() == null) return 1;
            return a.getPermissionId().compareTo(b.getPermissionId());
        });
        
        // 递归排序子菜单
        for (Permission menu : menuTree) {
            if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
                sortMenuTree(menu.getChildren());
            }
        }
    }
} 