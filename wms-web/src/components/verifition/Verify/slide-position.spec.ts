import { describe, expect, it } from 'vitest'

import {
  buildCaptchaImageState,
  formatVerifyDuration,
  getSlideMoveState,
  getSubBlockStyle,
  scaleMoveDistance,
} from './slide-position'

describe('slide captcha positioning', () => {
  it('keeps the puzzle image anchored inside the draggable handle', () => {
    expect(getSubBlockStyle({ blockY: 42, imgHeight: '155px', moveBlockLeft: '86px', vSpace: 5 }))
      .toEqual({
        height: '155px',
        left: '0px',
        top: '-160px',
      })
  })

  it('uses backend blockY or point.y when positioning the puzzle image vertically', () => {
    expect(buildCaptchaImageState({
      originalImageBase64: 'bg',
      jigsawImageBase64: 'block',
      token: 'token-1',
      secretKey: 'secret',
      point: { x: 0, y: 37 },
    })).toEqual({
      backgroundImage: 'bg',
      blockImage: 'block',
      token: 'token-1',
      secretKey: 'secret',
      blockY: 37,
    })

    expect(buildCaptchaImageState({
      backgroundImage: 'new-bg',
      blockImage: 'new-block',
      captchaToken: 'token-2',
      blockY: 45,
    })).toEqual({
      backgroundImage: 'new-bg',
      blockImage: 'new-block',
      token: 'token-2',
      secretKey: '',
      blockY: 45,
    })
  })

  it('calculates movement from the pointer without double-counting nested offsets', () => {
    expect(getSlideMoveState({
      pointerX: 186,
      barLeft: 100,
      startLeft: 12,
      barWidth: 310,
      blockWidth: 50,
    })).toEqual({
      moveBlockLeft: '74px',
      leftBarWidth: '74px',
    })
  })

  it('scales browser pixels back to the captcha coordinate system', () => {
    expect(scaleMoveDistance({ moveBlockLeft: '75px', renderedImgWidth: '300px' })).toBe(77.5)
  })

  it('formats successful verification duration for the slider bar', () => {
    expect(formatVerifyDuration(1000, 3210)).toBe('验证成功，用时 2.21s')
  })
})
