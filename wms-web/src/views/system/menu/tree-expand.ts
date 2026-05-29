import type { EntityId, MenuTreeNode } from '@/types/auth'

export type ExpandMode = 'first' | 'all' | 'none'

export function getExpandedRowKeysByMode(menuTree: MenuTreeNode[], mode: ExpandMode): EntityId[] {
  if (mode === 'none' || menuTree.length === 0) {
    return []
  }

  if (mode === 'first') {
    return [menuTree[0].id]
  }

  return collectTreeRowKeys(menuTree)
}

export function toggleExpandMode(mode: ExpandMode): ExpandMode {
  return mode === 'all' ? 'none' : 'all'
}

function collectTreeRowKeys(menuTree: MenuTreeNode[]): EntityId[] {
  return menuTree.flatMap((node) => [node.id, ...collectTreeRowKeys(node.children ?? [])])
}
