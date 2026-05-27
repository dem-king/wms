import type {
  EntityId,
  WmsAreaVo,
  WmsBinVo,
  WmsCabinetLayoutSaveVo,
  WmsCabinetVo,
  WmsWarehouseVo,
} from '@/types/warehouse'

export const VISUAL_STATUS_ENABLED = 1
export const VISUAL_STATUS_DISABLED = 0

export const VISUAL_PADDING = 24
const VISUAL_AREA_GAP = 24
const VISUAL_CABINET_GAP = 12
export const VISUAL_AREA_HEADER_HEIGHT = 48
const VISUAL_CABINET_WIDTH = 180
const VISUAL_CABINET_HEIGHT = 140
const VISUAL_AREA_MIN_WIDTH = 420
const VISUAL_AREA_MIN_HEIGHT = 280

export interface WarehouseVisualBuildInput {
  warehouse: WmsWarehouseVo
  areas: WmsAreaVo[]
  cabinets: WmsCabinetVo[]
  bins: WmsBinVo[]
}

export interface WarehouseVisualViewport {
  width: number
  height: number
  padding: number
  areaGap: number
  cabinetGap: number
}

export interface WarehouseVisualEmptyState {
  title: string
  description: string
}

export interface WarehouseVisualSummary {
  totalAreas: number
  totalCabinets: number
  totalBins: number
  disabledAreas: number
  disabledCabinets: number
  disabledBins: number
}

export interface WarehouseVisualBinNode {
  id: EntityId
  cabinetId: EntityId
  binCode: string
  row: number
  col: number
  status: number
}

export interface WarehouseVisualCabinetNode {
  id: EntityId
  areaId: EntityId
  cabinetName: string
  cabinetCode: string
  sortOrder: number
  status: number
  rows: number
  cols: number
  x: number
  y: number
  width: number
  height: number
  gridRowCount: number
  gridColCount: number
  binIds: EntityId[]
  cells: Array<Array<WarehouseVisualBinNode | null>>
}

export interface WarehouseVisualAreaNode {
  id: EntityId
  areaName: string
  areaCode: string
  sortOrder: number
  status: number
  cabinetIds: EntityId[]
  x: number
  y: number
  width: number
  height: number
}

export interface WarehouseVisualModel {
  warehouseId: EntityId
  warehouseName: string
  viewport: WarehouseVisualViewport
  empty: WarehouseVisualEmptyState | null
  summary: WarehouseVisualSummary
  areas: WarehouseVisualAreaNode[]
  cabinets: Record<string, WarehouseVisualCabinetNode>
  bins: Record<string, WarehouseVisualBinNode>
}

export type WarehouseVisualMatchedType = 'area' | 'cabinet' | 'bin'

export interface WarehouseVisualLocationMatch {
  matchedType: WarehouseVisualMatchedType
  areaId: EntityId
  cabinetId: EntityId | null
  binId: EntityId | null
  matchedCode: string
}

/**
 * 将库房、区域、存放柜、库位组装为稳定的 2D 可视化模型。
 */
