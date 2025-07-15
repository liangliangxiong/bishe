package org.example.enums;

import lombok.Getter;

/**
 * 性别枚举类
 */
@Getter
public enum GenderEnum {
    
    UNKNOWN(0, "未指定"),
    MALE(1, "男"),
    FEMALE(2, "女");
    
    private final Integer code;
    private final String desc;
    
    GenderEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
} 