import request from '@/utils/request.js'

/**
 * 获取接种记录列表（分页）
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.pageSize - 每页数量
 * @param {number} [params.childId] - 儿童ID（可选）
 */
export const getVaccinationListService = (params) => {
    return request.get('/vaccinations', { params })
}

/**
 * 添加接种记录
 * @param {Object} data - 接种记录数据
 * @param {number} data.childId - 儿童ID
 * @param {string} data.vaccineName - 疫苗名称
 * @param {string} data.vaccinationDate - 接种日期
 * @param {string} data.nextDueDate - 下次接种日期
 * @param {string} data.doctorId - 医生ID（如果是医生添加，会自动设置）
 * @param {string} [data.notes] - 备注信息（可选）
 */
export const addVaccinationService = (data) => {
    return request.post('/vaccinations', data)
}

/**
 * 更新接种记录
 * @param {number} vaccinationId - 记录ID
 * @param {Object} data - 更新的数据
 * @param {string} data.vaccineName - 疫苗名称
 * @param {string} data.vaccinationDate - 接种日期
 * @param {string} data.nextDueDate - 下次接种日期
 * @param {string} data.doctorId - 医生ID
 * @param {string} [data.notes] - 备注信息
 */
export const updateVaccinationService = (vaccinationId, data) => {
    return request.put(`/vaccinations/${vaccinationId}`, data)
}

/**
 * 删除接种记录
 * @param {number} vaccinationId - 记录ID
 */
export const deleteVaccinationService = (vaccinationId) => {
    return request.delete(`/vaccinations/${vaccinationId}`)
}
