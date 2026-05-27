import { get, post, put, del } from '../request'
import type { EntityId } from '@/types/common'

export function getItemList(params: any) {
  return get('/items', params)
}

export function getItem(id: EntityId) {
  return get(`/items/${id}`)
}

export function addItem(data: any) {
  return post('/items', data)
}

export function updateItem(id: EntityId, data: any) {
  return put(`/items/${id}`, data)
}

export function deleteItem(id: EntityId) {
  return del(`/items/${id}`)
}

export function searchItems(keyword: string) {
  return get('/items/search', { keyword })
}

export function getCategoryList() {
  return get('/categories')
}

export function addCategory(data: any) {
  return post('/categories', data)
}

export function getLabelList(params?: any) {
  return get('/labels', params)
}

export function generateLabels(data: any) {
  return post('/labels/generate', data)
}

export function getStockList(params?: any) {
  return get('/stock', params)
}

export function getStockAlert() {
  return get('/stock/alert')
}
