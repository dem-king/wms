<script setup lang="ts">
import { computed, ref } from 'vue'

import {
  preferences,
  resetPreferences,
  updatePreferences,
  usePreferences,
} from '@/utils/preferences'
import { buildSidebarPreferenceState } from '@/layouts/composables/menu-layout'

import PreferenceThemeMode from './PreferenceThemeMode.vue'
import PreferenceBuiltinTheme from './PreferenceBuiltinTheme.vue'
import PreferenceLayout from './PreferenceLayout.vue'
import PreferenceSidebar from './PreferenceSidebar.vue'
import PreferenceHeader from './PreferenceHeader.vue'
import PreferenceFooter from './PreferenceFooter.vue'
import PreferenceTabbar from './PreferenceTabbar.vue'
import PreferenceWidget from './PreferenceWidget.vue'

defineOptions({ name: 'PreferencesDrawer' })

const activeTab = ref('appearance')
const { diffPreference, isDark } = usePreferences()

const open = computed({
  get() {
    return preferences.app.enablePreferences
  },
  set(val) {
    updatePreferences({ app: { enablePreferences: val } })
  },
})

function handleReset() {
  if (!diffPreference.value) return
  resetPreferences()
}

async function handleCopy() {
  const preferencesCopy = JSON.stringify(diffPreference.value)
  try {
    await navigator.clipboard.writeText(preferencesCopy)
  } catch {
    const textarea = document.createElement('textarea')
    textarea.value = preferencesCopy
    document.body.appendChild(textarea)
    textarea.select()
    document.execCommand('copy')
    document.body.removeChild(textarea)
  }
}

const themeAttrs = computed(() => ({
  themeMode: preferences.theme.mode,
  themeSemiDarkHeader: preferences.theme.semiDarkHeader,
  themeSemiDarkSidebar: preferences.theme.semiDarkSidebar,
  themeBuiltinType: preferences.theme.builtinType,
  themeColorPrimary: preferences.theme.colorPrimary,
}))

const themeListen = computed(() => ({
  'update:themeMode': (val: string) => updatePreferences({ theme: { mode: val as any } }),
  'update:themeSemiDarkHeader': (val: boolean) => updatePreferences({ theme: { semiDarkHeader: val } }),
  'update:themeSemiDarkSidebar': (val: boolean) => updatePreferences({ theme: { semiDarkSidebar: val } }),
  'update:themeBuiltinType': (val: string) => updatePreferences({ theme: { builtinType: val as any } }),
  'update:themeColorPrimary': (val: string) => updatePreferences({ theme: { colorPrimary: val } }),
}))

const layoutAttrs = computed(() => ({
  appLayout: preferences.app.layout,
}))

const layoutListen = computed(() => ({
  'update:appLayout': (val: string) => updatePreferences({ app: { layout: val as any } }),
}))

const sidebarAttrs = computed(() => ({
  sidebarEnable: preferences.sidebar.enable,
  sidebarWidth: preferences.sidebar.width,
  sidebarMixedWidth: preferences.sidebar.mixedWidth,
  sidebarCollapsed: preferences.sidebar.collapsed,
  sidebarCollapsedShowTitle: preferences.sidebar.collapsedShowTitle,
  sidebarExpandOnHover: preferences.sidebar.expandOnHover,
  sidebarCollapsedButton: preferences.sidebar.collapsedButton,
  sidebarFixedButton: preferences.sidebar.fixedButton,
  currentLayout: preferences.app.layout,
  sidebarPreferenceState: buildSidebarPreferenceState(
    preferences.app.layout,
    preferences.sidebar.enable,
    preferences.sidebar.collapsed,
  ),
  disabled: preferences.app.isMobile,
}))

const sidebarListen = computed(() => ({
  'update:sidebarEnable': (val: boolean) => updatePreferences({ sidebar: { enable: val } }),
  'update:sidebarWidth': (val: number) => updatePreferences({ sidebar: { width: val } }),
  'update:sidebarMixedWidth': (val: number) => updatePreferences({ sidebar: { mixedWidth: val } }),
  'update:sidebarCollapsed': (val: boolean) => updatePreferences({ sidebar: { collapsed: val } }),
  'update:sidebarCollapsedShowTitle': (val: boolean) => updatePreferences({ sidebar: { collapsedShowTitle: val } }),
  'update:sidebarExpandOnHover': (val: boolean) => updatePreferences({ sidebar: { expandOnHover: val } }),
  'update:sidebarCollapsedButton': (val: boolean) => updatePreferences({ sidebar: { collapsedButton: val } }),
  'update:sidebarFixedButton': (val: boolean) => updatePreferences({ sidebar: { fixedButton: val } }),
}))

const headerAttrs = computed(() => ({
  headerEnable: preferences.header.enable,
  headerHidden: preferences.header.hidden,
  headerMode: preferences.header.mode,
  headerMenuAlign: preferences.header.menuAlign,
  disabled: preferences.app.isMobile,
}))

const headerListen = computed(() => ({
  'update:headerEnable': (val: boolean) => updatePreferences({ header: { enable: val } }),
  'update:headerHidden': (val: boolean) => updatePreferences({ header: { hidden: val } }),
  'update:headerMode': (val: string) => updatePreferences({ header: { mode: val as any } }),
  'update:headerMenuAlign': (val: string) => updatePreferences({ header: { menuAlign: val as any } }),
}))

