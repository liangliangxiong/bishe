package org.example.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.enums.MealTypeEnum;
import org.example.utils.MealTypeEnumDeserializer;

import java.time.LocalDateTime;

/**
 * 饮食记录实体类
 * 用于记录儿童的饮食情况
 */
@Data
public class DietRecord {
    // 定义验证组
    public interface Add {}
    public interface Update {}
    
    /** 记录ID */
    private Long recordId;
    
    /** 儿童ID */
    @NotNull(message = "儿童ID不能为空", groups = Add.class)  // 只在添加时验证
    private Long childId;
    
    /** 餐次类型 */
    @NotNull(message = "餐次类型不能为空", groups = {Add.class, Update.class})
    @JsonDeserialize(using = MealTypeEnumDeserializer.class)
    private MealTypeEnum mealType;
    
    /** 用餐时间 */
    @NotNull(message = "用餐时间不能为空", groups = {Add.class, Update.class})
    private LocalDateTime mealTime;
    
    /** 食物项目 */
    private String foodItems;
    
    /** 食物数量/份量 */
    private String amount;
    
    /** 备注信息 */
    private String notes;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    private LocalDateTime updatedAt;
    
    /** 关联的儿童信息 */
    private Child child;
} 