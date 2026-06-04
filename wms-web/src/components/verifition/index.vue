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
  <div :class="mode === 'pop' ? 'mask' : ''" v-show="showBox">
    <div
      :class="mode === 'pop' ? 'verifybox' : ''"
      :style="{ 'max-width': `${parseInt(imgSize.width) + 30}px` }"
    >
      <div class="verifybox-top" v-if="mode === 'pop'">
        请完成安全验证
        <span class="verifybox-close" @click="closeBox">
          <i class="iconfont icon-close"></i>
        </span>
      </div>
      <div
        class="verifybox-bottom"
        :style="{ padding: mode === 'pop' ? '15px' : '0' }"
      >
        <component
          v-if="componentType"
          :is="componentType"
          :captcha-type="captchaType"
          :type="verifyType"
          :img-size="imgSize"
          :block-size="blockSize"
          :bar-size="barSize"
          ref="instance"
          @success="onSuccess"
          @error="onError"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.mask {
  position: fixed;
  top: 0;
  left: 0;
  z-index: 1001;
  width: 100%;
  height: 100vh;
  background: rgb(0 0 0 / 30%);
  transition: all 0.5s;
}

.verifybox {
  position: relative;
  top: 50%;
  left: 50%;
  box-sizing: border-box;
  background: #fff;
  border: 1px solid #e4e7eb;
  border-radius: 2px;
  box-shadow: 0 0 10px rgb(0 0 0 / 30%);
  transform: translate(-50%, -50%);
}

.verifybox-top {
  box-sizing: border-box;
  height: 50px;
  padding: 0 15px;
  font-size: 16px;
  line-height: 50px;
  color: #45494c;
  text-align: left;
  border-bottom: 1px solid #e4e7eb;
}

.verifybox-close {
  position: absolute;
  top: 13px;
  right: 9px;
  width: 24px;
  height: 24px;
  text-align: center;
  cursor: pointer;
  color: #999;
}

.verifybox-close::before {
  font-size: 20px;
  line-height: 22px;
  content: '×';
}

.verifybox-close:hover {
  color: #333;
}

.verifybox-bottom {
  box-sizing: border-box;
}
</style>
