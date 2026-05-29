import type { EntityId, ItemLocationVo } from '@/types/item'

export type LocationCascaderValue = EntityId[][]

export function buildLocationText(location: ItemLocationVo) {
  return [
    location.warehouseName,
    location.areaName,
    location.cabinetName,
    location.binCode,
  ].filter(Boolean).join('/')
}

export function extractBinIds(paths: LocationCascaderValue) {
  return paths
    .map(path => path[path.length - 1])
    .filter((id): id is EntityId => id !== undefined && id !== null)
}

export function locationsToCascaderValue(locations: ItemLocationVo[] = []) {
  return locations.map(location => [
    location.warehouseId,
    location.areaId,
    location.cabinetId,
    location.binId,
  ].filter((id): id is EntityId => id !== undefined && id !== null))
}

export function formatLocations(locations: ItemLocationVo[] = []) {
  return locations
    .map(location => location.locationText || buildLocationText(location))
    .filter(Boolean)
}
