/**
 * 格式化日期时间
 * @param {string} dateTimeStr - 日期时间字符串
 * @param {boolean} [withTime=true] - 是否包含时间
 * @returns {string} 格式化后的日期时间字符串
 */
export const formatDateTime = (dateTimeStr, withTime = true) => {
    if (!dateTimeStr) return ''
    
    try {
        const date = new Date(dateTimeStr)
        if (isNaN(date.getTime())) return dateTimeStr
        
        const year = date.getFullYear()
        const month = String(date.getMonth() + 1).padStart(2, '0')
        const day = String(date.getDate()).padStart(2, '0')
        
        if (!withTime) {
            return `${year}-${month}-${day}`
        }
        
        const hours = String(date.getHours()).padStart(2, '0')
        const minutes = String(date.getMinutes()).padStart(2, '0')
        
        return `${year}-${month}-${day} ${hours}:${minutes}`
    } catch (error) {
        console.error('日期格式化错误:', error)
        return dateTimeStr
    }
}

/**
 * 格式化日期
 * @param {string} dateStr - 日期字符串
 * @returns {string} 格式化后的日期字符串
 */
export const formatDate = (dateStr) => {
    return formatDateTime(dateStr, false)
} 