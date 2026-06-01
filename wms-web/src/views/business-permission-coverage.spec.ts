import { readFileSync } from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'
import { describe, expect, it } from 'vitest'

const viewsRoot = path.resolve(path.dirname(fileURLToPath(import.meta.url)))

const pagePermissions: Record<string, string[]> = {
  'warehouse/warehouse/index.vue': [
    'warehouse:warehouse:add',
    'warehouse:warehouse:edit',
    'warehouse:warehouse:delete',
  ],
  'warehouse/area/index.vue': [
    'warehouse:area:add',
    'warehouse:area:edit',
    'warehouse:area:delete',
  ],
  'warehouse/cabinet/index.vue': [
    'warehouse:cabinet:add',
    'warehouse:cabinet:edit',
    'warehouse:cabinet:delete',
  ],
  'warehouse/bin/index.vue': [
    'warehouse:bin:add',
    'warehouse:bin:edit',
    'warehouse:bin:delete',
    'warehouse:bin:batch',
  ],
  'item/list/index.vue': [
    'item:item:add',
    'item:item:edit',
    'item:item:delete',
  ],
  'item/category/index.vue': [
    'item:category:add',
    'item:category:edit',
    'item:category:delete',
  ],
  'item/tag/index.vue': [
    'item:tag:add',
    'item:tag:edit',
    'item:tag:delete',
  ],
  'item/machineSpare/index.vue': [
    'item:machine-spare:add',
    'item:machine-spare:edit',
    'item:machine-spare:delete',
  ],
  'label/index.vue': [
    'item:label:generate',
    'item:label:bind',
    'item:label:status',
    'item:label:print',
  ],
  'stock/index.vue': [
    'item:stock:edit',
  ],
  'business/inbound/index.vue': [
    'business:inbound:add',
    'business:inbound:edit',
    'business:inbound:delete',
    'business:inbound:submit',
  ],
  'business/outbound/index.vue': [
    'business:outbound:add',
    'business:outbound:edit',
    'business:outbound:delete',
    'business:outbound:submit',
  ],
  'business/return/index.vue': [
    'business:return:add',
    'business:return:edit',
    'business:return:delete',
    'business:return:submit',
  ],
  'business/scrap/index.vue': [
    'business:scrap:add',
    'business:scrap:edit',
    'business:scrap:delete',
    'business:scrap:submit',
  ],
  'business/transfer/index.vue': [
    'business:transfer:add',
    'business:transfer:edit',
    'business:transfer:delete',
    'business:transfer:submit',
  ],
  'approval/config/index.vue': [
    'approval:config:add',
    'approval:config:edit',
    'approval:config:delete',
  ],
  'approval/pending/index.vue': [
    'approval:pending:approve',
    'approval:pending:reject',
  ],
  'monitor/stock-alert/index.vue': [
    'monitor:stock-alert:resolve',
  ],
}

describe('business permission coverage', () => {
  it('binds menu backed business actions to permission codes', () => {
    const missingPermissions = Object.entries(pagePermissions).flatMap(([relativePath, permissions]) => {
      const source = readFileSync(path.resolve(viewsRoot, relativePath), 'utf-8')
      return permissions
        .filter((permission) => !source.includes(permission))
        .map((permission) => `${relativePath}: ${permission}`)
    })

    expect(missingPermissions).toEqual([])
  })
})
