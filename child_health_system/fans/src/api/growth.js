import request from '@/utils/request.js'

/**
 * 获取生长记录列表（分页）
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.pageSize - 每页数量
 * @param {number} params.childId - 儿童ID（可选）
 */
export const getGrowthRecordListService = (params) => {
    return request.get('/growth-records', { params })
}

/**
 * 获取指定儿童的生长记录
 * @param {number} childId - 儿童ID
 */
export const getChildGrowthRecordsService = (childId) => {
    return request.get(`/growth-records/child/${childId}`)
}

/**
 * 添加生长记录
 * @param {Object} recordData - 记录数据
 */
export const addGrowthRecordService = (recordData) => {
    return request.post('/growth-records', recordData)
}

/**
 * 更新生长记录
 * @param {number} recordId - 记录ID
 * @param {Object} recordData - 记录数据
 */
export const updateGrowthRecordService = (recordId, recordData) => {
    return request.put(`/growth-records/${recordId}`, recordData)
}

/**
 * 删除生长记录
 * @param {number} recordId - 记录ID
 */
export const deleteGrowthRecordService = (recordId) => {
    return request.delete(`/growth-records/${recordId}`)
} 