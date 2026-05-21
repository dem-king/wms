import type { WmsWarehouseDto } from '@/types/warehouse'

export function buildWarehouseSubmitPayload(form: WmsWarehouseDto, isEdit: boolean): WmsWarehouseDto {
  const payload: WmsWarehouseDto = {
    warehouseName: form.warehouseName,
    address: form.address,
    manager: form.manager,
    phone: form.phone,
    area: form.area,
    status: form.status,
    remark: form.remark,
  }

  if (isEdit) {
    payload.warehouseCode = form.warehouseCode
  }

  return payload
}
