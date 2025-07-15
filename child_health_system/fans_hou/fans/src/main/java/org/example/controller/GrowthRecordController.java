package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pojo.GrowthRecord;
import org.example.pojo.Result;
import org.example.service.GrowthRecordService;
import org.example.utils.ThreadLocalUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 生长记录管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/growth-records")
@RequiredArgsConstructor
public class GrowthRecordController {
    private final GrowthRecordService growthRecordService;

    /**
     * 获取生长记录列表（分页）
     * 管理员和医生可以查看所有记录
     */
    @GetMapping
    public Result<Map<String, Object>> getGrowthRecordList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String query) {
        log.info("获取生长记录列表: page={}, pageSize={}, query={}", page, pageSize, query);
        
        // 检查用户角色
        Map<String, Object> userInfo = ThreadLocalUtil.get();
        Integer roleId = (Integer) userInfo.get("roleId");
        
        // 只允许管理员和医生访问
        if (roleId != 1 && roleId != 2) {
            return Result.error("无权访问此接口");
        }
        
        // 直接返回service层的结果
        return Result.success(growthRecordService.getGrowthRecordList(page, pageSize, query));
    }

    /**
     * 根据儿童ID获取生长记录列表
     */
    @GetMapping("/child/{childId}")
    public Result<List<Map<String, Object>>> getGrowthRecordsByChildId(@PathVariable Long childId) {
        log.info("获取儿童生长记录: childId={}", childId);
        
        try {
            List<Map<String, Object>> records = growthRecordService.getGrowthRecordsByChildId(childId);
            return Result.success(records);
        } catch (Exception e) {
            log.error("获取儿童生长记录失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取生长记录详情
     */
    @GetMapping("/{recordId}")
    public Result<Map<String, Object>> getGrowthRecordDetail(@PathVariable Long recordId) {
        log.info("获取生长记录详情: recordId={}", recordId);
        
        try {
            Map<String, Object> record = growthRecordService.getGrowthRecordById(recordId);
            return Result.success(record);
        } catch (Exception e) {
            log.error("获取生长记录详情失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 添加生长记录
     */
    @PostMapping
    public Result<Void> addGrowthRecord(@RequestBody @Valid GrowthRecord record) {
        log.info("添加生长记录: {}", record);
        
        try {
            growthRecordService.addGrowthRecord(record);
            return Result.success();
        } catch (Exception e) {
            log.error("添加生长记录失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新生长记录
     */
    @PutMapping("/{recordId}")
    public Result<Void> updateGrowthRecord(@PathVariable Long recordId, @RequestBody @Valid GrowthRecord record) {
        log.info("更新生长记录: recordId={}, record={}", recordId, record);
        
        try {
            record.setRecordId(recordId);  // 确保设置recordId
            growthRecordService.updateGrowthRecord(record);
            return Result.success();
        } catch (Exception e) {
            log.error("更新生长记录失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除生长记录
     */
    @DeleteMapping("/{recordId}")
    public Result<Void> deleteGrowthRecord(@PathVariable Long recordId) {
        log.info("删除生长记录: recordId={}", recordId);
        
        try {
            growthRecordService.deleteGrowthRecord(recordId);
            return Result.success();
        } catch (Exception e) {
            log.error("删除生长记录失败", e);
            return Result.error(e.getMessage());
        }
    }
} 