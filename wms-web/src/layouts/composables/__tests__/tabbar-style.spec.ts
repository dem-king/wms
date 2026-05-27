import { describe, expect, it } from 'vitest'

import { resolveTabbarStyleClass } from '../tabbar-style'

describe('resolveTabbarStyleClass', () => {
  it('maps chrome style to the chrome class name', () => {
    expect(resolveTabbarStyleClass('chrome')).toBe('tags-view--chrome')
  })

  it('maps card style to the card class name', () => {
    expect(resolveTabbarStyleClass('card')).toBe('tags-view--card')
  })

  it('maps plain style to the plain class name', () => {
    expect(resolveTabbarStyleClass('plain')).toBe('tags-view--plain')
  })

  it('falls back to chrome when style type is invalid', () => {
    expect(resolveTabbarStyleClass('unexpected-style')).toBe('tags-view--chrome')
  })
})