export function buildWarehouseVisualModel(input: WarehouseVisualBuildInput): WarehouseVisualModel {
  const sortedAreas = [...input.areas].sort(compareBySortOrder)
  const areaIdSet = new Set(sortedAreas.map(area => area.id))
  const sortedCabinets = [...input.cabinets].sort(compareCabinet)

  for (const cabinet of sortedCabinets) {
    if (!areaIdSet.has(cabinet.areaId)) {
      throw new Error(`存放柜 ${cabinet.id} 缺少所属区域`)
    }
  }

  const cabinetIdSet = new Set(sortedCabinets.map(cabinet => cabinet.id))
  for (const bin of input.bins) {
    if (!cabinetIdSet.has(bin.cabinetId)) {
      throw new Error(`库位 ${bin.id} 缺少所属存放柜`)
    }
  }

  const cabinetsByArea = new Map<EntityId, WmsCabinetVo[]>()
  for (const cabinet of sortedCabinets) {
    const cabinetList = cabinetsByArea.get(cabinet.areaId) ?? []
    cabinetList.push(cabinet)
    cabinetsByArea.set(cabinet.areaId, cabinetList)
  }

  const binsByCabinet = new Map<EntityId, WmsBinVo[]>()
  for (const bin of [...input.bins].sort(compareBin)) {
    const binList = binsByCabinet.get(bin.cabinetId) ?? []
    binList.push(bin)
    binsByCabinet.set(bin.cabinetId, binList)
  }

  const summary: WarehouseVisualSummary = {
    totalAreas: sortedAreas.length,
    totalCabinets: sortedCabinets.length,
    totalBins: input.bins.length,
    disabledAreas: sortedAreas.filter(area => area.status === VISUAL_STATUS_DISABLED).length,
    disabledCabinets: sortedCabinets.filter(cabinet => cabinet.status === VISUAL_STATUS_DISABLED).length,
    disabledBins: input.bins.filter(bin => bin.binStatus === VISUAL_STATUS_DISABLED).length,
  }

  if (sortedAreas.length === 0) {
    return {
      warehouseId: input.warehouse.id,
      warehouseName: input.warehouse.warehouseName,
      viewport: createViewport(VISUAL_AREA_MIN_WIDTH, VISUAL_AREA_MIN_HEIGHT),
      empty: {
        title: '当前库房暂无区域数据',
        description: '请先在区域管理中维护区域，再查看 2D 布局。',
      },
      summary,
      areas: [],
      cabinets: {},
      bins: {},
    }
  }

  if (sortedCabinets.length === 0) {
    return {
      warehouseId: input.warehouse.id,
      warehouseName: input.warehouse.warehouseName,
      viewport: createViewport(VISUAL_AREA_MIN_WIDTH, VISUAL_AREA_MIN_HEIGHT),
      empty: {
        title: '当前库房暂无存放柜数据',
        description: '请先在存放柜管理中维护存放柜，再查看 2D 布局。',
      },
      summary,
      areas: [],
      cabinets: {},
      bins: {},
    }
  }

  const areaNodes: WarehouseVisualAreaNode[] = []
  const cabinetNodes: Record<string, WarehouseVisualCabinetNode> = {}
  const binNodes: Record<string, WarehouseVisualBinNode> = {}

  let currentX = VISUAL_PADDING
  let maxHeight = VISUAL_AREA_MIN_HEIGHT

  for (const area of sortedAreas) {
    const areaCabinets = cabinetsByArea.get(area.id) ?? []
    const cabinetIds = areaCabinets.map(cabinet => cabinet.id)
    const areaWidth = Math.max(
      VISUAL_AREA_MIN_WIDTH,
      VISUAL_PADDING * 2 + calculateCabinetContentWidth(areaCabinets),
    )
    const areaHeight = Math.max(
      VISUAL_AREA_MIN_HEIGHT,
      VISUAL_AREA_HEADER_HEIGHT + VISUAL_PADDING + calculateCabinetContentHeight(areaCabinets),
    )

    areaNodes.push({
      id: area.id,
      areaName: area.areaName,
      areaCode: area.areaCode,
      sortOrder: area.sortOrder,
      status: area.status,
      cabinetIds,
      x: currentX,
      y: VISUAL_PADDING,
      width: areaWidth,
      height: areaHeight,
    })

    areaCabinets.forEach((cabinet, index) => {
      const cabinetBins = binsByCabinet.get(cabinet.id) ?? []
      const cabinetNode = createCabinetNode(cabinet, cabinetBins, currentX, VISUAL_PADDING, index)
      cabinetNodes[cabinet.id] = cabinetNode

      for (const binNode of cabinetNode.binIds.map(binId => cabinetNode.cells.flat().find(cell => cell?.id === binId)).filter(Boolean) as WarehouseVisualBinNode[]) {
        binNodes[binNode.id] = binNode
      }
    })

    currentX += areaWidth + VISUAL_AREA_GAP
    maxHeight = Math.max(maxHeight, areaHeight)
  }

  return {
    warehouseId: input.warehouse.id,
    warehouseName: input.warehouse.warehouseName,
    viewport: createViewport(currentX - VISUAL_AREA_GAP, maxHeight),
    empty: null,
    summary,
    areas: areaNodes,
    cabinets: cabinetNodes,
    bins: binNodes,
  }
}

/**
 * 按区域、存放柜、库位编码定位当前可视化节点，优先精确匹配，再回退到包含匹配。
 */
