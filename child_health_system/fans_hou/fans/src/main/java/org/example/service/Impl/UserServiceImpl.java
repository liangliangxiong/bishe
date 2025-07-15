package org.example.service.Impl;

import lombok.RequiredArgsConstructor;
import org.example.mapper.UserMapper;
import org.example.pojo.User;
import org.example.service.UserService;
import org.example.utils.Md5Util;
import org.example.utils.ThreadLocalUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private static final String DEFAULT_PASSWORD = "123456"; // 默认密码

    //根据用户名查询用户
    @Override
    public User findByUserName(String username) {
        User u = userMapper.findByUserName(username);
        return u;
    }

    //注册
    @Override
    public void register(String username, String password) {
        // 清理用户名中的空白字符
        username = username.trim();
        //加密
        String md5String = Md5Util.getMD5String(password);
        //添加
        userMapper.add(username, md5String);
    }

    //更新用户信息
    @Override
    public void update(User user) {
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.update(user);
    }

    //更新头像
    @Override
    public void updateAvatar(String avatarUrl) {
        //获取当前用户, 从ThreadLocal中获取
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        userMapper.updateAvatar(avatarUrl, userId);
    }

    //更新密码
    @Override
    public void updatePwd(String newPwd, Integer id) {//直接从前面获得id的值
        String md5String = Md5Util.getMD5String(newPwd);
        System.out.println(id);
        userMapper.updatePwd(md5String, id);
    }

    @Override
    public User findById(String userId) {
        return userMapper.findById(userId);
    }

    @Override
    public void updateProfile(User user) {
        // 只允许更新部分字段
        User updateUser = new User();
        updateUser.setUserId(user.getUserId());
        updateUser.setRealName(user.getRealName());
        updateUser.setPhone(user.getPhone());
        
        userMapper.updateProfile(updateUser);
    }

    @Override
    public void updatePassword(String userId, String oldPassword, String newPassword) {
        // 验证旧密码
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证原密码是否正确
        if (!Md5Util.checkPassword(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }

        // 使用Md5加密新密码
        String encodedPassword = Md5Util.getMD5String(newPassword);
        userMapper.updatePassword(userId, encodedPassword);
    }

    @Override
    public Map<String, Object> getUserList(Integer page, Integer pageSize, String query) {
        // 计算偏移量
        int offset = (page - 1) * pageSize;
        
        // 查询用户列表
        List<User> users = userMapper.selectUserList(offset, pageSize, query);
        // 查询总记录数
        Integer total = userMapper.selectUserCount(query);
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("items", users);
        
        return result;
    }

    @Override
    @Transactional
    public void addUser(User user) {
        // 检查必要参数
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new RuntimeException("密码不能为空");
        }
        
        // 生成UUID作为用户ID
        user.setUserId(UUID.randomUUID().toString());
        
        // 加密密码
        String encryptedPassword = Md5Util.getMD5String(user.getPassword());
        user.setPassword(encryptedPassword);
        
        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        
        // 如果没有设置角色ID，默认设置为普通用户角色(3)
        if (user.getRoleId() == null) {
            user.setRoleId(3);
        }
        
        // 插入用户
        userMapper.insertUser(user);
    }

    @Override
    @Transactional
    public void updateUserByAdmin(User user) {
        // 更新用户信息，但不更新密码
        userMapper.updateUserByAdmin(user);
    }

    @Override
    @Transactional
    public void deleteUser(String userId) {
        // 检查是否为超级管理员
        User user = userMapper.findById(userId);
        if (user != null && user.getRoleId() == 1) {
            throw new RuntimeException("不能删除超级管理员");
        }
        
        // 删除用户
        userMapper.deleteUser(userId);
    }

    @Override
    @Transactional
    public void resetPassword(String userId) {
        // 重置为默认密码
        String encryptedPassword = Md5Util.getMD5String(DEFAULT_PASSWORD);
        userMapper.updatePassword(userId, encryptedPassword);
    }
}
