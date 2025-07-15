package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.anno.RequirePermission;
import org.example.pojo.Result;
import org.example.pojo.User;
import org.example.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理员用户管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    /**
     * 获取用户列表
     * @param page 页码
     * @param pageSize 每页数量
     * @param query 搜索关键词
     */
    @GetMapping
    @RequirePermission("user:manage")
    public Result<Map<String, Object>> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String query) {
        log.info("获取用户列表: page={}, pageSize={}, query={}", page, pageSize, query);
        return Result.success(userService.getUserList(page, pageSize, query));
    }

    /**
     * 添加用户
     */
    @PostMapping
    @RequirePermission("user:manage")
    public Result<Void> addUser(@RequestBody @Validated User user) {
        log.info("添加用户: {}", user);
        userService.addUser(user);
        return Result.success();
    }

    /**
     * 更新用户
     */
    @PutMapping("/{userId}")
    @RequirePermission("user:manage")
    public Result<Void> updateUser(@PathVariable String userId, @RequestBody @Validated User user) {
        log.info("更新用户: id={}, user={}", userId, user);
        user.setUserId(userId);
        userService.updateUserByAdmin(user);
        return Result.success();
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{userId}")
    @RequirePermission("user:manage")
    public Result<Void> deleteUser(@PathVariable String userId) {
        log.info("删除用户: id={}", userId);
        userService.deleteUser(userId);
        return Result.success();
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/{userId}")
    @RequirePermission("user:manage")
    public Result<User> getUserDetail(@PathVariable String userId) {
        log.info("获取用户详情: id={}", userId);
        return Result.success(userService.findById(userId));
    }

    /**
     * 重置用户密码
     */
    @PostMapping("/{userId}/reset-password")
    @RequirePermission("user:manage")
    public Result<Void> resetPassword(@PathVariable String userId) {
        log.info("重置用户密码: id={}", userId);
        userService.resetPassword(userId);
        return Result.success();
    }
} 