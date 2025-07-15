package org.example.enums;

import lombok.Getter;

/**
 * 严重程度枚举类
 */
@Getter
public enum SeverityEnum {
    
    MILD("mild", "轻度"),
    MODERATE("moderate", "中度"),
    SEVERE("severe", "重度");
    
    private final String code;
    private final String desc;
    
    SeverityEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
} 