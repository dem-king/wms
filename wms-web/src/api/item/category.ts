import { get, post, put, del } from '../request'
import type { PageParams, PageResult } from '@/types/system'
import type { EntityId, WmsCategoryVo, WmsCategoryDto, WmsSubCategoryVo, WmsSubCategoryDto } from '@/types/item'

export function getCategoryPage(params: PageParams) {
  return get<PageResult<WmsCategoryVo>>('/item/categories', params)
}

export function getCategoryList() {
  return get<WmsCategoryVo[]>('/item/categories/list')
}

export function addCategory(data: WmsCategoryDto) {
  return post<WmsCategoryVo>('/item/categories', data)
}

export function updateCategory(id: EntityId, data: WmsCategoryDto) {
  return put<WmsCategoryVo>(`/item/categories/${id}`, data)
}

export function deleteCategory(id: EntityId) {
  return del<void>(`/item/categories/${id}`)
}

export function getSubCategoryPage(categoryId: EntityId, params: PageParams) {
  return get<PageResult<WmsSubCategoryVo>>(`/item/categories/${categoryId}/sub`, params)
}

export function getSubCategories(categoryId: EntityId) {
  return get<WmsSubCategoryVo[]>(`/item/categories/${categoryId}/sub/list`)
}

export function addSubCategory(categoryId: EntityId, data: WmsSubCategoryDto) {
  return post<WmsSubCategoryVo>(`/item/categories/${categoryId}/sub`, data)
}

export function updateSubCategory(id: EntityId, data: WmsSubCategoryDto) {
  return put<WmsSubCategoryVo>(`/item/categories/sub/${id}`, data)
}

export function deleteSubCategory(id: EntityId) {
  return del<void>(`/item/categories/sub/${id}`)
}
