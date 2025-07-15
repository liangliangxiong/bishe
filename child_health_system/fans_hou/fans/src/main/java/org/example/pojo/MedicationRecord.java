package org.example.pojo;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;

/**
 * 用药记录实体类
 * 用于记录儿童的用药情况
 */
@Data
public class MedicationRecord {
    // 定义验证组
    public interface Add {}
    public interface Update {}
    
    /** 记录ID */
    private Long recordId;
    
    /** 关联的就医记录ID */
    @NotNull(message = "就医记录ID不能为空", groups = Add.class)
    private Long medicalRecordId;
    
    /** 药品名称 */
    @NotEmpty(message = "药品名称不能为空", groups = {Add.class, Update.class})
    private String medicineName;
    
    /** 用药剂量 */
    @NotEmpty(message = "用药剂量不能为空", groups = {Add.class, Update.class})
    private String dosage;
    
    /** 用药频率 */
    @NotEmpty(message = "用药频率不能为空", groups = {Add.class, Update.class})
    private String frequency;
    
    /** 开始用药日期 */
    @NotNull(message = "开始用药日期不能为空", groups = {Add.class, Update.class})
    private LocalDate startDate;
    
    /** 结束用药日期 */
    @NotNull(message = "结束用药日期不能为空", groups = {Add.class, Update.class})
    private LocalDate endDate;
    
    /** 备注说明 */
    private String notes;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    private LocalDateTime updatedAt;
    
    /** 关联的就医记录对象 */
    private MedicalRecord medicalRecord;
} 