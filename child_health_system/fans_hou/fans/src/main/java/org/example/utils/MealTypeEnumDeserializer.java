package org.example.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.example.enums.MealTypeEnum;

import java.io.IOException;

public class MealTypeEnumDeserializer extends JsonDeserializer<MealTypeEnum> {
    @Override
    public MealTypeEnum deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        
        // 尝试通过code匹配
        for (MealTypeEnum type : MealTypeEnum.values()) {
            if (type.getCode().equalsIgnoreCase(value)) {
                return type;
            }
        }
        
        // 尝试通过枚举名称匹配
        try {
            return MealTypeEnum.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid meal type: " + value + 
                ". Accepted values are: breakfast, lunch, dinner, snack (or BREAKFAST, LUNCH, DINNER, SNACK)");
        }
    }
} 