/**
 * 计算两个对象的差异，返回obj2中与obj1不同的部分
 * @param obj1 原始对象
 * @param obj2 比较对象
 * @returns 差异部分
 */
export function diff<T extends Record<string, any>>(obj1: T, obj2: T): Partial<T> {
  function findDifferences(o1: any, o2: any): any {
    if (Array.isArray(o1) && Array.isArray(o2)) {
      if (JSON.stringify(o1) !== JSON.stringify(o2)) {
        return o2
      }
      return undefined
    }

    if (
      typeof o1 === 'object' &&
      typeof o2 === 'object' &&
      o1 !== null &&
      o2 !== null
    ) {
      const diffResult: any = {}
      const keys = new Set([...Object.keys(o1), ...Object.keys(o2)])
      keys.forEach((key) => {
        const valueDiff = findDifferences(o1[key], o2[key])
        if (valueDiff !== undefined) {
          diffResult[key] = valueDiff
        }
      })
      return Object.keys(diffResult).length > 0 ? diffResult : undefined
    }

    return o1 === o2 ? undefined : o2
  }

  return findDifferences(obj1, obj2) || {}
}
