package org.example.pojo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 角色-权限关联实体类
 * 用于管理角色和权限的多对多关系
 */
@Data
public class RolePermission {
    /** 角色ID */
    private Integer roleId;
    
    /** 权限ID */
    private Integer permissionId;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 关联的角色对象 */
    private Role role;
    
    /** 关联的权限对象 */
    private Permission permission;
} 