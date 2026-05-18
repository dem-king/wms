<script setup lang="ts">
import { computed, nextTick } from 'vue'

import { preferences, updatePreferences } from '@/utils/preferences'

defineOptions({ name: 'ThemeToggle' })

const isDark = computed(() => preferences.theme.mode === 'dark')

/**
 * 切换主题，支持View Transition动画
 */
function toggleTheme(event: MouseEvent) {
  // @ts-ignore startViewTransition尚未被所有浏览器支持
  const isAppearanceTransition =
    typeof document.startViewTransition === 'function' &&
    !window.matchMedia('(prefers-reduced-motion: reduce)').matches

  if (!isAppearanceTransition || !event) {
    updatePreferences({
      theme: { mode: isDark.value ? 'light' : 'dark' },
    })
    return
  }

  const x = event.clientX
  const y = event.clientY
  const endRadius = Math.hypot(
    Math.max(x, innerWidth - x),
    Math.max(y, innerHeight - y),
  )

  // @ts-ignore startViewTransition API
  const transition = document.startViewTransition(async () => {
    updatePreferences({
      theme: { mode: isDark.value ? 'light' : 'dark' },
    })
    await nextTick()
  })

  transition.ready.then(() => {
    const clipPath = [
      `circle(0px at ${x}px ${y}px)`,
      `circle(${endRadius}px at ${x}px ${y}px)`,
    ]
    document.documentElement.animate(
      {
        clipPath: isDark.value ? [...clipPath].reverse() : clipPath,
      },
      {
        duration: 450,
        easing: 'ease-in',
        pseudoElement: isDark.value
          ? '::view-transition-old(root)'
          : '::view-transition-new(root)',
      },
    )
  })
}
</script>

<template>
  <button
    class="theme-toggle"
    :class="[isDark ? 'is-dark' : 'is-light']"
    type="button"
    @click.stop="toggleTheme"
  >
    <svg aria-hidden="true" height="20" viewBox="0 0 24 24" width="20" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
      <circle
        v-if="!isDark"
        cx="12" cy="12" r="5"
      />
      <g v-if="!isDark" class="theme-toggle__sun-beams">
        <line x1="12" x2="12" y1="1" y2="3" />
        <line x1="12" x2="12" y1="21" y2="23" />
        <line x1="4.22" x2="5.64" y1="4.22" y2="5.64" />
        <line x1="18.36" x2="19.78" y1="18.36" y2="19.78" />
        <line x1="1" x2="3" y1="12" y2="12" />
        <line x1="21" x2="23" y1="12" y2="12" />
        <line x1="4.22" x2="5.64" y1="19.78" y2="18.36" />
        <line x1="18.36" x2="19.78" y1="5.64" y2="4.22" />
      </g>
      <path v-if="isDark" d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z" />
    </svg>
  </button>
</template>

<style scoped>
.theme-toggle {
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 38px;
  height: 38px;
  border: 1px solid transparent;
  border-radius: 12px;
  background: transparent;
  color: var(--text-muted-foreground);
  transition:
    background-color 0.2s ease,
    border-color 0.2s ease,
    color 0.2s ease,
    box-shadow 0.2s ease;
  &:hover {
    color: var(--color-primary);
    background-color: hsl(var(--accent));
    border-color: hsl(var(--border));
    box-shadow: 0 8px 20px hsl(var(--primary) / 0.08);
  }
  &.is-dark {
    color: var(--text-muted-foreground);
    background: transparent;
    border-color: transparent;
  }
}
</style>
