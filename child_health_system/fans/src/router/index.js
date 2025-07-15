//导入vue-router核心包
import { createRouter, createWebHistory } from 'vue-router'
import { useTokenStore } from '@/stores/token.js'  // 导入token存储

//导入所需组件
import LoginVue from '@/views/Login.vue'          // 登录页面组件
import LayoutVue from '@/views/Layout.vue'        // 布局框架组件
import UserAvatarVue from '@/views/user/UserAvatar.vue'    // 用户头像组件
import UserInfoVue from '@/views/user/UserInfo.vue'        // 用户信息组件
import UserResetPasswordVue from '@/views/user/UserResetPassword.vue'  // 重置密码组件
import Erroe401 from '@/views/error-page/401.vue'          // 401错误页面
import Erroe404 from '@/views/error-page/404.vue'          // 404错误页面
import Dashboard from '@/views/dashboard/index.vue'

//定义路由关系
const routes = [
    // 登录页路由
    { 
        path: '/login', 
        component: LoginVue,
        name: 'login',
        meta: { requiresAuth: false }  // 标记不需要认证
    },
    
    // 主布局路由
    {
        path: '/', 
        component: LayoutVue, 
        redirect: '/dashboard',
        meta: { requiresAuth: true },  // 标记需要认证
        children: [
            // 首页
            { 
                path: '/dashboard', 
                component: Dashboard,
                meta: { title: '首页' }
            },
            
            // 系统管理
            {
                path: '/system/users',
                component: () => import('@/views/system/user/index.vue'),
                meta: { title: '用户管理' }
            },
            {
                path: '/system/roles',
                component: () => import('@/views/system/role/index.vue'),
                meta: { title: '角色管理' }
            },
            {
                path: '/system/permissions',
                component: () => import('@/views/system/permission/index.vue'),
                meta: { title: '权限管理' }
            },

            // 儿童健康管理
            {
                path: '/child/info',
                component: () => import('@/views/child/info/index.vue'),
                meta: { title: '儿童信息' }
            },
            {
                path: '/child/growth',
                component: () => import('@/views/child/growth/index.vue'),
                meta: { title: '生长记录' }
            },
            {
                path: '/child/temperature',
                component: () => import('@/views/child/temperature/index.vue'),
                meta: { title: '体温记录' }
            },
            {
                path: '/child/diet',
                component: () => import('@/views/child/diet/index.vue'),
                meta: { title: '饮食记录' }
            },

            // 医疗管理
            {
                path: '/medical/visits',
                component: () => import('@/views/medical/visits/index.vue'),
                meta: { title: '就医记录' }
            },
            {
                path: '/medical/medications',
                component: () => import('@/views/medical/medications/index.vue'),
                meta: { title: '用药记录' }
            },
            {
                path: '/medical/vaccinations',
                component: () => import('@/views/medical/vaccinations/index.vue'),
                meta: { title: '疫苗接种' }
            },
            {
                path: '/medical/allergies',
                component: () => import('@/views/medical/allergies/index.vue'),
                meta: { title: '过敏史' }
            },

            // 健康分析
            // {
            //     path: '/analysis/growth',
            //     component: () => import('@/views/analysis/growth/index.vue'),
            //     meta: { title: '生长曲线' }
            // },
            // {
            //     path: '/analysis/trend',
            //     component: () => import('@/views/analysis/trend/index.vue'),
            //     meta: { title: '健康趋势' }
            // },

            // 用户相关路由
            { 
                path: '/user/avatar', 
                component: UserAvatarVue,
                meta: { title: '用户头像' }
            },
            { 
                path: '/user/info', 
                component: UserInfoVue,
                meta: { title: '用户信息' }
            },
            { 
                path: '/user/reset-password', 
                component: UserResetPasswordVue,
                meta: { title: '重置密码' }
            },
            
            // 错误页面
            { path: '/401', component: Erroe401 },
            { path: '/404', component: Erroe404 }
        ]
    },
    // 404路由需要放在最后
    { 
        path: '/:pathMatch(.*)*', 
        redirect: '/404'
    }
]

//创建路由器实例
const router = createRouter({
    history: createWebHistory(),
    routes: routes
});

// 添加全局前置守卫
router.beforeEach((to, from, next) => {
    const tokenStore = useTokenStore()
    
    // 设置页面标题
    if (to.meta.title) {
        document.title = `${to.meta.title} - CHILD健康管理系统`
    }
    
    // 如果需要认证且没有token
    if (to.meta.requiresAuth && !tokenStore.token) {
        // 保存目标路由，登录后可以直接跳转
        next({
            name: 'login',
            query: { redirect: to.fullPath }
        })
    } 
    // 如果已登录还访问登录页，直接跳转到首页
    else if (to.path === '/login' && tokenStore.token) {
        next('/')
    }
    else {
        next()
    }
})

//导出路由实例
export default router
