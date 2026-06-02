import type { CascaderOption, CascaderProps } from 'element-plus'
import { getAreaList } from '@/api/warehouse/area'
import { getBinList } from '@/api/warehouse/bin'
import { getCabinetList } from '@/api/warehouse/cabinet'
import type { EntityId } from '@/types/common'
import type { WmsWarehouseVo } from '@/types/warehouse'

export function toWarehouseLocationOptions(warehouses: WmsWarehouseVo[], warehouseId?: EntityId): CascaderOption[] {
  return warehouses
    .filter(warehouse => !warehouseId || warehouse.id === warehouseId)
    .map(warehouse => ({
      value: warehouse.id,
      label: warehouse.warehouseName,
      leaf: false,
    }))
}

export const locationCascaderProps: CascaderProps = {
  lazy: true,
  async lazyLoad(node, resolve) {
    const value = node.value as EntityId
    if (node.level === 1) {
      const res = await getAreaList(value)
      resolve(res.data.map(area => ({
        value: area.id,
        label: area.areaName,
        leaf: false,
      })))
      return
    }
    if (node.level === 2) {
      const res = await getCabinetList(value)
      resolve(res.data.map(cabinet => ({
        value: cabinet.id,
        label: cabinet.cabinetName,
        leaf: false,
      })))
      return
    }
    if (node.level === 3) {
      const res = await getBinList(value)
      resolve(res.data.map(bin => ({
        value: bin.id,
        label: bin.binCode,
        leaf: true,
      })))
      return
    }
    resolve([])
  },
}
