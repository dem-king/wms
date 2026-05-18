import { get, post, put, del } from '../request'

export function getItemList(params: any) {
  return get('/items', params)
}

export function getItem(id: number) {
  return get(`/items/${id}`)
}

export function addItem(data: any) {
  return post('/items', data)
}

export function updateItem(id: number, data: any) {
  return put(`/items/${id}`, data)
}

export function deleteItem(id: number) {
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
