import { describe, expect, it, vi } from 'vitest'
import {
  getLayoutElementList,
  addLayoutElement,
  updateLayoutElement,
  deleteLayoutElement,
  batchSaveLayoutElements,
  uploadWarehouseBackground,
  getWarehouseBackgroundUrl,
  deleteWarehouseBackground,
  updateAreaLayoutCoordinates,
} from '@/api/warehouse/layout-element'

// Mock the request module
vi.mock('@/api/request', () => ({
  get: vi.fn((url: string, params?: any) => Promise.resolve({ url, params })),
  post: vi.fn((url: string, data?: any, config?: any) => Promise.resolve({ url, data, config })),
  put: vi.fn((url: string, data?: any) => Promise.resolve({ url, data })),
  del: vi.fn((url: string) => Promise.resolve({ url })),
}))

/** 将 API 返回值断言为 mock 对象，以便访问 url/params/config 等测试属性 */
function asMock<T>(value: T): any {
  return value as any
}

describe('layout-element API', () => {
  it('getLayoutElementList should call GET /warehouse/layout-elements with warehouseId', async () => {
    const result = asMock(await getLayoutElementList('1'))
    expect(result.url).toBe('/warehouse/layout-elements')
    expect(result.params).toEqual({ warehouseId: '1' })
  })

  it('addLayoutElement should call POST /warehouse/layout-elements', async () => {
    const data = {
      warehouseId: '1',
      elementName: '墙体',
      elementType: 'wall' as const,
      shapeType: 'rect' as const,
      positionX: 0,
      positionY: 0,
      layoutWidth: 100,
      layoutHeight: 50,
      rotation: 0,
      pointData: '',
      styleData: '',
      labelText: '',
      sortOrder: 0,
    }
    const result = asMock(await addLayoutElement(data))
    expect(result.url).toBe('/warehouse/layout-elements')
    expect(result.data).toEqual(data)
  })

  it('updateLayoutElement should call PUT /warehouse/layout-elements/:id', async () => {
    const data = {
      warehouseId: '1',
      elementName: '更新墙体',
      elementType: 'wall' as const,
      shapeType: 'line' as const,
      positionX: 0,
      positionY: 0,
      layoutWidth: 100,
      layoutHeight: 50,
      rotation: 0,
      pointData: '',
      styleData: '',
      labelText: '',
      sortOrder: 0,
    }
    const result = asMock(await updateLayoutElement('123', data))
    expect(result.url).toBe('/warehouse/layout-elements/123')
    expect(result.data).toEqual(data)
  })

  it('deleteLayoutElement should call DELETE /warehouse/layout-elements/:id', async () => {
    const result = asMock(await deleteLayoutElement('123'))
    expect(result.url).toBe('/warehouse/layout-elements/123')
  })

  it('batchSaveLayoutElements should call POST /warehouse/layout-elements/batch-save', async () => {
    const data = {
      warehouseId: '1',
      created: [],
      updated: [],
      deletedIds: [],
    }
    const result = asMock(await batchSaveLayoutElements(data))
    expect(result.url).toBe('/warehouse/layout-elements/batch-save')
    expect(result.data).toEqual(data)
  })

  it('uploadWarehouseBackground should call POST with FormData and multipart headers', async () => {
    const file = new File(['test'], 'bg.png', { type: 'image/png' })
    const result = asMock(await uploadWarehouseBackground('1', file))
    expect(result.url).toBe('/warehouse/warehouses/1/background')
    expect(result.config?.headers).toEqual({ 'Content-Type': 'multipart/form-data' })
  })

  it('getWarehouseBackgroundUrl should return correct URL without version', () => {
    const url = getWarehouseBackgroundUrl('1')
    expect(url).toBe('/api/warehouse/warehouses/1/background')
  })

  it('getWarehouseBackgroundUrl should return URL with version parameter', () => {
    const url = getWarehouseBackgroundUrl('1', 'v2')
    expect(url).toBe('/api/warehouse/warehouses/1/background?v=v2')
  })

  it('deleteWarehouseBackground should call DELETE /warehouse/warehouses/:id/background', async () => {
    const result = asMock(await deleteWarehouseBackground('1'))
    expect(result.url).toBe('/warehouse/warehouses/1/background')
  })

  it('updateAreaLayoutCoordinates should call PUT /warehouse/areas/layout-coordinates', async () => {
    const data = { items: [{ id: '10', coordX: 100, coordY: 200 }] }
    const result = asMock(await updateAreaLayoutCoordinates(data))
    expect(result.url).toBe('/warehouse/areas/layout-coordinates')
    expect(result.data).toEqual(data)
  })
})
