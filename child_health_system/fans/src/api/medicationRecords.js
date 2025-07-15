import request from '@/utils/request.js'

/**
 * 获取用药记录列表
 * @param {number} medicalRecordId - 就医记录ID
 * @returns {Promise} 返回用药记录列表
 */
export const getMedicationListService = (medicalRecordId) => {
    return request.get(`/medication-records/medical/${medicalRecordId}`)
}

/**
 * 添加用药记录
 * @param {Object} data - 用药记录数据
 * @param {number} data.medical_record_id - 关联的就医记录ID
 * @param {string} data.medicine_name - 药品名称
 * @param {string} data.dosage - 剂量
 * @param {string} data.frequency - 服用频率
 * @param {string} data.start_date - 开始日期
 * @param {string} data.end_date - 结束日期
 * @param {string} [data.notes] - 备注信息（可选）
 */
export const addMedicationRecordService = (data) => {
    return request.post('/medication-records', data)
}

/**
 * 更新用药记录
 * @param {number} recordId - 记录ID
 * @param {Object} data - 更新的数据
 * @param {string} data.medicine_name - 药品名称
 * @param {string} data.dosage - 剂量
 * @param {string} data.frequency - 服用频率
 * @param {string} data.start_date - 开始日期
 * @param {string} data.end_date - 结束日期
 * @param {string} [data.notes] - 备注信息
 */
export const updateMedicationRecordService = (recordId, data) => {
    return request.put(`/medication-records/${recordId}`, data)
}

/**
 * 删除用药记录
 * @param {number} recordId - 记录ID
 */
export const deleteMedicationRecordService = (recordId) => {
    return request.delete(`/medication-records/${recordId}`)
}
