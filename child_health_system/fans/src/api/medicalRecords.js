import request from '@/utils/request.js'

/**
 * 获取就医记录列表（分页）
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.pageSize - 每页数量
 * @param {number} [params.childId] - 儿童ID（可选）
 */
export const getMedicalRecordListService = (params) => {
    return request.get('/medical-records', { params })
}

/**
 * 添加就医记录
 * @param {Object} data - 就医记录数据
 * @param {number} data.childId - 儿童ID
 * @param {string} data.doctorId - 医生ID（如果是医生添加，会自动设置）
 * @param {string} data.visitDate - 就诊日期
 * @param {string} data.diagnosis - 诊断结果
 * @param {string} data.treatment - 治疗方案
 * @param {string} data.hospitalName - 医院名称
 * @param {string} data.department - 科室
 * @param {string} [data.notes] - 备注信息（可选）
 * @param {Array} [data.medications] - 用药记录（可选）
 */
export const addMedicalRecordService = (data) => {
    return request.post('/medical-records', data)
}

/**
 * 更新就医记录
 * @param {number} recordId - 记录ID
 * @param {Object} data - 更新的数据
 * @param {string} data.visitDate - 就诊日期
 * @param {string} data.diagnosis - 诊断结果
 * @param {string} data.treatment - 治疗方案
 * @param {string} data.hospitalName - 医院名称
 * @param {string} data.department - 科室
 * @param {string} [data.notes] - 备注信息
 * @param {Array} [data.medications] - 用药记录
 */
export const updateMedicalRecordService = (recordId, data) => {
    return request.put(`/medical-records/${recordId}`, data)
}

/**
 * 删除就医记录
 * @param {number} recordId - 记录ID
 */
export const deleteMedicalRecordService = (recordId) => {
    return request.delete(`/medical-records/${recordId}`)
}
