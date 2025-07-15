package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.pojo.User;

import java.util.List;

@Mapper
public interface UserMapper {

    //根据用户名查询用户
    @Select("select * from user where username=#{username}")
    User findByUserName(String username);

    //注册
    @Insert("insert into user(username,password,role_id,created_at,updated_at)" +
            " values(#{username},#{password},3,now(),now())")
    void add(String username, String password);

    //修改用户信息
    @Update("update user set nickname=#{nickname},email=#{email},update_time=#{updateTime} where id=#{id}")
    void update(User user);

    //修改头像
    @Update("update user set user_pic=#{avatarUrl},update_time=now() where id=#{id}")
    void updateAvatar(String avatarUrl, Integer id);

    //修改密码
    @Update("update user set password=#{md5String},update_time=now() where id=#{id}")
    void updatePwd(String md5String, Integer id);

    /**
     * 根据ID查询用户（包含角色信息）
     */
    @Select("SELECT u.*, r.role_name " +
            "FROM user u " +
            "LEFT JOIN roles r ON u.role_id = r.role_id " +
            "WHERE u.user_id = #{userId}")
    @Results({
            @Result(column = "user_id", property = "userId"),
            @Result(column = "role_id", property = "roleId"),
            @Result(column = "role_name", property = "roleName"),
            @Result(column = "real_name", property = "realName"),
            @Result(column = "created_at", property = "createdAt"),
            @Result(column = "updated_at", property = "updatedAt")
    })
    User findById(String userId);

    /**
     * 更新用户个人信息
     */
    @Update("update user set real_name=#{realName}, phone=#{phone}, " +
            "updated_at=now() where user_id=#{userId}")
    void updateProfile(User user);

    /**
     * 更新密码
     */
    @Update("update user set password=#{password}, updated_at=now() " +
            "where user_id=#{userId}")
    void updatePassword(@Param("userId") String userId, @Param("password") String password);

    @Select("SELECT role_id FROM user WHERE user_id = #{userId}")
    Integer getUserRoleId(String userId);

    /**
     * 分页查询用户列表
     */
    @Select("<script>" +
            "SELECT u.*, r.role_name FROM user u " +
            "LEFT JOIN roles r ON u.role_id = r.role_id " +
            "<where>" +
            "   <if test='query != null and query != \"\"'>" +
            "       AND (u.username LIKE CONCAT('%', #{query}, '%') " +
            "       OR u.real_name LIKE CONCAT('%', #{query}, '%'))" +
            "   </if>" +
            "</where>" +
            "ORDER BY u.created_at DESC " +
            "LIMIT #{offset}, #{pageSize}" +
            "</script>")
    @Results({
            @Result(column = "user_id", property = "userId"),
            @Result(column = "role_id", property = "roleId"),
            @Result(column = "role_name", property = "roleName"),
            @Result(column = "real_name", property = "realName"),
            @Result(column = "created_at", property = "createdAt"),
            @Result(column = "updated_at", property = "updatedAt")
    })
    List<User> selectUserList(@Param("offset") Integer offset,
                              @Param("pageSize") Integer pageSize,
                              @Param("query") String query);

    /**
     * 查询用户总数
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM user " +
            "<where>" +
            "   <if test='query != null and query != \"\"'>" +
            "       AND (username LIKE CONCAT('%', #{query}, '%') " +
            "       OR real_name LIKE CONCAT('%', #{query}, '%'))" +
            "   </if>" +
            "</where>" +
            "</script>")
    Integer selectUserCount(@Param("query") String query);

    /**
     * 插入新用户
     */
    @Insert("INSERT INTO user (user_id, username, password, role_id, real_name, phone, created_at, updated_at) " +
            "VALUES (#{userId}, #{username}, #{password}, #{roleId}, #{realName}, #{phone}, #{createdAt}, #{updatedAt})")
    void insertUser(User user);

    /**
     * 管理员更新用户信息
     */
    @Update("UPDATE user SET " +
            "username = #{username}, " +
            "role_id = #{roleId}, " +
            "real_name = #{realName}, " +
            "phone = #{phone}, " +
            "updated_at = NOW() " +
            "WHERE user_id = #{userId}")
    void updateUserByAdmin(User user);

    /**
     * 删除用户
     */
    @Delete("DELETE FROM user WHERE user_id = #{userId}")
    void deleteUser(String userId);
}