export function findVisualLocationByCode(
  model: WarehouseVisualModel,
  keyword: string,
): WarehouseVisualLocationMatch | null {
  const normalizedKeyword = normalizeCode(keyword)
  if (!normalizedKeyword) {
    return null
  }

  const exactArea = model.areas.find(area => normalizeCode(area.areaCode) === normalizedKeyword)
  if (exactArea) {
    return {
      matchedType: 'area',
      areaId: exactArea.id,
      cabinetId: null,
      binId: null,
      matchedCode: exactArea.areaCode,
    }
  }

  const exactCabinet = Object.values(model.cabinets).find(
    cabinet => normalizeCode(cabinet.cabinetCode) === normalizedKeyword,
  )
  if (exactCabinet) {
    return {
      matchedType: 'cabinet',
      areaId: exactCabinet.areaId,
      cabinetId: exactCabinet.id,
      binId: null,
      matchedCode: exactCabinet.cabinetCode,
    }
  }

  const exactBin = Object.values(model.bins).find(bin => normalizeCode(bin.binCode) === normalizedKeyword)
  if (exactBin) {
    return {
      matchedType: 'bin',
      areaId: model.cabinets[exactBin.cabinetId]?.areaId ?? '',
      cabinetId: exactBin.cabinetId,
      binId: exactBin.id,
      matchedCode: exactBin.binCode,
    }
  }

  const partialArea = model.areas.find(area => normalizeCode(area.areaCode).includes(normalizedKeyword))
  if (partialArea) {
    return {
      matchedType: 'area',
      areaId: partialArea.id,
      cabinetId: null,
      binId: null,
      matchedCode: partialArea.areaCode,
    }
  }

  const partialCabinet = Object.values(model.cabinets).find(
    cabinet => normalizeCode(cabinet.cabinetCode).includes(normalizedKeyword),
  )
  if (partialCabinet) {
    return {
      matchedType: 'cabinet',
      areaId: partialCabinet.areaId,
      cabinetId: partialCabinet.id,
      binId: null,
      matchedCode: partialCabinet.cabinetCode,
    }
  }

  const partialBin = Object.values(model.bins).find(
    bin => normalizeCode(bin.binCode).includes(normalizedKeyword),
  )
  if (partialBin) {
    return {
      matchedType: 'bin',
      areaId: model.cabinets[partialBin.cabinetId]?.areaId ?? '',
      cabinetId: partialBin.cabinetId,
      binId: partialBin.id,
      matchedCode: partialBin.binCode,
    }
  }

  return null
}

/**
 * 将布局保存结果回写到当前可视化模型，便于前端在保存后立即回显最新位置。
 */
export function applyCabinetLayoutSaveResult(
  model: WarehouseVisualModel,
  result: WmsCabinetLayoutSaveVo,
): WarehouseVisualModel {
  const savedCabinetMap = new Map(result.cabinets.map(cabinet => [cabinet.id, cabinet]))
  const warehouse = {
    id: model.warehouseId,
    warehouseName: model.warehouseName,
    warehouseCode: '',
    address: '',
    manager: '',
    area: 0,
    status: VISUAL_STATUS_ENABLED,
    createTime: '',
  } satisfies WmsWarehouseVo
  const areas = model.areas.map(area => ({
    id: area.id,
    warehouseId: model.warehouseId,
    areaName: area.areaName,
    areaCode: area.areaCode,
    sortOrder: area.sortOrder,
    status: area.status,
    createTime: '',
  }))
  const cabinets = Object.values(model.cabinets).map((cabinet) => {
    const area = model.areas.find(item => item.id === cabinet.areaId)
    const savedCabinet = savedCabinetMap.get(cabinet.id)

    return {
      id: cabinet.id,
      areaId: cabinet.areaId,
      cabinetName: savedCabinet?.cabinetName ?? cabinet.cabinetName,
      cabinetCode: savedCabinet?.cabinetCode ?? cabinet.cabinetCode,
      rows: savedCabinet?.rows ?? cabinet.rows,
      cols: savedCabinet?.cols ?? cabinet.cols,
      sortOrder: savedCabinet?.sortOrder ?? cabinet.sortOrder,
      status: savedCabinet?.status ?? cabinet.status,
      positionX: savedCabinet?.positionX ?? Math.max(cabinet.x - (area?.x ?? 0) - VISUAL_PADDING, 0),
      positionY: savedCabinet?.positionY ?? Math.max(cabinet.y - (area?.y ?? 0) - VISUAL_AREA_HEADER_HEIGHT, 0),
      createTime: savedCabinet?.createTime ?? '',
    } satisfies WmsCabinetVo
  })
  const bins = Object.values(model.bins).map(bin => ({
    id: bin.id,
    cabinetId: bin.cabinetId,
    binCode: bin.binCode,
    rowNum: bin.row,
    colNum: bin.col,
    binStatus: bin.status,
    createTime: '',
  }))

  return buildWarehouseVisualModel({
    warehouse,
    areas,
    cabinets,
    bins,
  })
}

