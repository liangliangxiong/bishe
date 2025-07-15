package org.example.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mapper.ChildMapper;
import org.example.mapper.UserMapper;
import org.example.pojo.Child;
import org.example.pojo.User;
import org.example.service.ChildService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChildServiceImpl implements ChildService {
    private final ChildMapper childMapper;
    private final UserMapper userMapper;

    @Override
    public Map<String, Object> getChildList(Integer page, Integer pageSize, String query) {
        // 计算偏移量
        int offset = (page - 1) * pageSize;

        // 查询列表和总数
        List<Child> children = childMapper.selectChildList(offset, pageSize, query);
        Integer total = childMapper.selectChildCount(query);

        // 封装返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("items", children);

        return result;
    }

    @Override
    public List<Child> getChildrenByParentId(String parentId) {
        if (parentId == null || parentId.trim().isEmpty()) {
            throw new RuntimeException("父母ID不能为空");
        }

        // 验证用户是否存在
        User parent = userMapper.findById(parentId);
        if (parent == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证用户角色
        if (parent.getRoleId() != 3) {
            throw new RuntimeException("非家长用户无法查看儿童列表");
        }

        return childMapper.selectChildrenByParentId(parentId);
    }

    @Override
    public List<Child> getAllChildren() {
        return childMapper.selectAllChildren();
    }

    @Override
    public Child getChildById(Long childId) {
        Child child = childMapper.selectChildById(childId);
        if (child == null) {
            throw new RuntimeException("儿童信息不存在");
        }
        return child;
    }

    @Override
    @Transactional
    public void addChild(Child child) {
        // 验证父母信息
        User parent = userMapper.findById(child.getParentId());
        if (parent == null) {
            throw new RuntimeException("父母信息不存在");
        }

        // 设置创建和更新时间
        LocalDateTime now = LocalDateTime.now();
        child.setCreatedAt(now);
        child.setUpdatedAt(now);

        childMapper.insertChild(child);
    }

    @Override
    @Transactional
    public void updateChild(Child child) {
        // 验证儿童信息是否存在
        Child existingChild = childMapper.selectChildById(child.getChildId());
        if (existingChild == null) {
            throw new RuntimeException("儿童信息不存在");
        }

        // 设置更新时间
        child.setUpdatedAt(LocalDateTime.now());

        childMapper.updateChild(child);
    }

    @Override
    @Transactional
    public void deleteChild(Long childId) {
        // 验证儿童信息是否存在
        Child child = childMapper.selectChildById(childId);
        if (child == null) {
            throw new RuntimeException("儿童信息不存在");
        }

        childMapper.deleteChild(childId);
    }
}