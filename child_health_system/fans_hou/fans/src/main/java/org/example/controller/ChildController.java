package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pojo.Child;
import org.example.pojo.Result;
import org.example.service.ChildService;
import org.example.utils.ThreadLocalUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 儿童信息管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/children")
@RequiredArgsConstructor
public class ChildController {
    private final ChildService childService;

    /**
     * 获取儿童列表（分页）
     * 管理员和医生可以查看所有儿童信息
     */
    @GetMapping
    public Result<Map<String, Object>> getChildList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String query) {
        log.info("获取儿童列表: page={}, pageSize={}, query={}", page, pageSize, query);
        
        // 检查用户角色
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer roleId = (Integer) map.get("roleId");
        
        // 只允许管理员和医生访问
        if (roleId != 1 && roleId != 2) {
            return Result.error("无权访问此接口");
        }
        
        return Result.success(childService.getChildList(page, pageSize, query));
    }

    /**
     * 获取当前用户的儿童列表
     * 家长只能查看自己的儿童信息
     */
    @GetMapping("/my")
    public Result<List<Child>> getMyChildren() {
        Map<String, Object> map = ThreadLocalUtil.get();
        log.info("ThreadLocal中的用户信息: {}", map);

        if (map == null) {
            log.error("用户未登录，ThreadLocal中无用户信息");
            return Result.error("请先登录");
        }

        String userId = (String) map.get("id");
        Integer roleId = (Integer) map.get("roleId");
        log.info("用户[{}]角色[{}]获取儿童列表", userId, roleId);

        if (userId == null) {
            log.error("ThreadLocal中未找到用户ID");
            return Result.error("无法获取用户ID");
        }

        try {
            List<Child> children;
            if (roleId == 1 || roleId == 2) {
                // 管理员和医生可以查看所有儿童
                children = childService.getAllChildren();
            } else {
                // 家长只能查看自己的儿童
                children = childService.getChildrenByParentId(userId);
            }
            log.info("成功获取到{}个儿童信息", children.size());
            return Result.success(children);
        } catch (Exception e) {
            log.error("获取儿童列表失败: ", e);
            return Result.error("获取儿童列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取儿童详情
     * 管理员和医生可以查看任意儿童信息
     * 家长只能查看自己的儿童信息
     */
    @GetMapping("/{childId}")
    public Result<Child> getChildDetail(@PathVariable Long childId) {
        log.info("获取儿童详情: childId={}", childId);

        try {
            Map<String, Object> map = ThreadLocalUtil.get();
            String userId = (String) map.get("id");
            Integer roleId = (Integer) map.get("roleId");

            Child child = childService.getChildById(childId);
            
            // 检查权限
            if (roleId == 3 && !child.getParentId().equals(userId)) {
                return Result.error("无权查看其他家长的儿童信息");
            }
            
            return Result.success(child);
        } catch (Exception e) {
            log.error("获取儿童详情失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 添加儿童信息
     * 管理员可以添加任意儿童信息
     * 家长只能添加自己的儿童信息
     * 医生不能添加儿童信息
     */
    @PostMapping
    public Result<Void> addChild(@RequestBody @Valid Child child) {
        log.info("添加儿童信息: {}", child);

        try {
            Map<String, Object> map = ThreadLocalUtil.get();
            String userId = (String) map.get("id");
            Integer roleId = (Integer) map.get("roleId");

            // 检查权限
            if (roleId == 2) {
                return Result.error("医生不能添加儿童信息");
            }

            // 如果是家长，只能添加自己的儿童
            if (roleId == 3) {
                child.setParentId(userId);
            }
            // 如果是管理员，需要指定家长ID
            else if (roleId == 1 && child.getParentId() == null) {
                return Result.error("请指定儿童的家长");
            }

            childService.addChild(child);
            return Result.success();
        } catch (Exception e) {
            log.error("添加儿童信息失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新儿童信息
     * 管理员可以修改任意儿童信息
     * 家长只能修改自己的儿童信息
     * 医生不能修改儿童信息
     */
    @PutMapping("/{childId}")
    public Result<Void> updateChild(@PathVariable Long childId, @RequestBody @Valid Child child) {
        log.info("更新儿童信息: childId={}, child={}", childId, child);

        try {
            Map<String, Object> map = ThreadLocalUtil.get();
            String userId = (String) map.get("id");
            Integer roleId = (Integer) map.get("roleId");

            // 检查权限
            if (roleId == 2) {
                return Result.error("医生不能修改儿童信息");
            }

            Child existingChild = childService.getChildById(childId);
            if (roleId == 3 && !existingChild.getParentId().equals(userId)) {
                return Result.error("无权修改其他家长的儿童信息");
            }

            child.setChildId(childId);
            // 不允许修改父母ID
            child.setParentId(existingChild.getParentId());
            
            childService.updateChild(child);
            return Result.success();
        } catch (Exception e) {
            log.error("更新儿童信息失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除儿童信息
     * 管理员可以删除任意儿童信息
     * 家长只能删除自己的儿童信息
     * 医生不能删除儿童信息
     */
    @DeleteMapping("/{childId}")
    public Result<Void> deleteChild(@PathVariable Long childId) {
        log.info("删除儿童信息: childId={}", childId);

        try {
            Map<String, Object> map = ThreadLocalUtil.get();
            String userId = (String) map.get("id");
            Integer roleId = (Integer) map.get("roleId");

            // 检查权限
            if (roleId == 2) {
                return Result.error("医生不能删除儿童信息");
            }

            Child child = childService.getChildById(childId);
            if (roleId == 3 && !child.getParentId().equals(userId)) {
                return Result.error("无权删除其他家长的儿童信息");
            }

            childService.deleteChild(childId);
            return Result.success();
        } catch (Exception e) {
            log.error("删除儿童信息失败", e);
            return Result.error(e.getMessage());
        }
    }
}