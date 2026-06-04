/**
 * 尺寸换算工具函数
 * 将配置中的百分比或像素值换算为实际渲染尺寸
 * 支持百分比（如 '100%'）和具体像素值（如 '310px'）
 *
 * @param vm Vue 组件实例（需包含 imgSize/barSize 数据）
 * @returns {imgWidth, imgHeight, barWidth, barHeight} 换算后的尺寸对象
 */
export function resetSize(vm) {
  let bar_height, bar_width, img_height, img_width
  const parentWidth = vm.$el.parentNode.offsetWidth || window.offsetWidth
  const parentHeight = vm.$el.parentNode.offsetHeight || window.offsetHeight

  img_width = vm.imgSize.width.includes('%')
    ? `${(parseInt(vm.imgSize.width) / 100) * parentWidth}px`
    : vm.imgSize.width
  img_height = vm.imgSize.height.includes('%')
    ? `${(parseInt(vm.imgSize.height) / 100) * parentHeight}px`
    : vm.imgSize.height
  bar_width = vm.barSize.width.includes('%')
    ? `${(parseInt(vm.barSize.width) / 100) * parentWidth}px`
    : vm.barSize.width
  bar_height = vm.barSize.height.includes('%')
    ? `${(parseInt(vm.barSize.height) / 100) * parentHeight}px`
    : vm.barSize.height

  return { imgWidth: img_width, imgHeight: img_height, barWidth: bar_width, barHeight: bar_height }
}