import CryptoJS from 'crypto-js'

/**
 * AES 加密工具函数
 * 使用 ECB 模式 + PKCS7 填充，与后端 anji-plus/captcha SDK 的 AES 解密对齐
 *
 * @param word 待加密的明文字符串
 * @param keyWord AES 密钥（16 字节字符串，由 /auth/code/get 返回的 secretKey 提供）
 * @returns Base64 编码的加密结果
 */
export function aesEncrypt(word, keyWord = 'XwKsGlMcdPMEhR1B') {
  const key = CryptoJS.enc.Utf8.parse(keyWord)
  const srcs = CryptoJS.enc.Utf8.parse(word)
  const encrypted = CryptoJS.AES.encrypt(srcs, key, {
    mode: CryptoJS.mode.ECB,
    padding: CryptoJS.pad.Pkcs7,
  })
  return encrypted.toString()
}