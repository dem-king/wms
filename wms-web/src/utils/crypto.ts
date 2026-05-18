import JSEncrypt from 'jsencrypt'

export function rsaEncrypt(plainText: string, publicKey: string): string {
  const encrypt = new JSEncrypt()
  encrypt.setPublicKey(publicKey)
  const encrypted = encrypt.encrypt(plainText)
  if (encrypted === false || encrypted === null) {
    throw new Error('RSA加密失败')
  }
  return encrypted
}

export function isRsaSupported(): boolean {
  try {
    const test = new JSEncrypt()
    return typeof test.setPublicKey === 'function'
  } catch {
    return false
  }
}
