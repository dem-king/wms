import type { EntityId } from './common'
export type { EntityId } from './common'

export interface LoginReq {
  username: string
  encryptedPassword: string
  /** 滑块拼图 Token（来自 getSliderCaptcha 响应） */
  captchaToken: string
  /** 滑块拖动轨迹 JSON 字符串，由 SliderPuzzle 组件采集 */
  captchaTrack: string
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

export interface CaptchaImageResp {
  /** 滑块拼图 Token，登录时需回传 */
  captchaToken: string
  /** 背景图 Base64 (data:image/png;base64,...) */
  backgroundImage: string
  /** 拼图块 Base64 */
  blockImage: string
  /** 拼图缺口 Y 坐标（用于前端将拼图块拖到正确位置） */
  blockY: number
  /** Token 过期秒数 */
  expiresIn: number
}

/** 单个轨迹点 */
export interface SliderTrackPoint {
  /** 相对拖动起点的 X 偏移 (px) */
  x: number
  /** 相对拖动起点的 Y 偏移 (px) */
  y: number
  /** 时间戳偏移 (ms，相对于拖动开始) */
  t: number
  /** 动作类型 */
  type: 'MOVE' | 'DOWN' | 'UP'
}

/** 完整轨迹数据（序列化为 JSON 字符串后回传后端） */
export interface SliderTrackPayload {
  /** 验证码 Token */
  id: string
  /** 验证码类型：固定 SLIDER */
  type: 'SLIDER'
  /** 拖动轨迹点列表 */
  data: SliderTrackPoint[]
  /** 是否已结束（最后一次 UP） */
  stop: boolean
}

/** 旧图形码响应（保留以兼容历史 API，但前端不再使用） */
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
