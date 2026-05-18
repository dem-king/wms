/**
 * 向document中注入CSS变量
 * 创建或更新<style>元素，将变量映射写入:root
 * @param variables CSS变量键值对
 * @param id style元素的ID
 */
export function updateCSSVariables(
  variables: Record<string, string>,
  id = '__app-styles__',
): void {
  const styleElement =
    document.querySelector(`#${id}`) as HTMLStyleElement || document.createElement('style')

  styleElement.id = id

  let cssText = ':root {'
  for (const key in variables) {
    if (Object.prototype.hasOwnProperty.call(variables, key)) {
      cssText += `${key}: ${variables[key]};`
    }
  }
  cssText += '}'

  styleElement.textContent = cssText

  if (!document.querySelector(`#${id}`)) {
    setTimeout(() => {
      document.head.append(styleElement)
    })
  }
}
