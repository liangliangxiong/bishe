package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.anno.RequirePermission;
import org.example.pojo.Result;
import org.example.pojo.User;
import org.example.service.UserService;
import org.example.utils.ThreadLocalUtil;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

/**
 * 个人信息管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    /**
     * 获取当前用户个人信息（包含角色信息）
     */
    @GetMapping
    public Result<Map<String, Object>> getCurrentUserProfile() {
        Map<String, Object> map = ThreadLocalUtil.get();
        String userId = (String) map.get("id");
        log.info("获取用户信息，userId: {}", userId);
        
        User user = userService.findById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("userId", user.getUserId());
        result.put("username", user.getUsername());
        result.put("realName", user.getRealName());
        result.put("phone", user.getPhone());
        result.put("roleId", user.getRoleId());
        result.put("roleName", user.getRoleName());
        result.put("createdAt", user.getCreatedAt());
        result.put("updatedAt", user.getUpdatedAt());
        
        return Result.success(result);
    }

    /**
     * 更新个人信息
     */
    @PutMapping
    public Result<Void> updateProfile(@RequestBody User user) {
        Map<String, Object> map = ThreadLocalUtil.get();
        String userId = (String) map.get("id");
        log.info("更新用户信息，userId: {}", userId);
        
        user.setUserId(userId);
        try {
            userService.updateProfile(user);
            return Result.success();
        } catch (Exception e) {
            log.error("更新用户信息失败", e);
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestBody Map<String, String> params) {
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        String confirmPassword = params.get("confirmPassword");
        
        // 参数校验
        if (oldPassword == null || newPassword == null || confirmPassword == null) {
            return Result.error("密码参数不完整");
        }
        
        // 验证新密码与确认密码是否一致
        if (!newPassword.equals(confirmPassword)) {
            return Result.error("新密码与确认密码不一致");
        }

        Map<String, Object> map = ThreadLocalUtil.get();
        String userId = (String) map.get("id");
        log.info("修改密码，userId: {}", userId);
        
        try {
            userService.updatePassword(userId, oldPassword, newPassword);
            return Result.success();
        } catch (Exception e) {
            log.error("修改密码失败", e);
            return Result.error("修改失败：" + e.getMessage());
        }
    }
} 