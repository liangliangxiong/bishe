package org.example.enums;

import lombok.Getter;

/**
 * 体温测量部位枚举类
 */
@Getter
public enum MeasurePositionEnum {
    
    MOUTH("mouth", "口腔"),
    ARMPIT("armpit", "腋下"),
    EAR("ear", "耳温");
    
    private final String code;
    private final String description;
    
    MeasurePositionEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
} 