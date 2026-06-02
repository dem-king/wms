/**
 * WMS-PDA SQLite操作工具
 * 初始化SQLite数据库，提供离线队列和操作日志的CRUD操作
 * APP环境使用plus.sqlite，非APP环境降级为uni.setStorageSync
 */
import type { OfflineQueueItem, OfflineQueueStatusValue } from '@/utils/constants'
import { OPERATION_LOG_RETENTION_DAYS } from '@/utils/constants'

/** 数据库名称 */
const DB_NAME = '_wms_pda'
/** 数据库路径 */
const DB_PATH = '_doc/wms_pda.db'

/** 操作日志记录 */
export interface OperationLogItem {
  /** 本地自增主键 */
  id: number
  /** 操作动作 */
  action: string
  /** 所属模块 */
  module: string
  /** 操作详情 */
  detail: string
  /** 操作时间戳（毫秒） */
  timestamp: number
  /** 是否成功 */
  success: boolean
}

// ==================== 条件编译：APP环境SQLite实现 ====================

// #ifdef APP-PLUS

/** SQLite数据库是否已打开 */
let dbOpened = false

/**
 * 打开SQLite数据库
 * 若数据库不存在则自动创建
 */
function openDatabase(): Promise<void> {
  return new Promise((resolve, reject) => {
    plus.sqlite.openDatabase({
      name: DB_NAME,
      path: DB_PATH,
      success: () => {
        dbOpened = true
        resolve()
      },
      fail: (e: { code: number; message: string }) => {
        console.error('打开数据库失败:', e)
        reject(new Error(`打开数据库失败: ${e.message}`))
      }
    })
  })
}

/**
 * 执行SQL语句（无返回结果）
 *
 * @param sql SQL语句
 */
function executeSql(sql: string): Promise<void> {
  return new Promise((resolve, reject) => {
    plus.sqlite.executeSql({
      name: DB_NAME,
      sql,
      success: () => resolve(),
      fail: (e: { code: number; message: string }) => {
        console.error('执行SQL失败:', sql, e)
        reject(new Error(`执行SQL失败: ${e.message}`))
      }
    })
  })
}

/**
 * 执行SQL查询，返回结果集
 *
 * @param sql SQL查询语句
 * @returns 查询结果数组
 */
function selectSql<T = Record<string, unknown>>(sql: string): Promise<T[]> {
  return new Promise((resolve, reject) => {
    plus.sqlite.selectSql({
      name: DB_NAME,
      sql,
      success: (data: T[]) => resolve(data),
      fail: (e: { code: number; message: string }) => {
        console.error('查询SQL失败:', sql, e)
        reject(new Error(`查询SQL失败: ${e.message}`))
      }
    })
  })
}

/**
 * 初始化数据库表结构
 * 创建offline_queue和operation_log表及索引
 */
async function createTables(): Promise<void> {
  // 创建离线队列表
  const createQueueTable = `
    CREATE TABLE IF NOT EXISTS offline_queue (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      url TEXT NOT NULL,
      method TEXT NOT NULL,
      body TEXT NOT NULL DEFAULT '',
      timestamp INTEGER NOT NULL,
      retry_count INTEGER NOT NULL DEFAULT 0,
      status TEXT NOT NULL DEFAULT 'pending',
      error_msg TEXT NOT NULL DEFAULT ''
    )
  `

  // 创建操作日志表
  const createLogTable = `
    CREATE TABLE IF NOT EXISTS operation_log (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      action TEXT NOT NULL,
      module TEXT NOT NULL,
      detail TEXT NOT NULL DEFAULT '',
      timestamp INTEGER NOT NULL,
      success INTEGER NOT NULL DEFAULT 1
    )
  `

  // 创建索引：按状态查询离线队列
  const createQueueStatusIndex = `
    CREATE INDEX IF NOT EXISTS idx_queue_status ON offline_queue (status)
  `

  // 创建索引：按时间戳查询操作日志
  const createLogTimestampIndex = `
    CREATE INDEX IF NOT EXISTS idx_log_timestamp ON operation_log (timestamp)
  `

  await executeSql(createQueueTable)
  await executeSql(createLogTable)
  await executeSql(createQueueStatusIndex)
  await executeSql(createLogTimestampIndex)
}

