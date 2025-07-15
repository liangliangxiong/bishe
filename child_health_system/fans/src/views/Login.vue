<script setup>
import { User, Lock } from "@element-plus/icons-vue";
import { ref } from "vue";
import { ElMessage } from "element-plus";
//控制注册与登录表单的显示， 默认显示注册
const isRegister = ref(false);
//定义数据模型
const registerData = ref({
    username: "",
    password: "",
    rePassword: ""
});

//校验密码的函数
const checkRePassword = (rule, value, callback) => {
    if (value === "") {
        callback(new Error("请再次确认密码"));
    } else if (value !== registerData.value.password) {
        callback(new Error("请确保两次输入的密码一样"));
    } else {
        callback();
    }
};

//定义表单校验规则
const rules = {
    username: [
        { required: true, message: "请输入用户名", trigger: "blur" },
        { min: 5, max: 16, message: "长度为5~16位非空字符", trigger: "blur" },
        { pattern: /^\S+$/, message: "不能包含空格", trigger: "blur" }
    ],
    password: [
        { required: true, message: "请输入密码", trigger: "blur" },
        { min: 5, max: 16, message: "长度为5~16位非空字符", trigger: "blur" },
        { pattern: /^\S+$/, message: "不能包含空格", trigger: "blur" }
    ],
    rePassword: [{ validator: checkRePassword, trigger: "blur" }]
};

//调用后台接口,完成注册
import { userRegisterService, userLoginService, userInfoService } from "@/api/user.js";
const register = async () => {
    //registerData是一个响应式对象,如果要获取值,需要.value
    let result = await userRegisterService(registerData.value);
    console.log(result);
    // if (result.code === 0) {
    //     //成功了
    //     alert(result.msg ? result.msg : '注册成功');
    // }else{
    //     //失败了
    //     alert('注册失败')
    // }
    // alert(result.msg ? result.msg : "注册成功");
    ElMessage.success(result.msg ? result.msg : '注册成功')//注册成功
    isRegister.value = false;//切换到登录表单
    clearRegisterData();//清空表单数据
};

//绑定数据,复用注册表单的数据模型
//表单数据校验
//登录函数
import {useTokenStore} from '@/stores/token.js'
import {useRouter} from 'vue-router'
import { usePermissionStore } from '@/stores/permission'
const router = useRouter()
const tokenStore = useTokenStore();
const loading = ref(false);
const loginFormRef = ref(null);

const login = async () => {
    if (!loginFormRef.value) return;
    
    await loginFormRef.value.validate(async (valid) => {
        if (valid) {
            loading.value = true;
            try {
                // 1. 登录获取token
                const loginResult = await userLoginService(registerData.value);
                if (loginResult.code !== 0) {
                    throw new Error(loginResult.msg || '登录失败');
                }
                
                // 2. 存储token
                tokenStore.setToken(loginResult.data);
                
                // 3. 获取用户信息
                const userInfoResult = await userInfoService();
                if (userInfoResult.code === 0) {
                    // 存储用户信息
                    tokenStore.setUsername(userInfoResult.data.username);
                    tokenStore.setRole(userInfoResult.data.roleId, userInfoResult.data.roleName);
                    
                    // 4. 获取用户菜单权限
                    const permissionStore = usePermissionStore();
                    await permissionStore.getMenus();
                    
                    ElMessage.success(loginResult.msg || '登录成功');
                    
                    // 5. 跳转到首页或者目标页面
                    const redirect = router.currentRoute.value.query.redirect || '/';
                    router.push(redirect);
                } else {
                    throw new Error(userInfoResult.msg || '获取用户信息失败');
                }
            } catch (error) {
                console.error('登录失败:', error);
                ElMessage.error(error.message || '登录失败');
                // 登录失败时清除token
                tokenStore.removeToken();
            } finally {
                loading.value = false;
            }
        }
    });
};

//定义函数,清空数据模型的数据
const clearRegisterData = () => {
    registerData.value = {
        username: "",
        password: "",
        rePassword: ""
    };
};
</script>

