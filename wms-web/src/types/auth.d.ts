export interface LoginReq {
  username: string
  encryptedPassword: string
  captchaKey?: string
  captchaText?: string
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
  userId: number
  username: string
  realName: string
  avatar: string
  deptId: number
}

export interface RsaKeyPairResp {
  publicKey: string
  keyId: string
}

export interface CaptchaResp {
  captchaKey: string
  captchaImage: string
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
  id: number
  /** 菜单名称 */
  menuName: string
  /** 菜单编码 */
  menuCode: string
  /** 上级菜单ID */
  parentId: number
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
  /** 排序号 */
  sortOrder: number
  /** 关联权限编码 */
  permCode: string
  /** 子菜单列表 */
  children: MenuTreeNode[]
}
