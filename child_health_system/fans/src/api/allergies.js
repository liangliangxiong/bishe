import request from '@/utils/request.js'

/**
 * 获取过敏记录列表（分页）
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.pageSize - 每页数量
 * @param {number} [params.childId] - 儿童ID（可选）
 */
export const getAllergyListService = (params) => {
    return request.get('/allergies', { params })
}

/**
 * 添加过敏记录
 * @param {Object} data - 过敏记录数据
 * @param {number} data.childId - 儿童ID
 * @param {string} data.allergyName - 过敏源名称
 * @param {string} data.severity - 严重程度（MILD/MODERATE/SEVERE）
 * @param {string} data.diagnosisDate - 诊断日期
 * @param {string} [data.notes] - 备注信息（可选）
 */
export const addAllergyService = (data) => {
    return request.post('/allergies', data)
}

/**
 * 更新过敏记录
 * @param {number} allergyId - 记录ID
 * @param {Object} data - 更新的数据
 * @param {string} data.allergyName - 过敏源名称
 * @param {string} data.severity - 严重程度
 * @param {string} data.diagnosisDate - 诊断日期
 * @param {string} [data.notes] - 备注信息
 */
export const updateAllergyService = (allergyId, data) => {
    return request.put(`/allergies/${allergyId}`, data)
}

/**
 * 删除过敏记录
 * @param {number} allergyId - 记录ID
 */
export const deleteAllergyService = (allergyId) => {
    return request.delete(`/allergies/${allergyId}`)
}
