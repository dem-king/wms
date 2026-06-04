import { aesEncrypt } from './ase'

describe('AES 加密工具', () => {
  it('应返回合法的 Base64 字符串', () => {
    const result = aesEncrypt('{"x":120,"y":5}', 'XwKsGlMcdPMEhR1B')
    expect(result).toBeTruthy()
    expect(typeof result).toBe('string')
  })

  it('不同密钥应产生不同结果', () => {
    const result1 = aesEncrypt('test', 'key1key1key1key1')
    const result2 = aesEncrypt('test', 'key2key2key2key2')
    expect(result1).not.toBe(result2)
  })

  it('相同密钥和明文应产生相同结果', () => {
    const result1 = aesEncrypt('test', 'XwKsGlMcdPMEhR1B')
    const result2 = aesEncrypt('test', 'XwKsGlMcdPMEhR1B')
    expect(result1).toBe(result2)
  })

  it('默认密钥应正常工作', () => {
    const result = aesEncrypt('hello')
    expect(result).toBeTruthy()
    expect(typeof result).toBe('string')
  })

  it('空字符串应也能加密', () => {
    const result = aesEncrypt('', 'XwKsGlMcdPMEhR1B')
    expect(result).toBeTruthy()
    expect(typeof result).toBe('string')
  })

  it('加密结果应为合法 Base64 格式', () => {
    const result = aesEncrypt('{"x":120,"y":5}', 'XwKsGlMcdPMEhR1B')
    // Base64 字符集：A-Z, a-z, 0-9, +, /, =（填充）
    expect(result).toMatch(/^[A-Za-z0-9+/=]+$/)
  })

  it('不同明文应产生不同密文', () => {
    const result1 = aesEncrypt('{"x":100,"y":5}', 'XwKsGlMcdPMEhR1B')
    const result2 = aesEncrypt('{"x":200,"y":10}', 'XwKsGlMcdPMEhR1B')
    expect(result1).not.toBe(result2)
  })
})