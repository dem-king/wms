import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import { resolve } from 'path'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd())
  return {
    plugins: [
      vue(),
      AutoImport({
        resolvers: [ElementPlusResolver()],
        imports: ['vue', 'vue-router', 'pinia'],
        dts: 'src/auto-imports.d.ts'
      }),
      Components({
        resolvers: [ElementPlusResolver()],
        dts: 'src/components.d.ts'
      })
    ],
    resolve: {
      alias: {
        '@': resolve(__dirname, 'src')
      }
    },
    css: {
      preprocessorOptions: {
        scss: {
          api: 'modern-compiler',
          silenceDeprecations: ['legacy-js-api']
        }
      }
    },
    server: {
      port: 3000,
      proxy: {
        '/api': {
          target: env.VITE_API_BASE_URL || 'http://localhost:8080',
          changeOrigin: true
        }
      }
    },
    optimizeDeps: {
      include: [
        'three',
        'three/examples/jsm/controls/OrbitControls.js',
        'gsap',
        // 提前预构建库房可视化懒加载页的 Element Plus 子依赖，避免首次点击时二次优化导致动态导入失效。
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
      ],
    },
    build: {
      chunkSizeWarningLimit: 1000,
      rollupOptions: {
        onwarn(warning, warn) {
          if (warning.message.includes('contains an annotation that Rollup cannot interpret')) {
            return
          }
          warn(warning)
        },
        output: {
          manualChunks(id) {
            if (!id.includes('node_modules')) {
              return undefined
            }
            if (id.includes('element-plus') || id.includes('@element-plus')) {
              return 'element-plus'
            }
            if (id.includes('vue-router') || id.includes('pinia') || id.includes('/vue/')) {
              return 'vue-vendor'
            }
            if (id.includes('echarts')) {
              return 'echarts'
            }
            if (id.includes('konva') || id.includes('vue-konva')) {
              return 'konva'
            }
            if (id.includes('@zxing')) {
              return 'zxing'
            }
            return 'vendor'
          }
        }
      }
    }
  }
})
