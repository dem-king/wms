/**
 * WMS-PDA 格式化工具函数
 * 提供日期格式化、数量格式化、金额格式化等通用方法
 */

/**
 * 日期格式化
 * 将日期字符串或时间戳格式化为指定格式
 *
 * @param date 日期字符串（ISO格式）或时间戳（毫秒）
 * @param format 格式模板，默认 'YYYY-MM-DD HH:mm:ss'
 * @returns 格式化后的日期字符串
 */
export function formatDate(
  date: string | number | null | undefined,
  format: string = 'YYYY-MM-DD HH:mm:ss'
): string {
  if (date === null || date === undefined || date === '') {
    return '-'
  }

  let dateObj: Date

  if (typeof date === 'number') {
    dateObj = new Date(date)
  } else if (typeof date === 'string') {
    // 处理ISO格式日期字符串
    dateObj = new Date(date.replace(/-/g, '/'))
  } else {
    return '-'
  }

  // 校验日期有效性
  if (isNaN(dateObj.getTime())) {
    return '-'
  }

  const year = dateObj.getFullYear()
  const month = dateObj.getMonth() + 1
  const day = dateObj.getDate()
  const hours = dateObj.getHours()
  const minutes = dateObj.getMinutes()
  const seconds = dateObj.getSeconds()

  const pad = (num: number): string => num.toString().padStart(2, '0')

  return format
    .replace('YYYY', year.toString())
    .replace('MM', pad(month))
    .replace('DD', pad(day))
    .replace('HH', pad(hours))
    .replace('mm', pad(minutes))
    .replace('ss', pad(seconds))
}

/**
 * 短日期格式化（仅日期）
 *
 * @param date 日期字符串或时间戳
 * @returns 格式化后的日期字符串，如 2024-01-15
 */
export function formatDateShort(date: string | number | null | undefined): string {
  return formatDate(date, 'YYYY-MM-DD')
}

/**
 * 时间格式化（仅时间）
 *
 * @param date 日期字符串或时间戳
 * @returns 格式化后的时间字符串，如 14:30:00
 */
export function formatTime(date: string | number | null | undefined): string {
  return formatDate(date, 'HH:mm:ss')
}

/**
 * 数量格式化
 * 将数量格式化为千分位分隔的字符串
 *
 * @param quantity 数量值
 * @param decimals 小数位数，默认0
 * @returns 格式化后的数量字符串，如 1,234 或 1,234.56
 */
export function formatQuantity(quantity: number | null | undefined, decimals: number = 0): string {
  if (quantity === null || quantity === undefined) {
    return '0'
  }

  if (typeof quantity !== 'number' || isNaN(quantity)) {
    return '0'
  }

  return quantity.toLocaleString('zh-CN', {
    minimumFractionDigits: decimals,
    maximumFractionDigits: decimals
  })
}

/**
 * 金额格式化
 * 将金额格式化为千分位分隔的货币字符串
 *
 * @param amount 金额值
 * @param prefix 货币符号，默认 '¥'
 * @param decimals 小数位数，默认2
 * @returns 格式化后的金额字符串，如 ¥1,234.56
 */
export function formatAmount(
  amount: number | null | undefined,
  prefix: string = '¥',
  decimals: number = 2
): string {
  if (amount === null || amount === undefined) {
    return `${prefix}0.00`
  }

  if (typeof amount !== 'number' || isNaN(amount)) {
    return `${prefix}0.00`
  }

  const formatted = amount.toLocaleString('zh-CN', {
    minimumFractionDigits: decimals,
    maximumFractionDigits: decimals
  })

  return `${prefix}${formatted}`
}

/**
 * 百分比格式化
 *
 * @param value 数值（0-1之间）
 * @param decimals 小数位数，默认1
 * @returns 格式化后的百分比字符串，如 85.5%
 */
export function formatPercent(value: number | null | undefined, decimals: number = 1): string {
  if (value === null || value === undefined || isNaN(value)) {
    return '0%'
  }

  return `${(value * 100).toFixed(decimals)}%`
}

/**
 * 相对时间格式化
 * 将时间转换为"刚刚"、"X分钟前"、"X小时前"等相对时间描述
 *
 * @param date 日期字符串或时间戳
 * @returns 相对时间描述
 */
export function formatRelativeTime(date: string | number | null | undefined): string {
  if (date === null || date === undefined || date === '') {
    return '-'
  }

  let dateObj: Date
  if (typeof date === 'number') {
    dateObj = new Date(date)
  } else {
    dateObj = new Date(date.replace(/-/g, '/'))
  }

  if (isNaN(dateObj.getTime())) {
    return '-'
  }

  const now = Date.now()
  const diff = now - dateObj.getTime()

  // 1分钟内
  if (diff < 60 * 1000) {
    return '刚刚'
  }

  // 1小时内
  if (diff < 60 * 60 * 1000) {
    const minutes = Math.floor(diff / (60 * 1000))
    return `${minutes}分钟前`
  }

  // 24小时内
  if (diff < 24 * 60 * 60 * 1000) {
    const hours = Math.floor(diff / (60 * 60 * 1000))
    return `${hours}小时前`
  }

  // 7天内
  if (diff < 7 * 24 * 60 * 60 * 1000) {
    const days = Math.floor(diff / (24 * 60 * 60 * 1000))
    return `${days}天前`
  }

  // 超过7天，显示具体日期
  return formatDateShort(date)
}