<!--
  滑动拼图验证码（uni-app 版本）
  仅使用触摸事件（@touchstart/@touchmove.stop/@touchend），适配 PDA 设备
  使用 view/text/image 替代 HTML 标签
-->
<script>
import { aesEncrypt } from '../utils/ase'
import { resetSize } from '../utils/util'
import { reqGet, reqCheck } from '../api/index'

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
      setSize: {              // 实际渲染尺寸
        imgWidth: '310px',
        imgHeight: '155px',
        barWidth: '310px',
        barHeight: '40px',
      },
      top: 0,                 // 滑块在 Y 方向的偏移
      left: 0,                // 滑块在 X 方向的偏移
      moveBlockLeft: 0,       // 滑块拖动后的 left 值（px 数值）
      leftBarWidth: 0,        // 左侧进度条宽度（px 数值）
      // 拖动状态
      status: false,          // 是否正在拖动
      isEnd: false,           // 拖动是否已结束（校验后）
      startClientX: 0,        // 触摸开始时的 clientX
      startMoveBlockLeft: 0,  // 触摸开始时滑块的 left 值
      // UI 状态
      moveBlockBackgroundColor: '#337ab7',
      leftBarBorderColor: '#ddd',
      tipWords: '',
      // 垂直间距（拼图块相对拖动条的偏移）
      vSpace: 5,
      // 拖动条区域的位置信息（通过 uni.createSelectorQuery 获取）
      barAreaLeft: 0,
      barAreaWidth: 0,
    }
  },
  computed: {
    blockSizeInner() {
      return this.blockSize || { width: '50px', height: '50px' }
    },
  },
  mounted() {
    // 挂载时计算尺寸并获取验证码图片
    this.$nextTick(() => {
      this.setSize = resetSize(this)
      this.getPictrue()
    })
  },
  methods: {
    /** 刷新验证码（重置状态 + 重新请求） */
    refresh() {
      this.isEnd = false
      this.text = ''
      this.moveBlockLeft = 0
      this.leftBarWidth = 0
      this.moveBlockBackgroundColor = '#337ab7'
      this.leftBarBorderColor = '#ddd'
      this.tipWords = ''
      this.getPictrue()
    },

    /** 请求验证码图片 */
    getPictrue() {
      reqGet({ captchaType: this.captchaType }).then((res) => {
        if (res.repCode === '0000') {
          this.backImgBase = res.repData.originalImageBase64
          this.blockBackImgBase = res.repData.jigsawImageBase64
          this.backToken = res.repData.token
          this.secretKey = res.repData.secretKey
        } else {
          this.tipWords = res.repMsg
        }
      })
    },

    /**
     * 获取拖动条区域的位置信息
     * 使用 uni.createSelectorQuery 获取元素边界
     */
    getBarAreaRect() {
      return new Promise((resolve) => {
        const query = uni.createSelectorQuery().in(this)
        query.select('.verify-bar-area').boundingClientRect((rect) => {
          if (rect) {
            this.barAreaLeft = rect.left
            this.barAreaWidth = rect.width
          }
          resolve(rect)
        }).exec()
      })
    },

    /** 触摸开始 */
    async start(e) {
      if (this.isEnd) return
      // 获取拖动条区域位置
      await this.getBarAreaRect()
      const touch = e.touches[0]
      this.startClientX = touch.clientX
      this.startMoveBlockLeft = this.moveBlockLeft
      this.startMoveTime = Date.now()

      if (!this.isEnd) {
        this.text = ''
        this.moveBlockBackgroundColor = '#337ab7'
        this.leftBarBorderColor = '#337ab7'
        this.status = true
      }
    },

    /** 触摸移动 */
    move(e) {
      if (!this.status || this.isEnd) return
      const touch = e.touches[0]
      const deltaX = touch.clientX - this.startClientX
      let newLeft = this.startMoveBlockLeft + deltaX

      // 边界保护：不超过拖动条右边界
      const blockWidth = parseInt(this.blockSizeInner.width)
      const maxLeft = this.barAreaWidth - blockWidth / 2 - 2
      if (newLeft >= maxLeft) {
        newLeft = maxLeft
      }
      // 不小于左边界
      if (newLeft <= 0) {
        newLeft = 0
      }
      this.moveBlockLeft = newLeft
      this.leftBarWidth = newLeft
    },

    /** 触摸结束 */
    end() {
      if (!this.status || this.isEnd) return
      this.status = false

      // ★ 关键：把"浏览器实际像素"按比例换算回"原始 310x155" 坐标系
      const moveLeftDistance = (this.moveBlockLeft * 310) / parseInt(this.setSize.imgWidth)

      const data = {
        captchaType: this.captchaType,
        pointJson: this.secretKey
          ? aesEncrypt(JSON.stringify({ x: moveLeftDistance, y: 5.0 }), this.secretKey)
          : JSON.stringify({ x: moveLeftDistance, y: 5.0 }),
        token: this.backToken,
      }

      reqCheck(data).then((res) => {
        if (res.repCode === '0000') {
          // 校验成功
          this.isEnd = true
          this.moveBlockBackgroundColor = '#5cb85c'
          this.leftBarBorderColor = '#5cb85c'
          this.tipWords = '验证成功'
          // ★ 生成 captchaVerification，登录时回传
          const captchaVerification = this.secretKey
            ? aesEncrypt(
                `${this.backToken}---${JSON.stringify({ x: moveLeftDistance, y: 5.0 })}`,
                this.secretKey,
              )
            : `${this.backToken}---${JSON.stringify({ x: moveLeftDistance, y: 5.0 })}`

          setTimeout(() => {
            // 通知父组件关闭弹窗
            if (this.$parent && this.$parent.closeBox) {
              this.$parent.closeBox()
            }
            this.$emit('success', { captchaVerification })
          }, 1000)
        } else {
          // 校验失败 → 1s 后自动 refresh
          this.moveBlockBackgroundColor = '#d9534f'
          this.leftBarBorderColor = '#d9534f'
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
  <view style="position: relative">
    <!-- 背景大图（含缺口） -->
    <view
      class="verify-img-panel"
      :style="{
        width: setSize.imgWidth,
        height: setSize.imgHeight,
      }"
    >
      <image
        :src="'data:image/png;base64,' + backImgBase"
        mode="widthFix"
        class="verify-img"
      />
      <view class="verify-refresh" @click="refresh">
        <text class="verify-refresh-icon">↻</text>
      </view>
      <view class="verify-tips" v-if="tipWords">
        <text class="verify-tips-text">{{ tipWords }}</text>
      </view>
    </view>

    <!-- 拖动条区域 -->
    <view
      class="verify-bar-area"
      :style="{
        width: setSize.imgWidth,
        height: setSize.barHeight,
      }"
    >
      <text class="verify-msg" v-if="!isEnd && !status">{{ text || '向右滑动完成验证' }}</text>
      <view
        class="verify-left-bar"
        :style="{
          width: leftBarWidth + 'px',
          height: setSize.barHeight,
          borderColor: leftBarBorderColor,
          transition: status ? 'none' : 'width 0.3s',
        }"
      >
        <view
          class="verify-move-block"
          @touchstart="start"
          @touchmove.stop="move"
          @touchend="end"
          :style="{
            width: blockSizeInner.width,
            height: setSize.barHeight,
            backgroundColor: moveBlockBackgroundColor,
            left: moveBlockLeft + 'px',
            transition: status ? 'none' : 'left 0.3s',
          }"
        >
          <text class="verify-move-icon">{{ isEnd ? '✓' : '→' }}</text>
          <!-- 滑块小图（拼图块），浮在背景图上方 -->
          <view
            v-if="type === '2'"
            class="verify-sub-block"
            :style="{
              width: blockSizeInner.width,
              height: blockSizeInner.height,
              top: '-' + (parseInt(setSize.imgHeight) + vSpace) + 'px',
              left: moveBlockLeft + 'px',
            }"
          >
            <image
              :src="'data:image/png;base64,' + blockBackImgBase"
              mode="widthFix"
              class="verify-sub-img"
            />
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<style scoped>
.verify-img-panel {
  position: relative;
  box-sizing: border-box;
  border: 1px solid #ddd;
  border-radius: 3px;
  overflow: hidden;
}

