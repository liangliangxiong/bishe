//导入request.js请求工具
import request from '@/utils/request.js'

//提供调用注册接口的函数
export const userRegisterService = (registerData)=>{
    return request.post('/user/register', registerData);
}

//提供调用登录接口的函数
export const userLoginService = (loginData)=>{
    return request.post('/user/login', loginData)
}

//获取用户详细信息
export const userInfoService = ()=>{
    return request.get('/user/userInfo')
}

//修改个人信息
export const userInfoUpdateService = (userInfoData)=>{
   return request.put('/user/update',userInfoData)
}

//修改头像
export const userAvatarUpdateService = (avatarUrl)=>{
    return request.patch('/user/updateAvatar', { avatarUrl })
}

// 获取用户基本信息
export const getUserProfileService = () => {
    return request.get('/profile')
}

// 更新用户基本信息
export const updateUserProfileService = (profileData) => {
    return request.put('/profile', profileData)
}

// 修改密码
export const updatePasswordService = (passwordData) => {
    return request.put('/profile/password', {
        oldPassword: passwordData.oldPassword,
        newPassword: passwordData.newPassword,
        confirmPassword: passwordData.confirmPassword
    })
}

// 管理员用户管理相关接口

/**
 * 获取用户列表
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.pageSize - 每页数量
 * @param {string} params.query - 搜索关键词
 */
export const getUserListService = (params) => {
    return request.get('/admin/users', { params })
}

/**
 * 添加用户
 * @param {Object} userData - 用户数据
 */
export const addUserService = (userData) => {
    return request.post('/admin/users', userData)
}

/**
 * 更新用户信息
 * @param {string} userId - 用户ID
 * @param {Object} userData - 用户数据
 */
export const updateUserService = (userId, userData) => {
    return request.put(`/admin/users/${userId}`, userData)
}

/**
 * 删除用户
 * @param {string} userId - 用户ID
 */
export const deleteUserService = (userId) => {
    return request.delete(`/admin/users/${userId}`)
}

/**
 * 重置用户密码
 * @param {string} userId - 用户ID
 */
export const resetUserPasswordService = (userId) => {
    return request.post(`/admin/users/${userId}/reset-password`)
}

/**
 * 获取用户详情
 * @param {string} userId - 用户ID
 */
export const getUserDetailService = (userId) => {
    return request.get(`/admin/users/${userId}`)
}
