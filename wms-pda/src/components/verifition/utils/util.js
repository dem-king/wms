/**
 * 尺寸换算工具函数（uni-app 版本）
 * 将配置中的百分比或像素值换算为实际渲染尺寸
 * 支持百分比（如 '100%'）和具体像素值（如 '310px'）
 *
 * uni-app 中无法使用 this.$el.parentNode.offsetWidth，
 * 改为通过 uni.createSelectorQuery 获取父容器宽度，
 * 但验证码组件通常使用固定尺寸，此处直接返回配置值即可。
 *
 * @param vm Vue 组件实例（需包含 imgSize/barSize 数据）
 * @returns {imgWidth, imgHeight, barWidth, barHeight} 换算后的尺寸对象
 */
export function resetSize(vm) {
  // uni-app 环境下直接使用配置的固定像素值
  // 如需百分比适配，可通过 uni.createSelectorQuery 获取父容器尺寸后计算
  const img_width = vm.imgSize.width
  const img_height = vm.imgSize.height
  const bar_width = vm.barSize.width
  const bar_height = vm.barSize.height

  return { imgWidth: img_width, imgHeight: img_height, barWidth: bar_width, barHeight: bar_height }
}