package org.example.pojo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 角色实体类
 * 用于管理系统中的角色信息
 */
@Data
public class Role {
    /** 角色ID */
    private Integer roleId;
    
    /** 角色名称 */
    private String roleName;
    
    /** 角色描述 */
    private String roleDesc;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    private LocalDateTime updatedAt;
} 