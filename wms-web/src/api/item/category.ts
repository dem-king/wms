import { get, post, put, del } from '../request'
import type { WmsCategoryVo, WmsCategoryDto, WmsSubCategoryVo, WmsSubCategoryDto } from '@/types/item'

export function getCategoryList() {
  return get<WmsCategoryVo[]>('/item/categories')
}

export function addCategory(data: WmsCategoryDto) {
  return post<WmsCategoryVo>('/item/categories', data)
}

export function updateCategory(id: number, data: WmsCategoryDto) {
  return put<WmsCategoryVo>(`/item/categories/${id}`, data)
}

export function deleteCategory(id: number) {
  return del<void>(`/item/categories/${id}`)
}

export function getSubCategories(categoryId: number) {
  return get<WmsSubCategoryVo[]>(`/item/categories/${categoryId}/sub`)
}

export function addSubCategory(categoryId: number, data: WmsSubCategoryDto) {
  return post<WmsSubCategoryVo>(`/item/categories/${categoryId}/sub`, data)
}

export function updateSubCategory(id: number, data: WmsSubCategoryDto) {
  return put<WmsSubCategoryVo>(`/item/categories/sub/${id}`, data)
}

export function deleteSubCategory(id: number) {
  return del<void>(`/item/categories/sub/${id}`)
}
