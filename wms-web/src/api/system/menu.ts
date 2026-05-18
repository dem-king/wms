import { get, post, put, del } from '../request'
import type { MenuTreeNode } from '@/types/auth'

export function getMenuList() {
  return get<MenuTreeNode[]>('/system/menus')
}

export function getMenu(id: number) {
  return get<MenuTreeNode>(`/system/menus/${id}`)
}

export function addMenu(data: Partial<MenuTreeNode> & { parentId: number }) {
  return post<MenuTreeNode>('/system/menus', data)
}

export function updateMenu(id: number, data: Partial<MenuTreeNode>) {
  return put<MenuTreeNode>(`/system/menus/${id}`, data)
}

export function deleteMenu(id: number) {
  return del<void>(`/system/menus/${id}`)
}

export function getMenuTree() {
  return get<MenuTreeNode[]>('/system/menus/tree')
}

export function getUserMenuTree() {
  return get<MenuTreeNode[]>('/system/menus/userMenuTree')
}
