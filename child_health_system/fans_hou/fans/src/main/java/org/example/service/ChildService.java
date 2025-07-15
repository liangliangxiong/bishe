package org.example.service;

import org.example.pojo.Child;
import java.util.List;
import java.util.Map;

public interface ChildService {
    /**
     * 获取儿童列表（分页）
     */
    Map<String, Object> getChildList(Integer page, Integer pageSize, String query);

    /**
     * 根据父母ID获取儿童列表
     */
    List<Child> getChildrenByParentId(String parentId);

    /**
     * 获取所有儿童列表
     * 用于管理员和医生查看所有儿童信息
     */
    List<Child> getAllChildren();

    /**
     * 获取儿童详情
     */
    Child getChildById(Long childId);

    /**
     * 添加儿童信息
     */
    void addChild(Child child);

    /**
     * 更新儿童信息
     */
    void updateChild(Child child);

    /**
     * 删除儿童信息
     */
    void deleteChild(Long childId);
}