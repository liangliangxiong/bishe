package org.example.pojo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import lombok.Data;
import org.example.enums.MeasurePositionEnum;

import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * 体温记录实体类
 * 用于记录儿童的体温测量数据
 */
@Data
public class TemperatureRecord {
    /** 记录ID */
    private Long recordId;
    
    /** 儿童ID */
    @NotNull(message = "儿童ID不能为空", groups = {Add.class})
    private Long childId;
    
    /** 体温值(℃) */
    @NotNull(message = "体温不能为空")
    @DecimalMin(value = "35.0", message = "体温不能低于35度")
    @DecimalMax(value = "42.0", message = "体温不能高于42度")
    private BigDecimal temperature;
    
    /** 测量时间 */
    @NotNull(message = "测量时间不能为空")
    private LocalDateTime measureTime;
    
    /** 测量部位（mouth-口腔, armpit-腋下, ear-耳温）*/
    @NotNull(message = "测量部位不能为空")
    private MeasurePositionEnum measurePosition;
    
    /** 备注说明 */
    private String notes;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    private LocalDateTime updatedAt;
    
    /** 关联的儿童对象 */
    private Child child;

    // 添加验证分组
    public interface Add {}
} 