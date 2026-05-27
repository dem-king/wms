import type { TabbarPreferences } from '@/types/preferences'

/**
 * 解析标签栏样式对应的根节点类名，非法值回退到默认 chrome 风格。
 */
export function resolveTabbarStyleClass(
  styleType: TabbarPreferences['styleType'] | string,
): string {
  switch (styleType) {
    case 'card':
      return 'tags-view--card'
    case 'plain':
      return 'tags-view--plain'
    case 'chrome':
    default:
      return 'tags-view--chrome'
  }
}
