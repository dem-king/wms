import { describe, expect, it } from 'vitest'
import { getExpandedRowKeysByMode, toggleExpandMode, type ExpandMode } from './tree-expand'
import type { MenuTreeNode } from '@/types/auth'

function createNode(id: string, children: MenuTreeNode[] = []): MenuTreeNode {
  return {
    id,
    parentId: '0',
    menuName: `菜单${id}`,
    menuCode: `menu_${id}`,
    menuType: 1,
    path: '',
    component: '',
    redirect: '',
    icon: '',
    isExternal: 0,
    isCache: 0,
    visible: 1,
    sortOrder: 0,
    permCode: '',
    children,
  }
}

describe('menu tree expand state', () => {
  const menuTree = [
    createNode('100', [createNode('110')]),
    createNode('200', [createNode('210')]),
  ]

  it('expands only the first top-level menu by default', () => {
    expect(getExpandedRowKeysByMode(menuTree, 'first')).toEqual(['100'])
  })

  it('expands all rows in all mode', () => {
    expect(getExpandedRowKeysByMode(menuTree, 'all')).toEqual(['100', '110', '200', '210'])
  })

  it('collapses all rows in none mode', () => {
    expect(getExpandedRowKeysByMode(menuTree, 'none')).toEqual([])
  })

  it('toggles between all and none after default mode', () => {
    let mode: ExpandMode = 'first'
    mode = toggleExpandMode(mode)
    expect(mode).toBe('all')

    mode = toggleExpandMode(mode)
    expect(mode).toBe('none')
  })
})
