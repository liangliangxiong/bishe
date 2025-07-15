<script setup>
import {
  Management,
  Promotion,
  UserFilled,
  User,
  Crop,
  EditPen,
  SwitchButton,
  CaretBottom,
  Warning,
  HomeFilled,
  Menu,
  Location,
  Setting,
  Fold,
  Expand
} from "@element-plus/icons-vue";
// import avatar from "@/assets/default.png";
import avatar from "@/assets/spiderman.jpg";
import { useTokenStore } from '@/stores/token'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ref, watch, onMounted } from 'vue'
import { usePermissionStore } from '@/stores/permission'

const router = useRouter()
const route = useRoute()
const tokenStore = useTokenStore()
const permissionStore = usePermissionStore()

// 控制侧边栏折叠
const isCollapse = ref(false)
const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

// 当前激活的菜单项
const activeMenu = ref(route.path)

// 监听路由变化，更新激活菜单
watch(() => route.path, (newPath) => {
  activeMenu.value = newPath
})

// 处理下拉菜单的点击事件
const handleCommand = (command) => {
  switch(command) {
    case 'logout':
      handleLogout()
      break;
    case 'password':
      activeMenu.value = '/user/reset-password'
      router.push('/user/reset-password')
      break;
    case 'profile':
      activeMenu.value = '/user/info'
      router.push('/user/info')
      break;
    case 'avatar':
      activeMenu.value = '/user/avatar'
      router.push('/user/avatar')
      break;
    // 可以添加其他菜单项的处理...
  }
}

// 处理退出登录
const handleLogout = () => {
  // 清除token
  tokenStore.removeToken()
  // 提示用户
  ElMessage.success('退出登录成功')
  // 跳转到登录页
  router.push('/login')
}

// 确保菜单数据加载
onMounted(async () => {
  if (tokenStore.token && !permissionStore.menus.length) {
    try {
      await permissionStore.getMenus()
    } catch (error) {
      console.error('加载菜单失败:', error)
    }
  }
})
</script>

<template>
  <el-container class="layout-container">
    <!-- 左侧菜单 -->
    <el-aside :width="isCollapse ? '64px' : '200px'" class="aside">
      <!-- Logo区域 -->
      <div class="logo-box">
        <div class="logo-text" :class="{ 'collapsed': isCollapse }">
          <span class="primary-text">CHILD</span>
        </div>
      </div>

      <!-- 菜单区域 -->
      <el-menu
        :default-active="activeMenu"
        active-text-color="#ffd04b" 
        background-color="#232323" 
        text-color="#fff" 
        router 
        unique-opened
        :collapse="isCollapse"
        :collapse-transition="true"
      >
        <!-- 添加菜单数据调试信息 -->
        <div style="display: none;">
          {{ console.log('当前菜单数据:', permissionStore.menus) }}
          {{ console.log('当前用户角色:', tokenStore.roleId) }}
        </div>

        <el-menu-item index="/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>

        <!-- 渲染动态菜单 -->
        <div v-if="permissionStore.menus && permissionStore.menus.length">
          <div v-for="menu in permissionStore.menus" :key="menu.permissionId">
            <!-- 有子菜单的情况 -->
            <el-sub-menu 
              v-if="menu.children && menu.children.length" 
              :index="menu.menuUrl"
            >
              <template #title>
                <el-icon>
                  <component :is="menu.icon || 'Setting'" />
                </el-icon>
                <span>{{ menu.permissionName }}</span>
              </template>
              
              <el-menu-item 
                v-for="child in menu.children" 
                :key="child.permissionId"
                :index="child.menuUrl"
              >
                <el-icon>
                  <component :is="child.icon || 'Location'" />
                </el-icon>
                <span>{{ child.permissionName }}</span>
              </el-menu-item>
            </el-sub-menu>

            <!-- 没有子菜单的情况 -->
            <el-menu-item 
              v-else 
              :index="menu.menuUrl"
            >
              <el-icon>
                <component :is="menu.icon || 'Setting'" />
              </el-icon>
              <span>{{ menu.permissionName }}</span>
            </el-menu-item>
          </div>
        </div>
      </el-menu>
    </el-aside>
    <!-- 右侧主区域 -->
    <el-container>
      <!-- 头部区域 -->
      <el-header>
        <!-- 添加折叠按钮到头部 -->
        <div class="header-left">
          <el-icon class="fold-btn" @click="toggleCollapse">
            <Fold v-if="!isCollapse"/>
            <Expand v-else/>
          </el-icon>
          <span class="username">登录用户：<strong>{{ tokenStore.username }}</strong></span>
        </div>

        <!-- 用户信息区域 -->
        <el-dropdown placement="bottom-end" @command="handleCommand">
          <span class="el-dropdown__box">
            <el-avatar :src="avatar" />
            <el-icon>
              <CaretBottom />
            </el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile" :icon="User">基本资料</el-dropdown-item>
              <el-dropdown-item command="avatar" :icon="Crop">更换头像</el-dropdown-item>
              <el-dropdown-item command="password" :icon="EditPen">重置密码</el-dropdown-item>
              <el-dropdown-item command="logout" :icon="SwitchButton">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>
      <!-- 中间区域 -->
      <el-main>
        <!-- <div style="width: 1290px; height: 570px;border: 1px solid red;">内容显示区</div> -->
        <router-view></router-view>
      </el-main>
      <!-- 底部区域 -->
      <el-footer>CHILD ©2024 Created by 亮亮熊</el-footer>
    </el-container>
  </el-container>
</template>

<style lang="scss" scoped>
.layout-container {
  height: 100vh;

  .aside {
    background-color: #232323;
    transition: width 0.3s;
    overflow-x: hidden;

    .logo-box {
      height: 60px;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 0 20px;
      transition: all 0.3s;
      overflow: hidden;
      
      .logo-text {
        font-size: 24px;
        font-weight: bold;
        letter-spacing: 1px;
        text-transform: uppercase;
        white-space: nowrap;
        transition: all 0.3s;
        
        &.collapsed {
          font-size: 18px;
          letter-spacing: 0;
          
          .primary-text {
            margin-right: 0;
          }
        }
        
        .primary-text {
          color: #ffd04b;
          text-shadow: 0 0 10px rgba(255, 208, 75, 0.3);
        }
      }
    }

    .el-menu {
      border-right: none;
    }
  }

  .el-header {
    background-color: #fff;
    display: flex;
    align-items: center;
    justify-content: space-between;
    border-bottom: 1px solid #e6e6e6;

    .header-left {
      display: flex;
      align-items: center;
      gap: 20px;

      .fold-btn {
        font-size: 20px;
        cursor: pointer;
        color: #666;
        transition: all 0.3s;
        padding: 8px;
        border-radius: 4px;

        &:hover {
          background-color: #f5f5f5;
          color: #ffd04b;
        }
      }

      .username {
        color: #666;
        font-size: 14px;
      }
    }

    .el-dropdown__box {
      display: flex;
      align-items: center;
      
      .el-icon {
        color: #999;
        margin-left: 10px;
      }

      &:active,
      &:focus {
        outline: none;
      }
    }
  }

  .el-footer {
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 14px;
    color: #666;
  }
}

// 菜单折叠动画
.el-menu {
  transition: width 0.3s;
  
  &:not(.el-menu--collapse) {
    width: 200px;
  }
}

// 覆盖 element-plus 的默认样式，确保折叠时不显示文字
:deep(.el-menu--collapse) {
  .el-sub-menu__title span,
  .el-menu-item span {
    display: none;
  }
}
</style>