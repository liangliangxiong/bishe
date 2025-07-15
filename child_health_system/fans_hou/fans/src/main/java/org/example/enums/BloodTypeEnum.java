package org.example.enums;

import lombok.Getter;

/**
 * 血型枚举类
 */
@Getter
public enum BloodTypeEnum {
    
    A("A", "A型"),
    B("B", "B型"),
    AB("AB", "AB型"),
    O("O", "O型");
    
    private final String code;
    private final String desc;
    
    BloodTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
} 