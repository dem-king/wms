/** 内置主题类型 */
export type BuiltinThemeType =
  | 'custom'
  | 'deep-blue'
  | 'deep-green'
  | 'default'
  | 'gray'
  | 'green'
  | 'neutral'
  | 'orange'
  | 'pink'
  | 'rose'
  | 'sky-blue'
  | 'slate'
  | 'violet'
  | 'yellow'
  | 'zinc'

/** 内容紧凑类型 */
export type ContentCompactType = 'compact' | 'wide'

/** 头部模式类型 */
export type LayoutHeaderModeType = 'auto' | 'auto-scroll' | 'fixed' | 'static'

/** 布局类型 */
export type LayoutType =
  | 'full-content'
  | 'header-mixed-nav'
  | 'header-nav'
  | 'header-sidebar-nav'
  | 'mixed-nav'
  | 'sidebar-mixed-nav'
  | 'sidebar-nav'

/** 导航样式类型 */
export type NavigationStyleType = 'plain' | 'rounded'

/** 主题模式类型 */
export type ThemeModeType = 'auto' | 'dark' | 'light'

/** 应用偏好设置 */
export interface AppPreferences {
  accessMode: 'frontend' | 'backend'
  colorGrayMode: boolean
  colorWeakMode: boolean
  contentCompact: ContentCompactType
  contentCompactWidth: number
  contentPadding: number
  contentPaddingBottom: number
  contentPaddingLeft: number
  contentPaddingRight: number
  contentPaddingTop: number
  defaultHomePath: string
  dynamicTitle: boolean
  enablePreferences: boolean
  isMobile: boolean
  layout: LayoutType
  locale: string
  name: string
  preferencesButtonPosition: 'auto' | 'fixed' | 'header'
  watermark: boolean
  zIndex: number
}

/** 面包屑偏好设置 */
export interface BreadcrumbPreferences {
  enable: boolean
  hideOnlyOne: boolean
  showHome: boolean
  showIcon: boolean
  styleType: 'normal' | 'rounded'
}

/** 页脚偏好设置 */
export interface FooterPreferences {
  enable: boolean
  fixed: boolean
  height: number
}

/** 头部偏好设置 */
export interface HeaderPreferences {
  enable: boolean
  height: number
  hidden: boolean
  menuAlign: 'start' | 'center' | 'end'
  mode: LayoutHeaderModeType
}

/** Logo偏好设置 */
export interface LogoPreferences {
  enable: boolean
  source: string
}

/** 导航偏好设置 */
export interface NavigationPreferences {
  accordion: boolean
  split: boolean
  styleType: NavigationStyleType
}

/** 侧边栏偏好设置 */
export interface SidebarPreferences {
  autoActivateChild: boolean
  collapsed: boolean
  collapsedButton: boolean
  collapsedShowTitle: boolean
  collapseWidth: number
  enable: boolean
  expandOnHover: boolean
  extraCollapse: boolean
  extraCollapsedWidth: number
  fixedButton: boolean
  hidden: boolean
  mixedWidth: number
  width: number
}

/** 标签栏偏好设置 */
export interface TabbarPreferences {
  draggable: boolean
  enable: boolean
  height: number
  keepAlive: boolean
  maxCount: number
  showIcon: boolean
  showMaximize: boolean
  showMore: boolean
  styleType: 'chrome' | 'card' | 'plain'
  wheelable: boolean
}

/** 主题偏好设置 */
export interface ThemePreferences {
  builtinType: BuiltinThemeType
  colorDestructive: string
  colorPrimary: string
  colorSuccess: string
  colorWarning: string
  mode: ThemeModeType
  radius: string
  semiDarkHeader: boolean
  semiDarkSidebar: boolean
}

/** 过渡动画偏好设置 */
export interface TransitionPreferences {
  enable: boolean
  loading: boolean
  name: string
  progress: boolean
}

/** 小部件偏好设置 */
export interface WidgetPreferences {
  fullscreen: boolean
  globalSearch: boolean
  languageToggle: boolean
  lockScreen: boolean
  notification: boolean
  refresh: boolean
  sidebarToggle: boolean
  themeToggle: boolean
}

/** 完整偏好设置接口 */
export interface Preferences {
  app: AppPreferences
  breadcrumb: BreadcrumbPreferences
  footer: FooterPreferences
  header: HeaderPreferences
  logo: LogoPreferences
  navigation: NavigationPreferences
  sidebar: SidebarPreferences
  tabbar: TabbarPreferences
  theme: ThemePreferences
  transition: TransitionPreferences
  widget: WidgetPreferences
}

/** 深度部分类型 */
export type DeepPartial<T> = {
  [P in keyof T]?: T[P] extends object ? DeepPartial<T[P]> : T[P]
}

/** 初始化选项 */
export interface InitialOptions {
  namespace: string
  overrides?: DeepPartial<Preferences>
}

/** 内置主题预设项 */
export interface BuiltinThemePreset {
  color: string
  darkPrimaryColor?: string
  primaryColor?: string
  type: BuiltinThemeType
}
