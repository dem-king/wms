import { describe, expect, it } from 'vitest'
import { buildMenuSubmitPayload } from './submit-payload'

describe('buildMenuSubmitPayload', () => {
  it('omits id and keeps menuCode when editing a menu', () => {
    const payload = buildMenuSubmitPayload({
      id: '200',
      parentId: '0',
      menuType: 1,
      menuName: '库房管理',
      menuCode: 'warehouse',
      icon: 'House',
      path: 'warehouse',
      component: 'warehouse/index',
      redirect: '',
      isExternal: 0,
      isCache: 0,
      visible: 1,
      status: 1,
      sortOrder: 2,
      permCode: '',
    })

    expect(payload).toEqual({
      parentId: '0',
      menuType: 1,
      menuName: '库房管理',
      menuCode: 'warehouse',
      icon: 'House',
      path: 'warehouse',
      component: 'warehouse/index',
      redirect: '',
      isExternal: 0,
      isCache: 0,
      visible: 1,
      status: 1,
      sortOrder: 2,
      permCode: '',
    })
    expect(payload).not.toHaveProperty('id')
  })
})
