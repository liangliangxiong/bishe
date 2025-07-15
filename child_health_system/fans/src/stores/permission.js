import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getMenuPermissions } from '@/api/permission'
import { ElMessage } from 'element-plus'

export const usePermissionStore = defineStore('permission', () => {
    // 存储菜单数据
    const menus = ref([])
    
    // 获取菜单数据
    const getMenus = async () => {
        try {
            const res = await getMenuPermissions()
            
            if (res.code === 0 && Array.isArray(res.data)) {
                // 处理菜单数据,确保icon属性存在
                const processMenus = (menuList) => {
                    return menuList.map(menu => ({
                        ...menu,
                        icon: menu.icon || 'Setting',
                        children: menu.children ? processMenus(menu.children) : []
                    }))
                }
                
                menus.value = processMenus(res.data)
            } else {
                console.error('菜单数据格式错误:', res)
                ElMessage.error('获取菜单失败')
                menus.value = []
            }
        } catch (error) {
            console.error('获取菜单失败:', error)
            ElMessage.error('获取菜单失败')
            menus.value = []
        }
    }

    return {
        menus,
        getMenus
    }
}, {
    persist: true
}) 