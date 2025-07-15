package org.example.service;

import org.example.pojo.User;

import java.util.Map;

public interface UserService {
    //根据用户名查询用户
    User findByUserName(String username);
    //注册
    void register(String username, String password);

    //更新用户信息
    void update(User user);

    //更新用户头像
    void updateAvatar(String avatarUrl);

    //修改密码
    void updatePwd(String newPwd, Integer id);

    /**
     * 根据ID查询用户
     */
    User findById(String userId);

    /**
     * 更新用户个人信息
     */
    void updateProfile(User user);

    /**
     * 修改密码
     */
    void updatePassword(String userId, String oldPassword, String newPassword);

    // 新增管理员用户管理方法
    /**
     * 获取用户列表
     * @param page 页码
     * @param pageSize 每页数量
     * @param query 搜索关键词
     * @return 用户列表和总数
     */
    Map<String, Object> getUserList(Integer page, Integer pageSize, String query);

    /**
     * 添加用户
     * @param user 用户信息
     */
    void addUser(User user);

    /**
     * 管理员更新用户信息
     * @param user 用户信息
     */
    void updateUserByAdmin(User user);

    /**
     * 删除用户
     * @param userId 用户ID
     */
    void deleteUser(String userId);

    /**
     * 重置用户密码
     * @param userId 用户ID
     */
    void resetPassword(String userId);
}
