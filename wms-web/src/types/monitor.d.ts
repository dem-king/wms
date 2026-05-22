/** 监控模块类型 */

/** 库存预警信息 */
export interface StockAlertVo {
  id: number
  alertType: string
  itemId: number
  itemName: string
  itemCode: string
  warehouseId: number
  warehouseName: string
  currentQuantity: number
  thresholdValue: number
  status: string
  triggerTime: string
}

/** 逾期归还信息 */
export interface OverdueReturnVo {
  id: number
  orderId: number
  orderNo: string
  itemId: number
  itemName: string
  itemCode: string
  borrowQuantity: number
  borrowerName: string
  borrowTime: string
  expectedReturnDate: string
  overdueDays: number
  alertLevel: string
  status: string
}
