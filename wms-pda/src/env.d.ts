/**
 * UniApp 全局类型声明
 * 扩展Vue模块以支持.vue文件类型
 */

declare module '*.vue' {
  import { DefineComponent } from 'vue'
  const component: DefineComponent<object, object, unknown>
  export default component
}

/** uni 对象上的扩展方法 */
declare namespace UniApp {
  /** 页面生命周期 */
  interface Page {
    $vm: {
      $options: {
        onLaunch?: () => void
        onShow?: () => void
        onHide?: () => void
      }
    }
  }
}

/** plus 对象类型声明（5+ API） */
interface Plus {
  device: {
    /** 振动 */
    vibrate(duration: number): void
  }
  navigator: {
    /** 安全存储 */
    setSecurityStorage(key: string, value: string): void
    getSecurityStorage(key: string): string
  }
  sqlite: {
    /** 打开数据库 */
    openDatabase(options: {
      name: string
      path: string
      success?: (res: unknown) => void
      fail?: (err: unknown) => void
    }): void
    /** 执行SQL */
    executeSql(options: {
      name: string
      sql: string | string[]
      success?: (res: unknown) => void
      fail?: (err: unknown) => void
    }): void
    /** 查询SQL */
    selectSql(options: {
      name: string
      sql: string
      success?: (res: unknown[]) => void
      fail?: (err: unknown) => void
    }): void
  }
}

declare const plus: Plus