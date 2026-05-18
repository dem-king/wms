import { get, post, put } from '../request'

export function getInboundList(params?: any) {
  return get('/inbound/orders', params)
}

export function getInboundDetail(id: number) {
  return get(`/inbound/orders/${id}`)
}

export function addInbound(data: any) {
  return post('/inbound/orders', data)
}

export function submitInbound(id: number) {
  return put(`/inbound/orders/${id}/submit`)
}

export function getOutboundList(params?: any) {
  return get('/outbound/orders', params)
}

export function addOutbound(data: any) {
  return post('/outbound/orders', data)
}

export function submitOutbound(id: number) {
  return put(`/outbound/orders/${id}/submit`)
}

export function getReturnList(params?: any) {
  return get('/return/orders', params)
}

export function addReturn(data: any) {
  return post('/return/orders', data)
}

export function getScrapList(params?: any) {
  return get('/scrap/orders', params)
}

export function addScrap(data: any) {
  return post('/scrap/orders', data)
}

export function getTransferList(params?: any) {
  return get('/transfer/orders', params)
}

export function addTransfer(data: any) {
  return post('/transfer/orders', data)
}