.verify-img {
  width: 100%;
  height: 100%;
  display: block;
}

.verify-refresh {
  position: absolute;
  right: 5px;
  top: 5px;
  z-index: 2;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.3);
  border-radius: 3px;
}

.verify-refresh-icon {
  color: #fff;
  font-size: 16px;
}

.verify-tips {
  position: absolute;
  left: 0;
  bottom: 0;
  width: 100%;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.4);
}

.verify-tips-text {
  color: #fff;
  font-size: 12px;
}

.verify-bar-area {
  position: relative;
  text-align: center;
  border: 1px solid #ddd;
  border-top: 0;
  background: #f7f7f7;
  box-sizing: border-box;
  border-radius: 0 0 3px 3px;
  overflow: hidden;
}

.verify-msg {
  color: #999;
  font-size: 12px;
}

.verify-left-bar {
  position: absolute;
  top: 0;
  left: 0;
  border-right: 2px solid #ddd;
  background: #e0e0e0;
  box-sizing: border-box;
  z-index: 1;
}

.verify-move-block {
  position: absolute;
  top: 0;
  left: 0;
  text-align: center;
  box-sizing: border-box;
  z-index: 3;
  border-radius: 3px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.verify-move-icon {
  color: #fff;
  font-size: 18px;
}

.verify-sub-block {
  position: absolute;
  z-index: 2;
  box-shadow: 0 0 5px rgba(0, 0, 0, 0.3);
  overflow: hidden;
  border-radius: 3px;
}

.verify-sub-img {
  width: 100%;
  height: 100%;
  display: block;
}
</style>