import type { EntityId } from '@/types/auth'

export interface MenuSubmitForm {
  id?: EntityId
  parentId: EntityId
  menuType: number
  menuName: string
  menuCode: string
  path: string
  component: string
  redirect: string
  icon: string
  isExternal: number
  isCache: number
  visible: number
  status: number
  sortOrder: number
  permCode: string
}

export type MenuSubmitPayload = Omit<MenuSubmitForm, 'id'>

export function buildMenuSubmitPayload(form: MenuSubmitForm): MenuSubmitPayload {
  return {
    parentId: form.parentId,
    menuType: form.menuType,
    menuName: form.menuName,
    menuCode: form.menuCode,
    path: form.path,
    component: form.component,
    redirect: form.redirect,
    icon: form.icon,
    isExternal: form.isExternal,
    isCache: form.isCache,
    visible: form.visible,
    status: form.status,
    sortOrder: form.sortOrder,
    permCode: form.permCode,
  }
}
