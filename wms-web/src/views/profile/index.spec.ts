import { readFileSync } from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'
import { describe, expect, it } from 'vitest'

const currentDir = path.dirname(fileURLToPath(import.meta.url))
const pageSource = readFileSync(path.resolve(currentDir, 'index.vue'), 'utf-8')
const routerSource = readFileSync(path.resolve(currentDir, '../../router/index.ts'), 'utf-8')
const navbarSource = readFileSync(path.resolve(currentDir, '../../layouts/components/Navbar.vue'), 'utf-8')
const userDropdownSource = readFileSync(path.resolve(currentDir, '../../layouts/components/UserDropdown.vue'), 'utf-8')

describe('profile entry orchestration', () => {
  it('registers the hidden profile route under the root layout', () => {
    expect(routerSource).toContain("path: 'profile'")
    expect(routerSource).toContain("name: 'Profile'")
    expect(routerSource).toContain("title: '个人中心'")
    expect(routerSource).toContain("import('@/views/profile/index.vue')")
  })

  it('adds a profile entry in the user dropdown', () => {
    expect(userDropdownSource).toContain('emit(\'profile\')')
    expect(userDropdownSource).toContain('个人中心')
  })

  it('navigates to the profile page from the navbar user dropdown', () => {
    expect(navbarSource).toContain('@profile="openProfile"')
    expect(navbarSource).toContain("router.push('/profile')")
  })

  it('contains profile sections and data loading markers', () => {
    expect(pageSource).toContain('账号概览')
    expect(pageSource).toContain('基础资料')
    expect(pageSource).toContain('登录信息')
    expect(pageSource).toContain('安全中心')
    expect(pageSource).toContain('getProfile')
    expect(pageSource).toContain('lastLoginInfo')
    expect(pageSource).toContain('loginTime')
    expect(pageSource).toContain('loginIp')
  })

  it('contains password dialog and logout action markers', () => {
    expect(pageSource).toContain('ChangePasswordDialog')
    expect(pageSource).toContain('passwordDialogVisible')
    expect(pageSource).toContain('userStore.logout()')
  })

  it('contains editable profile form and avatar upload markers', () => {
    expect(pageSource).toContain('编辑资料')
    expect(pageSource).toContain('真实姓名')
    expect(pageSource).toContain('手机号')
    expect(pageSource).toContain('邮箱')
    expect(pageSource).toContain('editForm')
    expect(pageSource).toContain('previewAvatarUrl')
    expect(pageSource).toContain('uploadAvatar')
    expect(pageSource).toContain('handleAvatarChange')
    expect(pageSource).toContain('handleSaveProfile')
  })
})