/**
 * 初始化数据库
 * 打开数据库并创建表结构，应用启动时调用
 */
export async function initDatabase(): Promise<void> {
  try {
    await openDatabase()
    await createTables()
    console.log('SQLite数据库初始化成功')
  } catch (error) {
    console.error('SQLite数据库初始化失败:', error)
    throw error
  }
}

/**
 * 关闭数据库
 * 应用退出时调用
 */
export function closeDatabase(): void {
  if (dbOpened) {
    plus.sqlite.closeDatabase({
      name: DB_NAME,
      success: () => {
        dbOpened = false
      },
      fail: () => {}
    })
  }
}

// ==================== 离线队列操作 ====================

/**
 * 入队：插入一条离线操作记录
 *
 * @param item 离线操作数据（不含id）
 * @returns 插入记录的id
 */
export async function enqueue(item: Omit<OfflineQueueItem, 'id'>): Promise<number> {
  const sql = `INSERT INTO offline_queue (url, method, body, timestamp, retry_count, status, error_msg) VALUES ('${escapeSql(item.url)}', '${escapeSql(item.method)}', '${escapeSql(item.body)}', ${item.timestamp}, ${item.retryCount}, '${escapeSql(item.status)}', '${escapeSql(item.errorMsg)}')`
  await executeSql(sql)

  // 查询最后插入的id
  const result = await selectSql<{ id: number }>('SELECT last_insert_rowid() as id')
  return result[0]?.id ?? 0
}

/**
 * 出队：获取最早的一条pending记录
 *
 * @returns 最早的pending记录，无记录时返回null
 */
export async function dequeue(): Promise<OfflineQueueItem | null> {
  const result = await selectSql<OfflineQueueItem>(
    "SELECT * FROM offline_queue WHERE status = 'pending' ORDER BY timestamp ASC LIMIT 1"
  )
  return result[0] ?? null
}

/**
 * 更新队列记录状态
 *
 * @param id 记录id
 * @param status 新状态
 * @param errorMsg 失败原因（可选）
 */
export async function updateQueueStatus(id: number, status: OfflineQueueStatusValue, errorMsg?: string): Promise<void> {
  const escapedErrorMsg = escapeSql(errorMsg ?? '')
  const sql = `UPDATE offline_queue SET status = '${escapeSql(status)}', error_msg = '${escapedErrorMsg}' WHERE id = ${id}`
  await executeSql(sql)
}

/**
 * 增加重试次数
 *
 * @param id 记录id
 */
export async function incrementRetryCount(id: number): Promise<void> {
  await executeSql(`UPDATE offline_queue SET retry_count = retry_count + 1 WHERE id = ${id}`)
}

/**
 * 获取队列中pending状态的记录数
 *
 * @returns pending记录数
 */
export async function getQueueSize(): Promise<number> {
  const result = await selectSql<{ count: number }>(
    "SELECT COUNT(*) as count FROM offline_queue WHERE status = 'pending'"
  )
  return result[0]?.count ?? 0
}

/**
 * 获取所有pending记录（按时间戳升序）
 *
 * @returns pending记录列表
 */
export async function getPendingRecords(): Promise<OfflineQueueItem[]> {
  return selectSql<OfflineQueueItem>(
    "SELECT * FROM offline_queue WHERE status = 'pending' ORDER BY timestamp ASC"
  )
}

/**
 * 获取所有failed记录
 *
 * @returns failed记录列表
 */
export async function getFailedRecords(): Promise<OfflineQueueItem[]> {
  return selectSql<OfflineQueueItem>(
    "SELECT * FROM offline_queue WHERE status = 'failed' ORDER BY timestamp ASC"
  )
}

/**
 * 清除已同步成功的记录
 * 删除status='success'的记录
 */
export async function clearSuccessRecords(): Promise<void> {
  await executeSql("DELETE FROM offline_queue WHERE status = 'success'")
}

/**
 * 获取队列总记录数（所有状态）
 *
 * @returns 总记录数
 */
