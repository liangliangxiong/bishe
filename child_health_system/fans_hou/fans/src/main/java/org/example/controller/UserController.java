package org.example.controller;

import jakarta.validation.constraints.Pattern;
import org.example.pojo.Result;
import org.example.pojo.User;
import org.example.service.UserService;
import org.example.utils.JwtUtil;
import org.example.utils.Md5Util;
import org.example.utils.ThreadLocalUtil;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("/hello")
    public String hello() {
        return "hello";
    }

    //注册
    @PostMapping("/register")
    public Result register(@RequestBody Map<String, String> params) {
        // 参数校验
        String username = params.get("username");
        String password = params.get("password");
        
        // 校验用户名和密码格式
        if (!username.matches("^\\S{5,16}$") || !password.matches("^\\S{5,16}$")) {
            return Result.error("用户名和密码必须是5-16位的非空字符");
        }
        
        //查询用户
        User u = userService.findByUserName(username);
        if (u == null) {
            //没有占用，注册
            userService.register(username, password);
            return Result.success();
        } else {
            //占用
            return Result.error("用户名已被占用");
        }
    }

    //登录
    @PostMapping("/login")
    public Result<String> login(@RequestBody Map<String, String> params) {
        // 参数校验
        String username = params.get("username");
        String password = params.get("password");
        
        if (username == null || password == null) {
            return Result.error("用户名或密码不能为空");
        }
        
//  log.info("用户登录：{}", username); // 添加日志
        
        User loginUser = userService.findByUserName(username);
        if (loginUser == null) {
            return Result.error("用户名错误");
        }
        
        if (!Md5Util.checkPassword(password, loginUser.getPassword())) {
            return Result.error("密码错误");
        }
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", loginUser.getUserId());
        claims.put("username", loginUser.getUsername());
        claims.put("roleId", loginUser.getRoleId());
        
        String token = JwtUtil.genToken(claims);
        return Result.success(token);
    }

    //获取用户信息
    @GetMapping("/userInfo")
    public Result<User> userInfo(/*@RequestHeader(name = "Authorization") String token*/) {
//        Map<String, Object> map = JwtUtil.parseToken(token);
//        String username = (String)map.get("username");

        //从ThreadLocal中获取用户信息
        Map<String, Object> map = ThreadLocalUtil.get();
        //获取用户名
        String username = (String) map.get("username");
        //查询用户信息
        User user = userService.findByUserName(username);
        return Result.success(user);
    }


    //更新用户信息
    @PutMapping("/update")
    public Result update(@RequestBody @Validated User user) {
        userService.update(user);
        return Result.success("更新用户信息成功.");
    }

    //更新头像
    @PatchMapping("/updateAvatar")
    public Result updateAvatar(@RequestParam @URL String avatarUrl) {
        userService.updateAvatar(avatarUrl);
        return Result.success("更新头像成功.");
    }

    @PatchMapping("/updatePwd")
    public Result updatePwd(@RequestBody Map<String, String> params) {
        //校验参数
        String oldPwd = params.get("old_pwd");
        String newPwd = params.get("new_pwd");
        String reOwd = params.get("re_pwd");
        //判断参数是否为空
        if (!StringUtils.hasLength(oldPwd) || !StringUtils.hasLength(newPwd) || !StringUtils.hasLength(reOwd)) {
            return Result.error("参数错误.");
        }
        //校验原密码是否正确
        //用userService根据用户名拿到原密码，再与上面的旧密码进行比对
        Map<String, Object> map = ThreadLocalUtil.get();
        String username = (String) map.get("username");
//        System.out.println(username);
        User u = userService.findByUserName(username);
        // 将 userId 从 String 转换为 Integer
        Integer id = Integer.parseInt(u.getUserId());
//        Integer id = u.getUserId();
        System.out.println(id);
        //已经拿到了用户信息了呀
        System.out.println(u);
        //这个是加密过的密码，需要解密
        if (!Md5Util.getMD5String(oldPwd).equals(u.getPassword())) {
            return Result.error("原密码错误.");
        }
        if (!newPwd.equals(reOwd)) {
            return Result.error("两次密码不一致.");
        }
        try {
            userService.updatePwd(newPwd, id);
        } catch (Exception e) {
            return Result.error("修改密码失败.");
        }
        return Result.success("修改密码成功.");

    }
}


