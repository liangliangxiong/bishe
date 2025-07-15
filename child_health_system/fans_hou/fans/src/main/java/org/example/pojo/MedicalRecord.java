package org.example.pojo;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;

/**
 * 就医记录实体类
 * 用于记录儿童的就医情况
 */
@Data
public class MedicalRecord {
    // 定义验证组
    public interface Add {}
    public interface Update {}
    
    /** 记录ID */
    private Long recordId;
    
    /** 儿童ID */
    @NotNull(message = "儿童ID不能为空", groups = Add.class)
    private Long childId;
    
    /** 就诊医生ID */
    @NotEmpty(message = "就诊医生ID不能为空", groups = {Add.class, Update.class})
    private String doctorId;
    
    /** 就诊日期 */
    @NotNull(message = "就诊日期不能为空", groups = {Add.class, Update.class})
    private LocalDate visitDate;
    
    /** 诊断结果 */
    @NotEmpty(message = "诊断结果不能为空", groups = {Add.class, Update.class})
    private String diagnosis;
    
    /** 治疗方案 */
    private String treatment;
    
    /** 医院名称 */
    @NotEmpty(message = "医院名称不能为空", groups = {Add.class, Update.class})
    private String hospitalName;
    
    /** 就诊科室 */
    @NotEmpty(message = "就诊科室不能为空", groups = {Add.class, Update.class})
    private String department;
    
    /** 备注说明 */
    private String notes;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    private LocalDateTime updatedAt;
    
    /** 关联的儿童对象 */
    private Child child;
    
    /** 关联的医生对象 */
    private User doctor;
} 