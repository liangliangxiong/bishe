import request from '@/utils/request.js'

// 获取用户菜单权限
export const getMenuPermissions = () => {
    return request.get('/permissions/menu/tree')
} 