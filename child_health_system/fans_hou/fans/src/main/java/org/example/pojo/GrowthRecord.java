package org.example.pojo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.math.BigDecimal;

/**
 * 生长发育记录实体类
 * 用于记录儿童的身高、体重等生长发育数据
 */
@Data
public class GrowthRecord {
    /** 记录ID */
    private Long recordId;
    
    /** 儿童ID */
    @NotNull(message = "儿童ID不能为空")
    private Long childId;
    
    /** 身高(cm) */
    @NotNull(message = "身高不能为空")
    @Positive(message = "身高必须大于0")
    private BigDecimal height;
    
    /** 体重(kg) */
    @NotNull(message = "体重不能为空")
    @Positive(message = "体重必须大于0")
    private BigDecimal weight;
    
    /** BMI指数 */
    private BigDecimal bmi;
    
    /** 测量日期 */
    @NotNull(message = "测量日期不能为空")
    private LocalDate measureDate;
    
    /** 记录人ID */
    private String recordedBy;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    private LocalDateTime updatedAt;
    
    /** 关联的儿童对象 */
    private Child child;
    
    /** 关联的记录人对象 */
    private User recorder;
} 