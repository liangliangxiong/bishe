import { useTokenStore } from '@/stores/token'

export const permission = {
    mounted(el, binding) {
        const tokenStore = useTokenStore()
        const { value } = binding
        
        // 判断是否有权限
        const hasPermission = () => {
            if (value === 'admin' && tokenStore.roleId === 1) return true
            if (value === 'doctor' && tokenStore.roleId === 2) return true
            if (value === 'parent' && tokenStore.roleId === 3) return true
            return false
        }

        if (!hasPermission()) {
            el.parentNode?.removeChild(el)
        }
    }
} 