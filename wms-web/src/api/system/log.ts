import { get } from '../request'
import type { SysOperLogVo, SysLoginLogVo } from '@/types/system-log'

export function getOperLogPage(params: any) { return get<SysOperLogVo>('/system/oper-log', params) }
export function getLoginLogPage(params: any) { return get<SysLoginLogVo>('/system/login-log', params) }
