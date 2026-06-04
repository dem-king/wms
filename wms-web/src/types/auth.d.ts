import type { EntityId } from './common'
export type { EntityId } from './common'

export interface LoginReq {
  username: string
  encryptedPassword: string
  /** 验证码二次校验串（captchaVerification） */
  code: string
  /** 验证码类型（blockPuzzle/clickWord） */
  randomStr: string
}

export interface LoginResp {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresIn: number
  userInfo: UserInfoVO
  permissions: string[]
  menus: MenuTreeNode[]
}

export interface UserInfoVO {
  userId: EntityId
  username: string
  realName: string
  avatar: string
  deptId: EntityId
  phone?: string
  email?: string
}

export interface LastLoginInfoVO {
  loginTime: string | null
  loginIp: string | null
}

export interface ProfileResp {
  userInfo: UserInfoVO
  lastLoginInfo: LastLoginInfoVO | null
  permissions: string[]
  roles: string[]
}

export interface UpdateProfileReq {
  realName: string
  phone?: string
  email?: string
  avatar?: string
}

export interface UploadAvatarResp {
  avatarUrl: string
}

export interface RsaKeyPairResp {
  publicKey: string
  keyId: string
}

/** anji-plus 验证码请求参数 */
export interface CaptchaVO {
  captchaType?: string
  pointJson?: string
  token?: string
  captchaVerification?: string
}

/** anji-plus 验证码响应结构 */
export interface ResponseModel {
  repCode: string
  repData: any
  repMsg: string
  success: boolean
}

export interface RefreshTokenReq {
  refreshToken: string
}

export interface TokenResp {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresIn: number
}

export interface PasswordReq {
  encryptedOldPassword: string
  encryptedNewPassword: string
}

export interface ApiResult<T = void> {
  code: number
  msg: string
  data: T
}

export interface LoginForm {
  username: string
  password: string
  captchaText: string
}

export interface PasswordForm {
  oldPassword: string
  newPassword: string
  confirmPassword: string
}

/** 菜单树节点 */
export interface MenuTreeNode {
  /** 菜单ID */
  id: EntityId
  /** 菜单名称 */
  menuName: string
  /** 菜单编码 */
  menuCode: string
  /** 上级菜单ID */
  parentId: EntityId
  /** 菜单类型(1-目录 2-菜单 3-按钮/操作) */
  menuType: number
  /** 路由路径 */
  path: string
  /** 前端组件路径 */
  component: string
  /** 重定向路径 */
  redirect: string
  /** 菜单图标 */
  icon: string
  /** 是否外链(0-否 1-是) */
  isExternal: number
  /** 是否缓存(0-否 1-是) */
  isCache: number
  /** 是否可见(0-隐藏 1-显示) */
  visible: number
  /** 状态(0-禁用 1-启用) */
  status?: number
  /** 排序号 */
  sortOrder: number
  /** 关联权限编码 */
  permCode: string
  /** 子菜单列表 */
  children: MenuTreeNode[]
}
