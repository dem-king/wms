<script>
import { aesEncrypt } from '../utils/ase'
import { resetSize } from '../utils/util'
import { reqGet, reqCheck } from '../api/index'

export default {
  name: 'VerifyPoints',
  props: {
    captchaType: { type: String, required: true },
    imgSize: {
      type: Object,
      default: () => ({ width: '310px', height: '155px' }),
    },
    barSize: {
      type: Object,
      default: () => ({ width: '310px', height: '40px' }),
    },
  },
  data() {
    return {
      secretKey: '',            // AES 密钥，由后端 /auth/code/get 返回
      pointBackImgBase: '',     // 背景图 Base64
      backToken: '',            // 验证码 token
      wordList: [],             // 需要点击的文字列表，如 ['汉', '字', '码']
      setSize: {                // 实际渲染尺寸
        imgWidth: '310px',
        imgHeight: '155px',
        barWidth: '310px',
        barHeight: '40px',
      },
      // 点击状态
      checkPosArr: [],          // 用户点击坐标数组 [{x, y}, ...]
      tempPoints: [],           // 临时标记点（用于渲染编号圆点）
      num: 1,                   // 当前点击序号
      checkNum: 3,              // 需要点击的总数（默认 3 个字）
      bindingClick: true,       // 是否允许点击
      tipWords: '',             // 提示文字
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.setSize = resetSize(this)
      this.getPictrue()
    })
    window.addEventListener('resize', this.onResize)
  },
  beforeUnmount() {
    window.removeEventListener('resize', this.onResize)
  },
  methods: {
    /** 窗口 resize 时重新计算尺寸 */
    onResize() {
      this.setSize = resetSize(this)
    },

    /** 刷新验证码 */
    refresh() {
      this.checkPosArr = []
      this.tempPoints = []
      this.num = 1
      this.bindingClick = true
      this.tipWords = ''
      this.getPictrue()
    },

    /** 请求验证码图片 */
    getPictrue() {
      reqGet({ captchaType: this.captchaType }).then((res) => {
        if (res.repCode === '0000') {
          this.pointBackImgBase = res.repData.originalImageBase64
          this.backToken = res.repData.token
          this.secretKey = res.repData.secretKey
          this.wordList = res.repData.wordList || []
          this.checkNum = this.wordList.length || 3
          // 生成提示文字
          this.tipWords = `请依次点击【${this.wordList.join(',')}】`
        } else {
          this.tipWords = res.repMsg
        }
      })
    },

    /** 点击背景图记录坐标 */
    canvasClick(e) {
      if (!this.bindingClick) return
      // 记录坐标（相对 img 元素）
      const offsetX = e.offsetX
      const offsetY = e.offsetY
      this.checkPosArr.push({ x: offsetX, y: offsetY })

      // 渲染标记点
      const tempPoint = { x: offsetX, y: offsetY }
      this.tempPoints.push(tempPoint)

      if (this.checkPosArr.length >= this.checkNum) {
        this.bindingClick = false
        // ★ 坐标比例换算：浏览器像素 → 原始 310x155
        const arr = this.pointTransfrom(this.checkPosArr, this.setSize)
        this.checkPosArr = arr

        setTimeout(() => {
          // 生成二次校验串
          const captchaVerification = this.secretKey
            ? aesEncrypt(`${this.backToken}---${JSON.stringify(this.checkPosArr)}`, this.secretKey)
            : `${this.backToken}---${JSON.stringify(this.checkPosArr)}`

          // check 加密坐标
          const data = {
            captchaType: this.captchaType,
            pointJson: this.secretKey
              ? aesEncrypt(JSON.stringify(this.checkPosArr), this.secretKey)
              : JSON.stringify(this.checkPosArr),
            token: this.backToken,
          }

          reqCheck(data).then((res) => {
            if (res.repCode === '0000') {
              this.bindingClick = false
              this.tipWords = '验证成功'
              if (this.$parent && this.$parent.closeBox) {
                this.$parent.closeBox()
              }
              this.$emit('success', { captchaVerification })
            } else {
              this.tipWords = res.repMsg || '验证失败'
              this.$emit('error', this)
              setTimeout(() => this.refresh(), 700)
            }
          })
        }, 400)
      }
    },

    /**
     * 坐标比例换算
     * 把浏览器实际像素坐标换算为原始 310x155 坐标系
     */
    pointTransfrom(pointArr, imgSize) {
      return pointArr.map((p) => ({
        x: Math.round((310 * p.x) / parseInt(imgSize.imgWidth)),
        y: Math.round((155 * p.y) / parseInt(imgSize.imgHeight)),
      }))
    },
  },
}
</script>

<template>
  <div style="position: relative">
    <!-- 背景图 -->
    <div
      class="verify-img-panel"
      :style="{
        width: setSize.imgWidth,
        height: setSize.imgHeight,
      }"
    >
      <img
        :src="`data:image/png;base64,${pointBackImgBase}`"
        alt=""
        style="width: 100%; height: 100%; display: block; cursor: pointer"
        @click="bindingClick ? canvasClick($event) : undefined"
      />
      <!-- 点击标记点 -->
      <div
        v-for="(tempPoint, index) in tempPoints"
        :key="index"
        class="verify-point"
        :style="{
          top: `${tempPoint.y - 10}px`,
          left: `${tempPoint.x - 10}px`,
        }"
      >
        {{ index + 1 }}
      </div>
      <div class="verify-refresh" @click="refresh">
        <i class="iconfont icon-refresh"></i>
      </div>
    </div>

    <!-- 提示条 -->
    <div
      class="verify-bar-area"
      :style="{
        width: setSize.imgWidth,
        height: setSize.barHeight,
        lineHeight: setSize.barHeight,
      }"
    >
      <span class="verify-msg">{{ tipWords }}</span>
    </div>
  </div>
</template>

<style scoped>
.verify-img-panel {
  position: relative;
  box-sizing: border-box;
  border: 1px solid #ddd;
  border-radius: 3px;
  overflow: hidden;
}

.verify-img-panel img {
  width: 100%;
  height: 100%;
  display: block;
}

.verify-point {
  position: absolute;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #337ab7;
  color: #fff;
  font-size: 12px;
  line-height: 20px;
  text-align: center;
  z-index: 2;
  box-shadow: 0 0 3px rgba(0, 0, 0, 0.3);
}

.verify-refresh {
  position: absolute;
  right: 5px;
  top: 5px;
  cursor: pointer;
  font-size: 16px;
  z-index: 2;
  color: #fff;
  width: 24px;
  height: 24px;
  line-height: 24px;
  text-align: center;
  background: rgba(0, 0, 0, 0.3);
  border-radius: 3px;
}

.verify-refresh:hover {
  background: rgba(0, 0, 0, 0.5);
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
  color: #333;
  font-size: 12px;
  user-select: none;
  -webkit-user-select: none;
}
</style>