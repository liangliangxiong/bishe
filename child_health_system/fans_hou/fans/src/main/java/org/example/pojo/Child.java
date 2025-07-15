package org.example.pojo;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;
import org.example.enums.BloodTypeEnum;
import org.example.enums.GenderEnum;

import java.time.LocalDateTime;
import java.time.LocalDate;

/**
 * 儿童信息实体类
 * 用于管理儿童的基本信息
 */
@Data
public class Child {
    /** 儿童ID */
    private Long childId;
    
    /** 家长用户ID */
    private String parentId;
    
    /** 儿童姓名 */
    @NotEmpty(message = "儿童姓名不能为空")
    private String name;
    
    /** 性别（0-未指定 1-男性 2-女性）*/
    @NotNull(message = "性别不能为空")
    private GenderEnum gender;
    
    /** 出生日期 */
    @NotNull(message = "出生日期不能为空")
    @Past(message = "出生日期必须是过去的日期")
    private LocalDate birthDate;
    
    /** 血型 */
    private BloodTypeEnum bloodType;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    private LocalDateTime updatedAt;

    /** 家长用户名 */
    private String parentUsername;
    
    /** 家长真实姓名 */
    private String parentName;
} 