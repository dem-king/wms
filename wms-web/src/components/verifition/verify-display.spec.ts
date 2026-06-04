import { readFileSync } from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'
import { describe, expect, it } from 'vitest'

const currentDir = path.dirname(fileURLToPath(import.meta.url))
const verifySource = readFileSync(path.resolve(currentDir, 'index.vue'), 'utf-8')
const verifySlideSource = readFileSync(path.resolve(currentDir, 'Verify/VerifySlide.vue'), 'utf-8')
const loginSource = readFileSync(path.resolve(currentDir, '../../views/login/index.vue'), 'utf-8')

describe('aryn captcha display contract', () => {
  it('uses the reference project popup shell for captcha display', () => {
    expect(verifySource).toContain('background: rgb(0 0 0 / 30%)')
    expect(verifySource).toContain('transform: translate(-50%, -50%)')
    expect(verifySource).toContain('border: 1px solid #e4e7eb')
    expect(verifySource).toContain('height: 50px')
    expect(verifySource).toContain('line-height: 50px')
  })

  it('opens the aryn captcha popup before submitting login and keeps the reference image size', () => {
    expect(loginSource).toContain('verifyRef.value?.show()')
    expect(loginSource).toContain('@success="verifySuccess"')
    expect(loginSource).toContain("const captchaType = ref('blockPuzzle')")
    expect(loginSource).toContain(":img-size=\"{ width: '310px', height: '155px' }\"")
  })

  it('allows the draggable puzzle block to float over the captcha image like the reference project', () => {
    expect(verifySlideSource).toContain('class="verify-img-out"')
    expect(verifySlideSource).toContain('parseInt(setSize.imgHeight) + vSpace')

    const barAreaStyle = verifySlideSource.match(/\.verify-bar-area\s*\{[\s\S]*?\}/)?.[0] || ''
    expect(barAreaStyle).not.toContain('overflow: hidden')
  })
})
