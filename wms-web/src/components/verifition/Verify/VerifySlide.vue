<script>
import { aesEncrypt } from '../utils/ase'
import { resetSize } from '../utils/util'
import { reqGet, reqCheck } from '../api/index'
import {
  buildCaptchaImageState,
  formatVerifyDuration,
  getSlideMoveState,
  getSubBlockStyle,
  scaleMoveDistance,
} from './slide-position'

export default {
  name: 'VerifySlide',
  props: {
    captchaType: { type: String, required: true },
    type: { type: String, default: '2' },
    imgSize: {
      type: Object,
      default: () => ({ width: '310px', height: '155px' }),
    },
    blockSize: {
      type: Object,
      default: () => ({ width: '50px', height: '50px' }),
    },
    barSize: {
      type: Object,
      default: () => ({ width: '310px', height: '40px' }),
    },
  },
  data() {
    return {
      secretKey: '',          // AES 密钥，由后端 /auth/code/get 返回
      backImgBase: '',        // 背景图 Base64（含缺口）
      blockBackImgBase: '',   // 滑块图 Base64
      backToken: '',          // 验证码 token
      text: '',               // 拖动条提示文字
      finishText: '',         // 验证成功后的滑动条提示文字
      setSize: {              // 实际渲染尺寸
        imgWidth: '310px',
        imgHeight: '155px',
        barWidth: '310px',
        barHeight: '40px',
      },
      blockY: 5,              // 拼图块在背景图中的 Y 位置
      left: 0,                // 滑块在 X 方向的偏移
      moveBlockLeft: '0px',   // 滑块拖动后的 left 值
      leftBarWidth: undefined, // 左侧进度条宽度
      // 拖动状态
      status: false,          // 是否正在拖动
      isEnd: false,           // 拖动是否已结束（校验后）
      startLeft: 0,           // 按下时鼠标与滑块左边缘的偏移
      startMoveTime: 0,       // 开始拖动时间
      // UI 状态
      moveBlockBackgroundColor: '#fff',
      leftBarBorderColor: '#ddd',
      iconClass: 'icon-right',
      iconColor: '#000',
      tipWords: '',
      // 垂直间距（拼图块相对拖动条的偏移）
      vSpace: 5,
    }
  },
  computed: {
    blockSizeInner() {
      return this.blockSize || { width: '50px', height: '50px' }
    },
    subBlockStyle() {
      const position = getSubBlockStyle({
        blockY: this.blockY,
        imgHeight: this.setSize.imgHeight,
        moveBlockLeft: this.moveBlockLeft,
        vSpace: this.vSpace,
      })

      return {
        width: `${Math.floor((parseInt(this.setSize.imgWidth) * 47) / 310)}px`,
        height: position.height,
        top: position.top,
        left: position.left,
      }
    },
  },
  mounted() {
    // 挂载时计算尺寸并获取验证码图片
    this.$nextTick(() => {
      this.setSize = resetSize(this)
      this.getPictrue()
    })
    // 监听窗口 resize
    window.addEventListener('resize', this.onResize)
  },
  beforeUnmount() {
    window.removeEventListener('resize', this.onResize)
    // 移除全局 mouse/touch 事件
    document.removeEventListener('mousemove', this.move)
    document.removeEventListener('mouseup', this.end)
    document.removeEventListener('touchmove', this.move)
    document.removeEventListener('touchend', this.end)
  },
  methods: {
    /** 窗口 resize 时重新计算尺寸 */
    onResize() {
      this.setSize = resetSize(this)
    },

    /** 刷新验证码（重置状态 + 重新请求） */
    refresh() {
      this.isEnd = false
      this.text = ''
      this.finishText = ''
      this.moveBlockLeft = '0px'
      this.leftBarWidth = undefined
      this.moveBlockBackgroundColor = '#fff'
      this.leftBarBorderColor = '#ddd'
      this.iconClass = 'icon-right'
      this.iconColor = '#000'
      this.tipWords = ''
      this.getPictrue()
    },

    /** 请求验证码图片 */
    getPictrue() {
      reqGet({ captchaType: this.captchaType }).then((res) => {
        if (res.repCode === '0000') {
          const imageState = buildCaptchaImageState(res.repData || {})
          this.backImgBase = imageState.backgroundImage
          this.blockBackImgBase = imageState.blockImage
          this.backToken = imageState.token
          this.secretKey = imageState.secretKey
          this.blockY = imageState.blockY
        } else {
          this.tipWords = res.repMsg
        }
      })
    },

    /** 拖动开始（mousedown / touchstart） */
    start(e) {
      if (this.isEnd) return
      e = e || window.event
      const x = e.touches ? e.touches[0].pageX : e.clientX
      // 获取拖动条区域的位置
      const barArea = this.$refs.barArea
      if (!barArea) return
      this.startLeft = Math.floor(x - barArea.getBoundingClientRect().left)
      this.startMoveTime = Date.now()
      if (!this.isEnd) {
        this.text = ''
        this.finishText = ''
        this.moveBlockBackgroundColor = '#337ab7'
        this.leftBarBorderColor = '#337ab7'
        this.iconClass = 'icon-right'
        this.iconColor = '#fff'
        this.status = true
      }
      // 绑定全局 mouse/touch 事件
      document.addEventListener('mousemove', this.move)
      document.addEventListener('mouseup', this.end)
      document.addEventListener('touchmove', this.move)
      document.addEventListener('touchend', this.end)
    },

    /** 拖动中（mousemove / touchmove） */
    move(e) {
      if (!this.status || this.isEnd) return
      e = e || window.event
      const x = e.touches ? e.touches[0].pageX : e.clientX
      const barArea = this.$refs.barArea
      if (!barArea) return
      const barLeft = barArea.getBoundingClientRect().left
      const moveState = getSlideMoveState({
        pointerX: x,
        barLeft,
        startLeft: this.startLeft,
        barWidth: barArea.offsetWidth,
        blockWidth: parseInt(this.blockSizeInner.width),
      })
      this.moveBlockLeft = moveState.moveBlockLeft
      this.leftBarWidth = moveState.leftBarWidth
    },

    /** 拖动结束（mouseup / touchend） */
    end() {
      if (!this.status || this.isEnd) return
      const endMoveTime = Date.now()
      this.status = false
      // 移除全局事件
      document.removeEventListener('mousemove', this.move)
      document.removeEventListener('mouseup', this.end)
      document.removeEventListener('touchmove', this.move)
      document.removeEventListener('touchend', this.end)

      // ★ 关键：把"浏览器实际像素"按比例换算回"原始 310x155" 坐标系
      const moveLeftDistance = scaleMoveDistance({
        moveBlockLeft: this.moveBlockLeft,
        renderedImgWidth: this.setSize.imgWidth,
      })

      const data = {
        captchaType: this.captchaType,
        pointJson: this.secretKey
          ? aesEncrypt(JSON.stringify({ x: moveLeftDistance, y: this.blockY }), this.secretKey)
          : JSON.stringify({ x: moveLeftDistance, y: this.blockY }),
        token: this.backToken,
      }

      reqCheck(data).then((res) => {
        if (res.repCode === '0000') {
          // 校验成功
          this.isEnd = true
          this.moveBlockBackgroundColor = '#5cb85c'
          this.leftBarBorderColor = '#5cb85c'
          this.iconClass = 'icon-check'
          this.iconColor = '#fff'
          this.finishText = formatVerifyDuration(this.startMoveTime, endMoveTime)
          this.tipWords = this.finishText
          // ★ 生成 captchaVerification，登录时回传
          const captchaVerification = this.secretKey
            ? aesEncrypt(
                `${this.backToken}---${JSON.stringify({ x: moveLeftDistance, y: this.blockY })}`,
                this.secretKey,
              )
            : `${this.backToken}---${JSON.stringify({ x: moveLeftDistance, y: this.blockY })}`

          setTimeout(() => {
            if (this.$parent && this.$parent.closeBox) {
              this.$parent.closeBox()
            }
            this.$emit('success', { captchaVerification })
          }, 1000)
        } else {
          // 校验失败 → 1s 后自动 refresh
          this.moveBlockBackgroundColor = '#d9534f'
          this.leftBarBorderColor = '#d9534f'
          this.iconClass = 'icon-close'
          this.iconColor = '#fff'
          this.tipWords = res.repMsg || '验证失败'
          this.$emit('error', this)
          setTimeout(() => this.refresh(), 1000)
        }
      })
    },
  },
}
</script>

