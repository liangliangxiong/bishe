package org.example.pojo;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.enums.SeverityEnum;

import java.time.LocalDateTime;
import java.time.LocalDate;

/**
 * 过敏史记录实体类
 * 用于记录儿童的过敏情况
 */
@Data
public class Allergy {
    // 定义验证组
    public interface Add {}
    public interface Update {}
    
    /** 过敏记录ID */
    private Long allergyId;
    
    /** 儿童ID */
    @NotNull(message = "儿童ID不能为空", groups = Add.class)
    private Long childId;
    
    /** 过敏原名称 */
    @NotEmpty(message = "过敏原名称不能为空", groups = {Add.class, Update.class})
    private String allergyName;
    
    /** 严重程度（mild-轻度, moderate-中度, severe-重度）*/
    @NotNull(message = "严重程度不能为空", groups = {Add.class, Update.class})
    private SeverityEnum severity;
    
    /** 诊断日期 */
    @NotNull(message = "诊断日期不能为空", groups = {Add.class, Update.class})
    private LocalDate diagnosisDate;
    
    /** 备注说明 */
    private String notes;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    private LocalDateTime updatedAt;
    
    /** 关联的儿童对象 */
    private Child child;
} 