/**
 * 文件下载工具函数
 */

export function downloadBlob(blob: Blob, filename: string) {
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = filename
  a.click()
  URL.revokeObjectURL(url)
}

export function getExportFilename(reportType: string, ext: string) {
  const now = new Date()
  const dateStr = `${now.getFullYear()}${String(now.getMonth() + 1).padStart(2, '0')}${String(now.getDate()).padStart(2, '0')}`
  const typeMap: Record<string, string> = {
    inbound: '入库统计',
    outbound: '出库统计',
    stock: '库存统计',
    return: '借还统计',
    scrap: '报废统计',
    transfer: '调拨统计',
    alert: '预警统计'
  }
  const typeName = typeMap[reportType] || reportType
  return `${typeName}_${dateStr}.${ext}`
}