<template>
    <el-row class="login-page">
        <el-col :span="12" class="bg">
            <div class="logo-container">
                <h1 class="logo-text">
                    <span class="primary-text">CHILD</span>
                </h1>
                <p class="subtitle">儿童健康跟踪管理系统</p>
            </div>
        </el-col>
        <el-col :span="6" :offset="3" class="form">
            <!-- 注册表单 -->
            <el-form ref="form" size="large" autocomplete="off" v-if="isRegister" :model="registerData" :rules="rules">
                <el-form-item>
                    <h1>注册</h1>
                </el-form-item>
                <el-form-item prop="username">
                    <el-input :prefix-icon="User" placeholder="请输入用户名" v-model="registerData.username"></el-input>
                </el-form-item>
                <el-form-item prop="password">
                    <el-input :prefix-icon="Lock" type="password" placeholder="请输入密码"
                        v-model="registerData.password"></el-input>
                </el-form-item>
                <el-form-item prop="rePassword">
                    <el-input :prefix-icon="Lock" type="password" placeholder="请输入再次密码"
                        v-model="registerData.rePassword"></el-input>
                </el-form-item>
                <!-- 注册按钮 -->
                <el-form-item>
                    <el-button class="button" type="primary" auto-insert-space @click="register">注册</el-button>
                </el-form-item>
                <el-form-item class="flex">
                    <el-link type="info" :underline="false" @click="isRegister = false; clearRegisterData()">←
                        返回</el-link>
                </el-form-item>
            </el-form>
            <!-- 登录表单 -->
            <el-form 
                ref="loginFormRef"
                size="large" 
                autocomplete="off" 
                v-else 
                :model="registerData" 
                :rules="rules"
            >
                <el-form-item>
                    <h1>登录</h1>
                </el-form-item>
                <el-form-item prop="username">
                    <el-input :prefix-icon="User" placeholder="请输入用户名" v-model="registerData.username"></el-input>
                </el-form-item>
                <el-form-item prop="password">
                    <el-input name="password" :prefix-icon="Lock" type="password" placeholder="请输入密码"
                        v-model="registerData.password"></el-input>
                </el-form-item>
                <el-form-item class="flex">
                    <div class="flex">
                        <el-checkbox>记住我</el-checkbox>
                        <el-link type="primary" :underline="false">忘记密码？</el-link>
                    </div>
                </el-form-item>
                <!-- 登录按钮 -->
                <el-form-item>
                    <el-button 
                        class="button" 
                        type="primary" 
                        :loading="loading"
                        auto-insert-space 
                        @click="login"
                    >
                        登录
                    </el-button>
                </el-form-item>
                <el-form-item class="flex">
                    <el-link type="info" :underline="false" @click="isRegister = true; clearRegisterData()">注册
                        →</el-link>
                </el-form-item>
            </el-form>
        </el-col>
    </el-row>
</template>

<style lang="scss" scoped>
/* 样式 */
.login-page {
    height: 100vh;
    background-color: #fff;

    .bg {
        background: #232323;
        border-radius: 0 20px 20px 0;
        display: flex;
        align-items: center;
        justify-content: center;

        .logo-container {
            text-align: center;
            
            .logo-text {
                font-size: 64px;
                font-weight: bold;
                letter-spacing: 2px;
                text-transform: uppercase;
                margin-bottom: 20px;
                
                .primary-text {
                    color: #ffd04b;
                    text-shadow: 0 0 20px rgba(255, 208, 75, 0.3);
                }
            }

            .subtitle {
                color: #ffffff;
                font-size: 24px;
                opacity: 0.9;
                letter-spacing: 4px;
            }
        }
    }

    .form {
        display: flex;
        flex-direction: column;
        justify-content: center;
        user-select: none;

        .title {
            margin: 0 auto;
        }

        .button {
            width: 100%;
        }

        .flex {
            width: 100%;
            display: flex;
            justify-content: space-between;
        }
    }
}
</style>