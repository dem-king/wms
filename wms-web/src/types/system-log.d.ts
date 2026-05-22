/** 系统模块类型（补充日志相关） */

/** 操作日志信息 */
export interface SysOperLogVo {
  id: number
  module: string
  type: string
  desc: string
  operatorId: number
  operatorName: string
  requestUrl: string
  requestMethod: string
  requestParams: string
  responseResult: string
  operIp: string
  status: string
  errorMsg: string
  costTime: number
  operTime: string
  createTime: string
}

/** 登录日志信息 */
export interface SysLoginLogVo {
  id: number
  username: string
  loginIp: string
  loginLocation: string
  browser: string
  os: string
  status: string
  failReason: string
  loginTime: string
  createTime: string
}
