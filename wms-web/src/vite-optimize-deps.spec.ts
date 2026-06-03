import { describe, expect, it } from 'vitest'
import viteConfig from '../vite.config'

describe('vite optimizeDeps', () => {
  it('pre-bundles warehouse visual lazy-route dependencies', () => {
    const resolvedConfig = typeof viteConfig === 'function'
      ? viteConfig({ command: 'serve', mode: 'development', isSsrBuild: false, isPreview: false })
      : viteConfig

    const optimizeDepsInclude = resolvedConfig.optimizeDeps?.include ?? []

    expect(optimizeDepsInclude).toEqual(expect.arrayContaining([
      'three',
      'three/examples/jsm/controls/OrbitControls.js',
      'gsap',
      'element-plus/es/components/empty/style/css',
      'element-plus/es/components/alert/style/css',
      'element-plus/es/components/progress/style/css',
      'element-plus/es/components/card/style/css',
      'element-plus/es/components/select/style/css',
      'element-plus/es/components/option/style/css',
      'element-plus/es/components/radio-group/style/css',
      'element-plus/es/components/radio-button/style/css',
      'element-plus/es/components/button-group/style/css',
      'element-plus/es/components/slider/style/css',
      'element-plus/es/components/switch/style/css',
      'element-plus/es/components/loading/style/css',
      'element-plus/es/components/tooltip/style/css',
    ]))
  })
})
