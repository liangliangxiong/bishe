package org.example.pojo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 权限实体类
 * 用于管理系统中的权限信息
 */
@Data
public class Permission {
    /** 权限ID */
    private Integer permissionId;
    
    /** 权限名称 */
    private String permissionName;
    
    /** 权限标识符 */
    private String permissionKey;
    
    /** 菜单URL */
    private String menuUrl;
    
    /** 父级权限ID */
    private Integer parentId;
    
    /** 图标 */
    private String icon;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    private LocalDateTime updatedAt;
    
    /** 子菜单列表 */
    private List<Permission> children;
} 