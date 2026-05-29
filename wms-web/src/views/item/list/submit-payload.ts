import type { WmsItemDto, WmsItemSubmitDto } from '@/types/item'

export function buildItemSubmitPayload(form: WmsItemDto & { binIds?: Array<string | number> }): WmsItemSubmitDto {
  return {
    itemName: form.itemName,
    model: form.specModel,
    spec: form.specModel,
    unit: form.unit,
    categoryId: form.categoryId,
    subCategoryId: form.subCategoryId,
    tagIds: form.tagIds,
    binIds: form.binIds,
    supplierId: form.supplierId,
    stockLowerLimit: form.stockLowerLimit,
    stockUpperLimit: form.stockUpperLimit,
    replenishThreshold: form.replenishThreshold,
    status: form.status,
  }
}
