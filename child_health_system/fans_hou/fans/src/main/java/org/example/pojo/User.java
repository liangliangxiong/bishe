package org.example.pojo;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 用于管理系统用户信息
 */
@Data
public class User {
    /** 用户ID（UUID） */
    private String userId;
    
    /** 用户名 */
    @NotEmpty(message = "用户名不能为空")
    private String username;
    
    /** 密码 */
//    @NotEmpty(message = "密码不能为空")
    private String password;
    
    /** 角色ID */
    private Integer roleId;
    
    /** 真实姓名 */
    @NotEmpty(message = "真实姓名不能为空")
    private String realName;
    
    /** 手机号 */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    private LocalDateTime updatedAt;
    
    /** 关联的角色对象 */
    private Role role;
    
    /** 角色名称 */
    private String roleName;
}
