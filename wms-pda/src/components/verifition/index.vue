<!--
  验证码父容器（uni-app 版本）
  mode=pop：全屏遮罩 + 底部弹出面板
  mode=fixed：直接内嵌
  使用 view/text 替代 div/span，使用 uni-app 遮罩替代 el-dialog
-->
<script>
import VerifyPoints from './Verify/VerifyPoints.vue'
import VerifySlide from './Verify/VerifySlide.vue'

export default {
  name: 'Vue2Verify',
  components: { VerifySlide, VerifyPoints },
  props: {
    captchaType: { type: String, required: true }, // 'blockPuzzle' | 'clickWord'
    mode: { type: String, default: 'pop' },        // 'pop' 弹窗 | 'fixed' 内嵌
    imgSize: {
      type: Object,
      default: () => ({ width: '310px', height: '155px' }),
    },
    blockSize: { type: Object },                    // 滑块大小
    barSize: {
      type: Object,
      default: () => ({ width: '310px', height: '40px' }),
    },
  },
  data() {
    return {
      clickShow: false,     // pop 模式下是否显示
      verifyType: undefined, // 验证类型标识
      componentType: undefined, // 动态组件名
    }
  },
  computed: {
    /** 是否显示验证码区域 */
    showBox() {
      return this.mode === 'pop' ? this.clickShow : true
    },
  },
  watch: {
    captchaType: {
      handler(val) {
        switch (val) {
          case 'blockPuzzle':
            this.verifyType = '2'
            this.componentType = 'VerifySlide'
            break
          case 'clickWord':
            this.verifyType = ''
            this.componentType = 'VerifyPoints'
            break
        }
      },
      immediate: true,
    },
  },
  methods: {
    /** 刷新验证码 */
    refresh() {
      if (this.$refs.instance && this.$refs.instance.refresh) {
        this.$refs.instance.refresh()
      }
    },

    /** 关闭弹窗 */
    closeBox() {
      this.clickShow = false
      this.refresh()
    },

    /** 弹出验证码（供父组件调用） */
    show() {
      if (this.mode === 'pop') {
        this.clickShow = true
      }
    },

    /** 子组件 success 事件向上传递 */
    onSuccess(params) {
      this.$emit('success', params)
    },

    /** 子组件 error 事件向上传递 */
    onError(params) {
      this.$emit('error', params)
    },
  },
}
</script>

<template>
  <view v-if="showBox" :class="mode === 'pop' ? 'verify-mask' : ''">
    <view
      v-if="mode === 'pop'"
      class="verify-mask-bg"
      @click="closeBox"
    />
    <view
      :class="mode === 'pop' ? 'verifybox' : ''"
      :style="{ 'max-width': (parseInt(imgSize.width) + 30) + 'px' }"
    >
      <view class="verifybox-top" v-if="mode === 'pop'">
        <text class="verifybox-title">请完成安全验证</text>
        <text class="verifybox-close" @click="closeBox">×</text>
      </view>
      <view
        class="verifybox-bottom"
        :style="{ padding: mode === 'pop' ? '15px' : '0' }"
      >
        <VerifySlide
          v-if="componentType === 'VerifySlide'"
          :captcha-type="captchaType"
          :type="verifyType"
          :img-size="imgSize"
          :block-size="blockSize"
          :bar-size="barSize"
          ref="instance"
          @success="onSuccess"
          @error="onError"
        />
        <VerifyPoints
          v-if="componentType === 'VerifyPoints'"
          :captcha-type="captchaType"
          :img-size="imgSize"
          :bar-size="barSize"
          ref="instance"
          @success="onSuccess"
          @error="onError"
        />
      </view>
    </view>
  </view>
</template>

<style scoped>
/* 全屏遮罩容器 */
.verify-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 遮罩背景 */
.verify-mask-bg {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 9998;
}

/* 弹窗容器 */
.verifybox {
  position: relative;
  background: #fff;
  border-radius: 5px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.3);
  overflow: hidden;
  z-index: 9999;
}

/* 弹窗顶部标题栏 */
.verifybox-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 15px;
  height: 40px;
  border-bottom: 1px solid #e8e8e8;
  background: #f7f7f7;
}

.verifybox-title {
  font-size: 14px;
  color: #333;
}

/* 关闭按钮 */
.verifybox-close {
  font-size: 20px;
  color: #999;
  padding: 0 4px;
}

/* 弹窗内容区域 */
.verifybox-bottom {
  box-sizing: border-box;
}
</style>