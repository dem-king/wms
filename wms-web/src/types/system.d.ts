/** 用户管理 */

/** 用户视图对象 */
export interface SysUserVo {
  /** 用户ID */
  id: number
  /** 用户名 */
  username: string
  /** 真实姓名 */
  realName: string
  /** 部门ID */
  deptId: number
  /** 部门名称 */
  deptName: string
  /** 手机号 */
  phone: string
  /** 邮箱 */
  email: string
  /** 状态(0-禁用 1-启用) */
  status: number
  /** 角色ID列表 */
  roleIds: number[]
  /** 角色名称列表 */
  roleNames: string[]
  /** 创建时间 */
  createTime: string
  /** 更新时间 */
  updateTime: string
}

/** 用户新增/编辑DTO */
export interface SysUserDto {
  /** 用户名 */
  username: string
  /** 密码(新增时必填) */
  password?: string
  /** 真实姓名 */
  realName: string
  /** 部门ID */
  deptId: number
  /** 手机号 */
  phone: string
  /** 邮箱 */
  email: string
  /** 状态(0-禁用 1-启用) */
  status: number
  /** 角色ID列表 */
  roleIds: number[]
}

/** 角色管理 */

/** 角色视图对象 */
export interface SysRoleVo {
  /** 角色ID */
  id: number
  /** 角色名称 */
  roleName: string
  /** 角色编码 */
  roleCode: string
  /** 描述 */
  roleDesc: string
  /** 数据范围(1-全部 2-自定义 3-本部门 4-本部门及以下 5-仅本人) */
  dataScope: number
  /** 状态(0-禁用 1-启用) */
  status: number
  /** 创建时间 */
  createTime: string
}

/** 角色新增/编辑DTO */
export interface SysRoleDto {
  /** 角色名称 */
  roleName: string
  /** 角色编码 */
  roleCode: string
  /** 描述 */
  roleDesc: string
  /** 数据范围 */
  dataScope: number
  /** 状态(0-禁用 1-启用) */
  status: number
}

/** 权限管理 */

/** 权限视图对象 */
export interface SysPermissionVo {
  /** 权限ID */
  id: number
  /** 权限名称 */
  permName: string
  /** 权限编码 */
  permCode: string
  /** 权限类型(1-菜单 2-按钮 3-数据) */
  permType: number
  /** 关联菜单ID */
  menuId: number
  /** 关联菜单名称 */
  menuName: string
  /** 状态(0-禁用 1-启用) */
  status: number
  /** 创建时间 */
  createTime: string
}

/** 权限新增/编辑DTO */
export interface SysPermissionDto {
  /** 权限名称 */
  permName: string
  /** 权限编码 */
  permCode: string
  /** 权限类型 */
  permType: number
  /** 关联菜单ID */
  menuId: number
  /** 状态(0-禁用 1-启用) */
  status: number
}

/** 部门管理 */

/** 部门视图对象 */
export interface SysDeptVo {
  /** 部门ID */
  id: number
  /** 部门名称 */
  deptName: string
  /** 部门编码 */
  deptCode: string
  /** 上级部门ID */
  parentId: number
  /** 负责人 */
  leader: string
  /** 排序号 */
  sortOrder: number
  /** 状态(0-禁用 1-启用) */
  status: number
  /** 创建时间 */
  createTime: string
  /** 子部门列表 */
  children: SysDeptVo[]
}

/** 部门新增/编辑DTO */
export interface SysDeptDto {
  /** 部门名称 */
  deptName: string
  /** 部门编码 */
  deptCode: string
  /** 上级部门ID */
  parentId: number
  /** 负责人 */
  leader: string
  /** 排序号 */
  sortOrder: number
  /** 状态(0-禁用 1-启用) */
  status: number
}

/** 系统配置 */

/** 系统配置视图对象 */
export interface SysConfigVo {
  /** 配置ID */
  id: number
  /** 配置名称 */
  configName: string
  /** 配置键 */
  configKey: string
  /** 配置值 */
  configValue: string
  /** 配置组 */
  configGroup: string
  /** 描述 */
  description: string
  /** 创建时间 */
  createTime: string
}

/** 系统配置新增/编辑DTO */
export interface SysConfigDto {
  /** 配置名称 */
  configName: string
  /** 配置键 */
  configKey: string
  /** 配置值 */
  configValue: string
  /** 配置组 */
  configGroup: string
  /** 描述 */
  description: string
}

/** 供应商管理 */

/** 供应商视图对象 */
export interface SysSupplierVo {
  /** 供应商ID */
  id: number
  /** 供应商名称 */
  supplierName: string
  /** 供应商编码 */
  supplierCode: string
  /** 联系人 */
  contactPerson: string
  /** 联系电话 */
  contactPhone: string
  /** 地址 */
  address: string
  /** 状态(0-禁用 1-启用) */
  status: number
  /** 创建时间 */
  createTime: string
}

/** 供应商新增/编辑DTO */
export interface SysSupplierDto {
  /** 供应商名称 */
  supplierName: string
  /** 供应商编码 */
  supplierCode: string
  /** 联系人 */
  contactPerson: string
  /** 联系电话 */
  contactPhone: string
  /** 地址 */
  address: string
  /** 状态(0-禁用 1-启用) */
  status: number
}

/** 分页请求参数 */
export interface PageParams {
  /** 当前页码 */
  page: number
  /** 每页条数 */
  size: number
}

/** 分页响应结果 */
export interface PageResult<T> {
  /** 数据列表 */
  records: T[]
  /** 总条数 */
  total: number
  /** 当前页码 */
  page: number
  /** 每页条数 */
  size: number
}