<template>
  <div style="position: relative">
    <!-- 背景大图（含缺口） -->
    <div
      v-if="type === '2'"
      class="verify-img-out"
      :style="{ height: `${parseInt(setSize.imgHeight) + vSpace}px` }"
    >
      <div
        class="verify-img-panel"
        :style="{
          width: setSize.imgWidth,
          height: setSize.imgHeight,
          backgroundSize: `${setSize.imgWidth} ${setSize.imgHeight}`,
        }"
      >
        <img
          :src="`data:image/png;base64,${backImgBase}`"
          alt=""
          style="width: 100%; height: 100%; display: block"
        />
        <div class="verify-refresh" @click="refresh">
          <i class="iconfont icon-refresh"></i>
        </div>
        <div class="verify-tips" v-if="tipWords">{{ tipWords }}</div>
      </div>
    </div>

    <!-- 拖动条区域 -->
    <div
      class="verify-bar-area"
      ref="barArea"
      :style="{
        width: setSize.imgWidth,
        height: setSize.barHeight,
        lineHeight: setSize.barHeight,
      }"
    >
      <span class="verify-msg" v-if="!isEnd && !status">{{ text || '向右滑动完成验证' }}</span>
      <span class="verify-msg verify-finish-msg" v-if="isEnd">{{ finishText }}</span>
      <div
        class="verify-left-bar"
        :style="{
          width: leftBarWidth !== undefined ? leftBarWidth : setSize.barHeight,
          height: setSize.barHeight,
          borderColor: leftBarBorderColor,
          transition: status ? 'none' : 'width 0.3s',
        }"
      >
        <div
          class="verify-move-block"
          @touchstart="start"
          @mousedown="start"
          :style="{
            width: setSize.barHeight,
            height: setSize.barHeight,
            backgroundColor: moveBlockBackgroundColor,
            left: moveBlockLeft,
            transition: status ? 'none' : 'left 0.3s',
          }"
        >
          <i :class="['iconfont', iconClass]" :style="{ color: iconColor }"></i>
          <!-- 滑块小图（拼图块），浮在背景图上方 -->
          <div
            v-if="type === '2'"
            class="verify-sub-block"
            :style="subBlockStyle"
          >
            <img
              :src="`data:image/png;base64,${blockBackImgBase}`"
              alt=""
              style="width: 100%; height: 100%; display: block"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.verify-img-out {
  position: relative;
}

