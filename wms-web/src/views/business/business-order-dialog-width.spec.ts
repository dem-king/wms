import { describe, expect, it } from 'vitest'
import { readFileSync } from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'

const currentDir = path.dirname(fileURLToPath(import.meta.url))

const orderFormFiles = [
  'inbound/components/InboundForm.vue',
  'outbound/components/OutboundForm.vue',
  'transfer/components/TransferForm.vue',
  'return/components/ReturnForm.vue',
  'scrap/components/ScrapForm.vue',
]

const expectedDialogWidth = 'min(1280px, calc(100vw - 48px))'

describe('business order dialog layout', () => {
  it('uses a wide responsive dialog for order detail entry tables', () => {
    orderFormFiles.forEach((file) => {
      const source = readFileSync(path.resolve(currentDir, file), 'utf-8')

      expect(source).toContain(`width="${expectedDialogWidth}"`)
      expect(source).not.toContain('width="900px"')
    })
  })
})
