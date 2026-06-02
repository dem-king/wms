import type { EntityId, ItemLocationVo, WmsItemVo } from '@/types/item'

export type LocationPath = [EntityId, EntityId, EntityId, EntityId]

export interface PickDefaultLocationOptions {
  strictWarehouse?: boolean
}

export function pickDefaultLocation(
  item: WmsItemVo | undefined,
  currentWarehouseId?: EntityId,
  options: PickDefaultLocationOptions = {},
) {
  const locations = getSelectableLocations(item, currentWarehouseId, options)
  if (!locations.length) {
    return undefined
  }
  return locations[0]
}

export function getSelectableLocations(
  item: WmsItemVo | undefined,
  currentWarehouseId?: EntityId,
  options: PickDefaultLocationOptions = {},
) {
  const locations = item?.locations || []
  if (!currentWarehouseId) {
    return locations
  }
  const warehouseLocations = locations.filter(location => location.warehouseId === currentWarehouseId)
  if (warehouseLocations.length || options.strictWarehouse) {
    return warehouseLocations
  }
  return locations
}

export function locationToPath(location: ItemLocationVo | undefined): LocationPath | undefined {
  if (!location?.warehouseId || !location.areaId || !location.cabinetId || !location.binId) {
    return undefined
  }
  return [location.warehouseId, location.areaId, location.cabinetId, location.binId]
}

export function pathToBinId(path: LocationPath | undefined) {
  return path?.[3]
}

export function resolveLocationPathByBinId(
  item: WmsItemVo | undefined,
  binId: EntityId | undefined,
  warehouseId?: EntityId,
) {
  if (!item || !binId) {
    return undefined
  }
  const location = (item.locations || []).find(candidate => (
    candidate.binId === binId && (!warehouseId || candidate.warehouseId === warehouseId)
  ))
  return locationToPath(location)
}