export async function getTotalQueueSize(): Promise<number> {
  const result = await selectSql<{ count: number }>(
    'SELECT COUNT(*) as count FROM offline_queue'
  )
  return result[0]?.count ?? 0
}

// ==================== 操作日志操作 ====================

/**
 * 记录操作日志
 *
 * @param item 操作日志数据（不含id）
 */
export async function addOperationLog(item: Omit<OperationLogItem, 'id'>): Promise<void> {
  const successValue = item.success ? 1 : 0
  const sql = `INSERT INTO operation_log (action, module, detail, timestamp, success) VALUES ('${escapeSql(item.action)}', '${escapeSql(item.module)}', '${escapeSql(item.detail)}', ${item.timestamp}, ${successValue})`
  await executeSql(sql)
}

/**
 * 清理过期的操作日志
 * 保留最近N天的日志，删除更早的记录
 */
export async function cleanOperationLogs(): Promise<void> {
  // 计算过期时间戳：当前时间 - 保留天数 * 24小时 * 60分 * 60秒 * 1000毫秒
  const expireTimestamp = Date.now() - OPERATION_LOG_RETENTION_DAYS * 24 * 60 * 60 * 1000
  await executeSql(`DELETE FROM operation_log WHERE timestamp < ${expireTimestamp}`)
}

/**
 * 查询操作日志
 *
 * @param limit 最大返回条数，默认50
 * @returns 操作日志列表（按时间降序）
 */
export async function getOperationLogs(limit: number = 50): Promise<OperationLogItem[]> {
  return selectSql<OperationLogItem>(
    `SELECT * FROM operation_log ORDER BY timestamp DESC LIMIT ${limit}`
  )
}

// ==================== SQL注入防护 ====================

/**
 * 转义SQL字符串中的特殊字符，防止SQL注入
 *
 * @param value 原始字符串
 * @returns 转义后的字符串
 */
