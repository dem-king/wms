import { get, post, put, del } from '../request'
import type { WmsCategoryVo, WmsCategoryDto, WmsSubCategoryVo, WmsSubCategoryDto } from '@/types/item'

export function getCategoryList() {
  return get<WmsCategoryVo[]>('/categories')
}

export function addCategory(data: WmsCategoryDto) {
  return post<WmsCategoryVo>('/categories', data)
}

export function updateCategory(id: number, data: WmsCategoryDto) {
  return put<WmsCategoryVo>(`/categories/${id}`, data)
}

export function deleteCategory(id: number) {
  return del<void>(`/categories/${id}`)
}

export function getSubCategories(categoryId: number) {
  return get<WmsSubCategoryVo[]>(`/categories/${categoryId}/sub`)
}

export function addSubCategory(data: WmsSubCategoryDto) {
  return post<WmsSubCategoryVo>('/categories/sub', data)
}

export function updateSubCategory(id: number, data: WmsSubCategoryDto) {
  return put<WmsSubCategoryVo>(`/categories/sub/${id}`, data)
}

export function deleteSubCategory(id: number) {
  return del<void>(`/categories/sub/${id}`)
}

