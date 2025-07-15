package org.example.pojo;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;

/**
 * 疫苗接种记录实体类
 * 用于记录儿童的疫苗接种情况
 */
@Data
public class Vaccination {
    // 定义验证组
    public interface Add {}
    public interface Update {}
    
    /** 接种记录ID */
    private Long vaccinationId;
    
    /** 儿童ID */
    @NotNull(message = "儿童ID不能为空", groups = Add.class)
    private Long childId;
    
    /** 疫苗名称 */
    @NotEmpty(message = "疫苗名称不能为空", groups = {Add.class, Update.class})
    private String vaccineName;
    
    /** 接种日期 */
    @NotNull(message = "接种日期不能为空", groups = {Add.class, Update.class})
    private LocalDate vaccinationDate;
    
    /** 下次接种日期 */
    private LocalDate nextDueDate;
    
    /** 接种医生ID */
    @NotEmpty(message = "接种医生ID不能为空", groups = {Add.class, Update.class})
    private String doctorId;
    
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