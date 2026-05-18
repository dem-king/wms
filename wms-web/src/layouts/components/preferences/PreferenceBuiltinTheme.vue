<script setup lang="ts">
import type { BuiltinThemePreset, BuiltinThemeType } from '@/types/preferences'

import { computed, ref, watch } from 'vue'

import { BUILT_IN_THEME_PRESETS } from '@/utils/preferences/constants'
import { convertToHsl, TinyColor } from '@/utils/preferences/color-convert'
import { useThrottleFn } from '@vueuse/core'

const props = defineProps<{ isDark: boolean }>()

const colorInput = ref<HTMLInputElement[]>()
const themeBuiltinType = defineModel<BuiltinThemeType>('themeBuiltinType', { default: 'default' })
const themeColorPrimary = defineModel<string>('themeColorPrimary')

const updateThemeColorPrimary = useThrottleFn(
  (value: string) => {
    themeColorPrimary.value = value
  },
  300,
  true,
  true,
)

const inputValue = computed(() => {
  return new TinyColor(themeColorPrimary.value || '').toHexString()
})

const builtinThemePresets = computed(() => {
  return [...BUILT_IN_THEME_PRESETS]
})

/** 主题类型中文标签映射 */
const themeLabels: Record<string, string> = {
  default: '默认',
  violet: '紫罗兰',
  pink: '粉色',
  yellow: '黄色',
  'sky-blue': '天空蓝',
  green: '绿色',
  zinc: '锌灰',
  'deep-green': '深绿',
  'deep-blue': '深蓝',
  orange: '橙色',
  rose: '玫红',
  neutral: '中性',
  slate: '石板灰',
  gray: '灰色',
  custom: '自定义',
}

function handleSelect(theme: BuiltinThemePreset) {
  themeBuiltinType.value = theme.type
}

function handleInputChange(e: Event) {
  const target = e.target as HTMLInputElement
  updateThemeColorPrimary(convertToHsl(target.value))
}

function selectColor() {
  colorInput.value?.[0]?.click?.()
}

watch(
  () => [themeBuiltinType.value, props.isDark] as [BuiltinThemeType, boolean],
  ([themeType, isDark]) => {
    const theme = builtinThemePresets.value.find(
      (item) => item.type === themeType,
    )
    if (theme) {
      const primaryColor = isDark
        ? theme.darkPrimaryColor || theme.primaryColor
        : theme.primaryColor
      themeColorPrimary.value = primaryColor || theme.color
    }
  },
)
</script>

<template>
  <div class="preference-section">
    <div class="section-title">内置主题</div>
    <div class="builtin-theme-list">
      <div
        v-for="theme in builtinThemePresets"
        :key="theme.type"
        class="builtin-theme-item"
        @click="handleSelect(theme)"
      >
        <div
          class="theme-color-box"
          :class="{ active: theme.type === themeBuiltinType }"
        >
          <template v-if="theme.type !== 'custom'">
            <div
              class="color-dot"
              :style="{ backgroundColor: theme.color }"
            ></div>
          </template>
          <template v-else>
            <div class="custom-color-area" @click.stop="selectColor">
              <el-icon :size="16" class="custom-icon"><Edit /></el-icon>
              <input
                ref="colorInput"
                :value="inputValue"
                class="color-input-hidden"
                type="color"
                @input="handleInputChange"
              />
            </div>
          </template>
        </div>
        <div class="theme-label">{{ themeLabels[theme.type] || theme.type }}</div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.preference-section {
  margin-bottom: 20px;
}
.section-title {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 12px;
  color: var(--text-foreground);
}
.builtin-theme-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}
.builtin-theme-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
}
.theme-color-box {
  width: 40px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid var(--color-border);
  border-radius: 6px;
  margin-bottom: 4px;
  transition: all 0.2s;
  &.active {
    border-color: var(--color-primary);
    box-shadow: 0 0 0 2px hsl(var(--primary) / 20%);
  }
  &:hover {
    border-color: var(--color-primary);
  }
}
.color-dot {
  width: 20px;
  height: 20px;
  border-radius: 4px;
}
.custom-color-area {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.custom-icon {
  opacity: 0.6;
}
.color-input-hidden {
  position: absolute;
  inset: 0;
  opacity: 0;
  width: 100%;
  height: 100%;
  cursor: pointer;
}
.theme-label {
  font-size: 11px;
  color: var(--text-muted-foreground);
  text-align: center;
}
</style>
