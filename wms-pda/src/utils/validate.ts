/**
 * WMS-PDA 校验工具函数
 * 提供URL合法性校验、编码格式校验等通用校验方法
 */

/**
 * URL合法性校验
 * 校验字符串是否为合法的HTTP/HTTPS URL
 *
 * @param url 待校验的URL字符串
 * @returns 是否为合法URL
 */
export function isValidUrl(url: string): boolean {
  if (!url || typeof url !== 'string') {
    return false
  }

  // 去除首尾空格
  const trimmedUrl = url.trim()

  // 空字符串
  if (trimmedUrl.length === 0) {
    return false
  }

  // URL正则：支持http/https协议，支持IP和域名，支持端口号
  const urlPattern = /^https?:\/\/(([a-zA-Z0-9]([a-zA-Z0-9-]*[a-zA-Z0-9])?\.)+[a-zA-Z]{2,}|(\d{1,3}\.){3}\d{1,3})(:\d{1,5})?(\/.*)?$/

  return urlPattern.test(trimmedUrl)
}

/**
 * 服务器地址校验
 * 校验服务器地址格式（允许不带路径的地址）
 *
 * @param url 服务器地址
 * @returns 是否为合法的服务器地址
 */
export function isValidServerUrl(url: string): boolean {
  if (!url || typeof url !== 'string') {
    return false
  }

  const trimmedUrl = url.trim()

  // 移除末尾的斜杠
  const normalizedUrl = trimmedUrl.replace(/\/+$/, '')

  // 服务器地址正则：http/https + 域名或IP + 可选端口
  const serverUrlPattern = /^https?:\/\/(([a-zA-Z0-9]([a-zA-Z0-9-]*[a-zA-Z0-9])?\.)+[a-zA-Z]{2,}|(\d{1,3}\.){3}\d{1,3})(:\d{1,5})?$/

  return serverUrlPattern.test(normalizedUrl)
}

/**
 * 编码格式校验
 * 校验编码是否符合WMS系统编码规范
 * 支持的编码格式：
 * - 物品编码：WP + 年月日 + 4位流水号（如 WP202401150001）
 * - 库房编码：KF + 年月日 + 4位流水号
 * - 标签编码：BQ + 年月日 + 4位流水号
 * - 入库单号：RK + 年月日 + 4位流水号
 * - 出库单号：CK + 年月日 + 4位流水号
 * - 归还单号：GH + 年月日 + 4位流水号
 * - 报废单号：BF + 年月日 + 4位流水号
 * - 调拨单号：DB + 年月日 + 4位流水号
 *
 * @param code 待校验的编码
 * @returns 是否为合法编码
 */
export function isValidCode(code: string): boolean {
  if (!code || typeof code !== 'string') {
    return false
  }

  const trimmedCode = code.trim()

  // 通用编码格式：2位前缀 + 8位日期(YYYYMMDD) + 4位流水号
  const codePattern = /^[A-Z]{2}\d{12}$/

  return codePattern.test(trimmedCode)
}

/**
 * 物品编码校验
 * 校验是否为合法的物品编码（WP前缀）
 *
 * @param code 物品编码
 * @returns 是否为合法的物品编码
 */
export function isValidItemCode(code: string): boolean {
  if (!isValidCode(code)) {
    return false
  }
  return code.trim().startsWith('WP')
}

/**
 * 单据编码校验
 * 校验是否为合法的单据编码
 *
 * @param code 单据编码
 * @returns 是否为合法的单据编码
 */
export function isValidOrderCode(code: string): boolean {
  if (!isValidCode(code)) {
    return false
  }
  const orderPrefixes = ['RK', 'CK', 'GH', 'BF', 'DB']
  const prefix = code.trim().substring(0, 2)
  return orderPrefixes.includes(prefix)
}

/**
 * 标签编码校验
 * 校验是否为合法的标签编码（BQ前缀）
 *
 * @param code 标签编码
 * @returns 是否为合法的标签编码
 */
export function isValidLabelCode(code: string): boolean {
  if (!isValidCode(code)) {
    return false
  }
  return code.trim().startsWith('BQ')
}

/**
 * 用户名校验
 * 校验用户名是否合法（4-20位字母数字下划线）
 *
 * @param username 用户名
 * @returns 是否合法
 */
export function isValidUsername(username: string): boolean {
  if (!username || typeof username !== 'string') {
    return false
  }
  const usernamePattern = /^[a-zA-Z0-9_]{4,20}$/
  return usernamePattern.test(username.trim())
}

/**
 * 手机号校验（中国大陆手机号）
 *
 * @param phone 手机号
 * @returns 是否合法
 */
export function isValidPhone(phone: string): boolean {
  if (!phone || typeof phone !== 'string') {
    return false
  }
  const phonePattern = /^1[3-9]\d{9}$/
  return phonePattern.test(phone.trim())
}