function createCabinetNode(
  cabinet: WmsCabinetVo,
  cabinetBins: WmsBinVo[],
  areaX: number,
  areaY: number,
  index: number,
): WarehouseVisualCabinetNode {
  const gridRowCount = Math.max(cabinet.rows ?? 1, ...cabinetBins.map(bin => bin.rowNum), 1)
  const gridColCount = Math.max(cabinet.cols ?? 1, ...cabinetBins.map(bin => bin.colNum), 1)
  const cells = Array.from({ length: gridRowCount }, () => Array.from({ length: gridColCount }, () => null as WarehouseVisualBinNode | null))
  const binIds: EntityId[] = []

  for (const bin of cabinetBins) {
    const binNode: WarehouseVisualBinNode = {
      id: bin.id,
      cabinetId: bin.cabinetId,
      binCode: bin.binCode,
      row: bin.rowNum,
      col: bin.colNum,
      status: bin.binStatus,
    }
    binIds.push(bin.id)
    if (bin.rowNum > 0 && bin.colNum > 0) {
      cells[bin.rowNum - 1][bin.colNum - 1] = binNode
    }
  }

  return {
    id: cabinet.id,
    areaId: cabinet.areaId,
    cabinetName: cabinet.cabinetName,
    cabinetCode: cabinet.cabinetCode,
    sortOrder: cabinet.sortOrder,
    status: cabinet.status,
    rows: cabinet.rows,
    cols: cabinet.cols,
    x: areaX + VISUAL_PADDING + resolveCabinetOffsetX(cabinet, index),
    y: areaY + VISUAL_AREA_HEADER_HEIGHT + resolveCabinetOffsetY(cabinet),
    width: VISUAL_CABINET_WIDTH,
    height: VISUAL_CABINET_HEIGHT,
    gridRowCount,
    gridColCount,
    binIds,
    cells,
  }
}

function createViewport(contentWidth: number, contentHeight: number): WarehouseVisualViewport {
  return {
    width: Math.max(contentWidth + VISUAL_PADDING, 960),
    height: Math.max(contentHeight + VISUAL_PADDING * 2, 360),
    padding: VISUAL_PADDING,
    areaGap: VISUAL_AREA_GAP,
    cabinetGap: VISUAL_CABINET_GAP,
  }
}

function calculateCabinetContentWidth(cabinets: WmsCabinetVo[]): number {
  if (cabinets.length === 0) {
    return VISUAL_CABINET_WIDTH
  }

  return cabinets.reduce((maxWidth, cabinet, index) => {
    return Math.max(maxWidth, resolveCabinetOffsetX(cabinet, index) + VISUAL_CABINET_WIDTH)
  }, VISUAL_CABINET_WIDTH)
}

function calculateCabinetContentHeight(cabinets: WmsCabinetVo[]): number {
  if (cabinets.length === 0) {
    return VISUAL_CABINET_HEIGHT
  }

  return cabinets.reduce((maxHeight, cabinet) => {
    return Math.max(maxHeight, resolveCabinetOffsetY(cabinet) + VISUAL_CABINET_HEIGHT)
  }, VISUAL_CABINET_HEIGHT)
}

function resolveCabinetOffsetX(cabinet: WmsCabinetVo, index: number): number {
  return cabinet.positionX ?? index * (VISUAL_CABINET_WIDTH + VISUAL_CABINET_GAP)
}

function resolveCabinetOffsetY(cabinet: WmsCabinetVo): number {
  return cabinet.positionY ?? 0
}

function compareBySortOrder(left: Pick<WmsAreaVo, 'sortOrder' | 'id'>, right: Pick<WmsAreaVo, 'sortOrder' | 'id'>): number {
  return left.sortOrder - right.sortOrder || compareEntityId(left.id, right.id)
}

function compareCabinet(left: WmsCabinetVo, right: WmsCabinetVo): number {
  return left.sortOrder - right.sortOrder
    || (left.positionY ?? 0) - (right.positionY ?? 0)
    || (left.positionX ?? 0) - (right.positionX ?? 0)
    || compareEntityId(left.id, right.id)
}

function compareBin(left: WmsBinVo, right: WmsBinVo): number {
  return left.rowNum - right.rowNum || left.colNum - right.colNum || compareEntityId(left.id, right.id)
}

function normalizeCode(value: string): string {
  return value.trim().toUpperCase()
}

function compareEntityId(left: EntityId, right: EntityId): number {
  return left.localeCompare(right)
}
