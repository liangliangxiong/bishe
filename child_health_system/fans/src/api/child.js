import request from '@/utils/request.js'

/**
 * 获取儿童列表（分页）
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.pageSize - 每页数量
 * @param {string} params.query - 搜索关键词
 */
export const getChildListService = (params) => {
    return request.get('/children', { params })
}

/**
 * 获取我的儿童列表
 */
export const getMyChildrenService = () => {
    return request.get('/children/my')
}

/**
 * 获取儿童详情
 * @param {number} childId - 儿童ID
 */
export const getChildDetailService = (childId) => {
    return request.get(`/children/${childId}`)
}

/**
 * 添加儿童信息
 * @param {Object} childData - 儿童信息
 */
export const addChildService = (childData) => {
    return request.post('/children', childData)
}

/**
 * 更新儿童信息
 * @param {number} childId - 儿童ID
 * @param {Object} childData - 儿童信息
 */
export const updateChildService = (childId, childData) => {
    return request.put(`/children/${childId}`, childData)
}

/**
 * 删除儿童信息
 * @param {number} childId - 儿童ID
 */
export const deleteChildService = (childId) => {
    return request.delete(`/children/${childId}`)
} 