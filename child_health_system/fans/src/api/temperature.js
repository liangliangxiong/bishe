import request from '@/utils/request.js'

/**
 * 获取体温记录列表（分页）
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.pageSize - 每页数量
 * @param {number} [params.childId] - 儿童ID（可选）
 */
export const getTemperatureRecordListService = (params) => {
    return request.get('/temperature-records', { params })
}

/**
 * 获取体温记录详情
 * @param {number} recordId - 记录ID
 */
export const getTemperatureRecordDetailService = (recordId) => {
    return request.get(`/temperature-records/${recordId}`)
}

/**
 * 添加体温记录
 * @param {Object} data - 体温记录数据
 * @param {number} data.childId - 儿童ID
 * @param {number} data.temperature - 体温值
 * @param {string} data.measureTime - 测量时间
 * @param {string} data.measurePosition - 测量部位（MOUTH/ARMPIT/EAR）
 * @param {string} [data.notes] - 备注信息（可选）
 */
export const addTemperatureRecordService = (data) => {
    return request.post('/temperature-records', data)
}

/**
 * 更新体温记录
 * @param {number} recordId - 记录ID
 * @param {Object} data - 更新的数据
 * @param {number} data.temperature - 体温值
 * @param {string} data.measureTime - 测量时间
 * @param {string} data.measurePosition - 测量部位
 * @param {string} [data.notes] - 备注信息
 */
export const updateTemperatureRecordService = (recordId, data) => {
    return request.put(`/temperature-records/${recordId}`, data)
}

/**
 * 删除体温记录
 * @param {number} recordId - 记录ID
 */
export const deleteTemperatureRecordService = (recordId) => {
    return request.delete(`/temperature-records/${recordId}`)
} 