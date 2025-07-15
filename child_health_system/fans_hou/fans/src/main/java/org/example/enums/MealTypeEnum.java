package org.example.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 餐次类型枚举类
 */
@Getter
public enum MealTypeEnum {
    
    BREAKFAST("breakfast", "早餐"),
    LUNCH("lunch", "午餐"),
    DINNER("dinner", "晚餐"),
    SNACK("snack", "零食");
    
    private final String code;
    private final String desc;
    
    MealTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonValue
    public String getCode() {
        return code;
    }
} 