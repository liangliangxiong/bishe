import request from '@/utils/request.js'

/**
 * 获取饮食记录列表（分页）
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.pageSize - 每页数量
 * @param {number} [params.childId] - 儿童ID（可选）
 */
export const getDietRecordListService = (params) => {
    return request.get('/diet-records', { params })
}

/**
 * 添加饮食记录
 * @param {Object} data - 饮食记录数据
 * @param {number} data.childId - 儿童ID
 * @param {string} data.mealType - 餐次类型（BREAKFAST/LUNCH/DINNER/SNACK）
 * @param {string} data.mealTime - 用餐时间
 * @param {string} data.foodItems - 食物项目
 * @param {string} data.amount - 食用量
 * @param {string} [data.notes] - 备注信息（可选）
 */
export const addDietRecordService = (data) => {
    return request.post('/diet-records', data)
}

/**
 * 更新饮食记录
 * @param {number} recordId - 记录ID
 * @param {Object} data - 更新的数据
 * @param {string} data.mealType - 餐次类型
 * @param {string} data.mealTime - 用餐时间
 * @param {string} data.foodItems - 食物项目
 * @param {string} data.amount - 食用量
 * @param {string} [data.notes] - 备注信息
 */
export const updateDietRecordService = (recordId, data) => {
    return request.put(`/diet-records/${recordId}`, data)
}

/**
 * 删除饮食记录
 * @param {number} recordId - 记录ID
 */
export const deleteDietRecordService = (recordId) => {
    return request.delete(`/diet-records/${recordId}`)
}

/**
 * 获取饮食统计信息
 * @param {number} childId - 儿童ID
 * @returns {Promise<Object>} 包含统计数据的对象
 * - statistics.monthly: 按月统计数据
 * - statistics.daily: 按日统计数据
 * - weeklyRecords: 最近一周的记录
 */
export const getDietStatisticsService = (childId) => {
    return request.get(`/diet-records/statistics/${childId}`)
}

