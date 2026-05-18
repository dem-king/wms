import { get, post } from '../request'

export function getStockReport(params?: any) { return get('/report/stock', params) }
export function getInboundReport(params?: any) { return get('/report/inbound', params) }
export function getOutboundReport(params?: any) { return get('/report/outbound', params) }
export function getBorrowReturnReport(params?: any) { return get('/report/borrow-return', params) }
export function getScrapReport(params?: any) { return get('/report/scrap', params) }
export function getTransferReport(params?: any) { return get('/report/transfer', params) }
export function getAlertReport(params?: any) { return get('/report/alert', params) }
export function getCostReport(params?: any) { return get('/report/cost', params) }

export function exportStockReport(data: any) { return post('/report/stock/export', data, { responseType: 'blob' }) }
export function exportInboundReport(data: any) { return post('/report/inbound/export', data, { responseType: 'blob' }) }
export function exportOutboundReport(data: any) { return post('/report/outbound/export', data, { responseType: 'blob' }) }
