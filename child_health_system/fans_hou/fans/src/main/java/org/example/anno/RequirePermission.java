package org.example.anno;

import java.lang.annotation.*;

/**
 * 权限控制注解
 * 用于标记需要进行权限验证的方法
 * 支持指定单个权限或多个权限
 * 多个权限时，用户拥有其中任意一个权限即可访问
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {
    /**
     * 需要的权限标识
     * 支持指定多个权限，用户拥有其中任意一个权限即可
     */
    String[] value() default {};

    /**
     * 权限验证模式
     * ANY: 满足任意一个权限即可（默认）
     * ALL: 必须满足所有指定的权限
     */
    Mode mode() default Mode.ANY;

    /**
     * 权限验证模式枚举
     */
    enum Mode {
        /** 满足任意一个权限即可 */
        ANY,
        /** 必须满足所有指定的权限 */
        ALL
    }
} 