function escapeSql(value: string): string {
  if (!value) return ''
  return value.replace(/'/g, "''").replace(/\\/g, '\\\\')
}

// #endif

// ==================== 非APP环境：Storage降级实现 ====================

// #ifndef APP-PLUS

/** Storage Key常量 */
const STORAGE_KEYS = {
  /** 离线队列存储Key */
  OFFLINE_QUEUE: 'offline_queue',
  /** 操作日志存储Key */
  OPERATION_LOG: 'operation_log'
} as const

/**
 * 初始化数据库（Storage降级实现，无需操作）
 */
export async function initDatabase(): Promise<void> {
  console.log('非APP环境，使用Storage降级存储')
}

/**
 * 关闭数据库（Storage降级实现，无需操作）
 */
export function closeDatabase(): void {
  // Storage无需关闭
}

/**
 * 从Storage读取离线队列
 */
function readQueueFromStorage(): OfflineQueueItem[] {
  try {
    const data = uni.getStorageSync(STORAGE_KEYS.OFFLINE_QUEUE)
    return data ? JSON.parse(data) : []
  } catch {
    return []
  }
}

/**
 * 写入离线队列到Storage
 */
function writeQueueToStorage(queue: OfflineQueueItem[]): void {
  uni.setStorageSync(STORAGE_KEYS.OFFLINE_QUEUE, JSON.stringify(queue))
}

/**
 * 从Storage读取操作日志
 */
function readLogsFromStorage(): OperationLogItem[] {
  try {
    const data = uni.getStorageSync(STORAGE_KEYS.OPERATION_LOG)
    return data ? JSON.parse(data) : []
  } catch {
    return []
  }
}

/**
 * 写入操作日志到Storage
 */
function writeLogsToStorage(logs: OperationLogItem[]): void {
  uni.setStorageSync(STORAGE_KEYS.OPERATION_LOG, JSON.stringify(logs))
}

/**
 * 入队：插入一条离线操作记录（Storage降级）
 *
 * @param item 离线操作数据（不含id）
 * @returns 插入记录的id
 */
export async function enqueue(item: Omit<OfflineQueueItem, 'id'>): Promise<number> {
  const queue = readQueueFromStorage()
  // 生成自增id
  const maxId = queue.reduce((max, r) => Math.max(max, r.id), 0)
  const newId = maxId + 1
  const newItem: OfflineQueueItem = { ...item, id: newId }
  queue.push(newItem)
  writeQueueToStorage(queue)
  return newId
}

/**
 * 出队：获取最早的一条pending记录（Storage降级）
 *
 * @returns 最早的pending记录，无记录时返回null
 */
export async function dequeue(): Promise<OfflineQueueItem | null> {
  const queue = readQueueFromStorage()
  const pending = queue.filter(r => r.status === 'pending').sort((a, b) => a.timestamp - b.timestamp)
  return pending[0] ?? null
}

/**
 * 更新队列记录状态（Storage降级）
 *
 * @param id 记录id
 * @param status 新状态
 * @param errorMsg 失败原因（可选）
 */
export async function updateQueueStatus(id: number, status: OfflineQueueStatusValue, errorMsg?: string): Promise<void> {
  const queue = readQueueFromStorage()
  const record = queue.find(r => r.id === id)
  if (record) {
    record.status = status
    record.errorMsg = errorMsg ?? ''
    writeQueueToStorage(queue)
  }
}

/**
 * 增加重试次数（Storage降级）
 *
 * @param id 记录id
 */
export async function incrementRetryCount(id: number): Promise<void> {
  const queue = readQueueFromStorage()
  const record = queue.find(r => r.id === id)
  if (record) {
    record.retryCount++
    writeQueueToStorage(queue)
  }
}

/**
 * 获取队列中pending状态的记录数（Storage降级）
 *
 * @returns pending记录数
 */
export async function getQueueSize(): Promise<number> {
  const queue = readQueueFromStorage()
  return queue.filter(r => r.status === 'pending').length
}

/**
 * 获取所有pending记录（Storage降级）
 *
 * @returns pending记录列表
 */
export async function getPendingRecords(): Promise<OfflineQueueItem[]> {
  const queue = readQueueFromStorage()
  return queue.filter(r => r.status === 'pending').sort((a, b) => a.timestamp - b.timestamp)
}

/**
 * 获取所有failed记录（Storage降级）
 *
 * @returns failed记录列表
 */
export async function getFailedRecords(): Promise<OfflineQueueItem[]> {
  const queue = readQueueFromStorage()
  return queue.filter(r => r.status === 'failed').sort((a, b) => a.timestamp - b.timestamp)
}

/**
 * 清除已同步成功的记录（Storage降级）
 */
export async function clearSuccessRecords(): Promise<void> {
  const queue = readQueueFromStorage()
  const remaining = queue.filter(r => r.status !== 'success')
  writeQueueToStorage(remaining)
}

/**
 * 获取队列总记录数（Storage降级）
 *
 * @returns 总记录数
 */
export async function getTotalQueueSize(): Promise<number> {
  const queue = readQueueFromStorage()
  return queue.length
}

/**
 * 记录操作日志（Storage降级）
 *
 * @param item 操作日志数据（不含id）
 */
export async function addOperationLog(item: Omit<OperationLogItem, 'id'>): Promise<void> {
  const logs = readLogsFromStorage()
  const maxId = logs.reduce((max, r) => Math.max(max, r.id), 0)
  const newLog: OperationLogItem = { ...item, id: maxId + 1 }
  logs.push(newLog)
  writeLogsToStorage(logs)
}

/**
 * 清理过期的操作日志（Storage降级）
 * 保留最近N天的日志
 */
export async function cleanOperationLogs(): Promise<void> {
  const logs = readLogsFromStorage()
  const expireTimestamp = Date.now() - OPERATION_LOG_RETENTION_DAYS * 24 * 60 * 60 * 1000
  const remaining = logs.filter(r => r.timestamp >= expireTimestamp)
  writeLogsToStorage(remaining)
}

/**
 * 查询操作日志（Storage降级）
 *
 * @param limit 最大返回条数，默认50
 * @returns 操作日志列表（按时间降序）
 */
export async function getOperationLogs(limit: number = 50): Promise<OperationLogItem[]> {
  const logs = readLogsFromStorage()
  return logs.sort((a, b) => b.timestamp - a.timestamp).slice(0, limit)
}

// #endif