.verify-img-panel {
  position: relative;
  box-sizing: content-box;
  margin: 0;
  border-top: 1px solid #ddd;
  border-bottom: 1px solid #ddd;
  border-radius: 3px;
  overflow: hidden;
}

.verify-img-panel img {
  width: 100%;
  height: 100%;
  display: block;
}

.verify-refresh {
  position: absolute;
  right: 0;
  top: 0;
  cursor: pointer;
  font-size: 20px;
  z-index: 2;
  color: #fff;
  width: 25px;
  height: 25px;
  line-height: 25px;
  text-align: center;
  padding: 5px;
}

.verify-refresh:hover {
  background: rgba(0, 0, 0, 0.5);
}

.verify-tips {
  position: absolute;
  left: 0;
  bottom: 0;
  width: 100%;
  height: 30px;
  line-height: 30px;
  text-align: center;
  color: #fff;
  font-size: 12px;
  background: rgba(0, 0, 0, 0.4);
}

.verify-bar-area {
  position: relative;
  box-sizing: content-box;
  text-align: center;
  border: 1px solid #ddd;
  background: #fff;
  border-radius: 4px;
}

.verify-msg {
  color: #999;
  font-size: 12px;
  user-select: none;
  -webkit-user-select: none;
}

.verify-finish-msg {
  position: absolute;
  inset: 0;
  z-index: 4;
  color: #5cb85c;
  font-weight: 500;
  text-shadow: 0 1px 0 #fff;
  pointer-events: none;
}

.verify-left-bar {
  position: absolute;
  top: -1px;
  left: -1px;
  box-sizing: content-box;
  cursor: pointer;
  background: #f0fff0;
  border: 1px solid #ddd;
  z-index: 1;
}

.verify-move-block {
  position: absolute;
  top: 0;
  left: 0;
  cursor: pointer;
  text-align: center;
  box-sizing: content-box;
  z-index: 3;
  border-radius: 1px;
  box-shadow: 0 0 2px #888;
  display: flex;
  align-items: center;
  justify-content: center;
}

.verify-move-block:hover {
  color: #fff;
  background-color: #337ab7 !important;
}

.verify-move-block .iconfont {
  font-size: 18px;
}

.icon-right::before {
  content: '>';
}

.icon-check::before {
  content: 'v';
}

.icon-close::before {
  content: 'x';
}

.verify-sub-block {
  position: absolute;
  z-index: 3;
  text-align: center;
}

.verify-sub-block img {
  width: 100%;
  height: 100%;
  display: block;
  -webkit-user-drag: none;
  filter: drop-shadow(0 0 5px rgba(0, 0, 0, 0.35));
}
</style>