const footerAttrs = computed(() => ({
  footerEnable: preferences.footer.enable,
  footerFixed: preferences.footer.fixed,
  footerHeight: preferences.footer.height,
}))

const footerListen = computed(() => ({
  'update:footerEnable': (val: boolean) => updatePreferences({ footer: { enable: val } }),
  'update:footerFixed': (val: boolean) => updatePreferences({ footer: { fixed: val } }),
  'update:footerHeight': (val: number) => updatePreferences({ footer: { height: val } }),
}))

const tabbarAttrs = computed(() => ({
  tabbarEnable: preferences.tabbar.enable,
  tabbarHeight: preferences.tabbar.height,
  tabbarStyleType: preferences.tabbar.styleType,
  tabbarShowMore: preferences.tabbar.showMore,
  tabbarShowMaximize: preferences.tabbar.showMaximize,
  tabbarDraggable: preferences.tabbar.draggable,
}))

const tabbarListen = computed(() => ({
  'update:tabbarEnable': (val: boolean) => updatePreferences({ tabbar: { enable: val } }),
  'update:tabbarHeight': (val: number) => updatePreferences({ tabbar: { height: val } }),
  'update:tabbarStyleType': (val: string) => updatePreferences({ tabbar: { styleType: val as any } }),
  'update:tabbarShowMore': (val: boolean) => updatePreferences({ tabbar: { showMore: val } }),
  'update:tabbarShowMaximize': (val: boolean) => updatePreferences({ tabbar: { showMaximize: val } }),
  'update:tabbarDraggable': (val: boolean) => updatePreferences({ tabbar: { draggable: val } }),
}))

const widgetAttrs = computed(() => ({
  widgetThemeToggle: preferences.widget.themeToggle,
  widgetFullscreen: preferences.widget.fullscreen,
  widgetRefresh: preferences.widget.refresh,
  widgetSidebarToggle: preferences.widget.sidebarToggle,
  appPreferencesButtonPosition: preferences.app.preferencesButtonPosition,
}))

const widgetListen = computed(() => ({
  'update:widgetThemeToggle': (val: boolean) => updatePreferences({ widget: { themeToggle: val } }),
  'update:widgetFullscreen': (val: boolean) => updatePreferences({ widget: { fullscreen: val } }),
  'update:widgetRefresh': (val: boolean) => updatePreferences({ widget: { refresh: val } }),
  'update:widgetSidebarToggle': (val: boolean) => updatePreferences({ widget: { sidebarToggle: val } }),
  'update:appPreferencesButtonPosition': (val: string) =>
    updatePreferences({ app: { preferencesButtonPosition: val as any } }),
}))
</script>

<template>
  <el-drawer
    v-model="open"
    title="偏好设置"
    direction="rtl"
    size="420px"
    :z-index="preferences.app.zIndex"
    class="preferences-drawer"
  >
    <div class="drawer-intro">
      <div class="intro-title">后台外观与布局</div>
      <div class="intro-description">按参考项目风格自由调整主题、布局、标签页和顶部工具区。</div>
    </div>

    <el-tabs v-model="activeTab" class="drawer-tabs">
      <el-tab-pane label="外观" name="appearance">
        <PreferenceThemeMode
          v-bind="themeAttrs"
          v-on="themeListen"
        />
        <PreferenceBuiltinTheme
          v-bind="{ themeBuiltinType: themeAttrs.themeBuiltinType, themeColorPrimary: themeAttrs.themeColorPrimary, isDark }"
          v-on="{ 'update:themeBuiltinType': themeListen['update:themeBuiltinType'], 'update:themeColorPrimary': themeListen['update:themeColorPrimary'] }"
        />
      </el-tab-pane>

      <el-tab-pane label="布局" name="layout">
        <PreferenceLayout
          v-bind="layoutAttrs"
          v-on="layoutListen"
        />
        <PreferenceSidebar
          v-bind="sidebarAttrs"
          v-on="sidebarListen"
        />
        <PreferenceHeader
          v-bind="headerAttrs"
          v-on="headerListen"
        />
        <PreferenceFooter
          v-bind="footerAttrs"
          v-on="footerListen"
        />
      </el-tab-pane>

      <el-tab-pane label="组件" name="widgets">
        <PreferenceTabbar
          v-bind="tabbarAttrs"
          v-on="tabbarListen"
        />
        <PreferenceWidget
          v-bind="widgetAttrs"
          v-on="widgetListen"
        />
      </el-tab-pane>
    </el-tabs>

    <template #footer>
      <div class="drawer-footer">
        <el-button size="small" round @click="handleCopy">复制配置</el-button>
        <el-button size="small" round type="primary" :disabled="!diffPreference" @click="handleReset">
          重置
        </el-button>
      </div>
    </template>
  </el-drawer>
</template>

<style lang="scss" scoped>
.drawer-intro {
  margin-bottom: 14px;
  padding: 14px 16px;
  border: 1px solid hsl(var(--border));
  border-radius: 18px;
  background: linear-gradient(135deg, hsl(var(--primary) / 0.1), hsl(var(--card)));
}

.intro-title {
  color: var(--text-foreground);
  font-size: 15px;
  font-weight: 700;
}

.intro-description {
  margin-top: 4px;
  color: var(--text-muted-foreground);
  font-size: 12px;
  line-height: 1.5;
}

.drawer-tabs {
  :deep(.el-tabs__nav-wrap::after) {
    display: none;
  }
}

.drawer-footer {
  display: flex;
  width: 100%;
  justify-content: space-between;
}
</style>
