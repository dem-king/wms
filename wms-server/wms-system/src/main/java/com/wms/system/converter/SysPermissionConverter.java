package com.wms.system.converter;

import com.wms.system.domain.entity.SysPermission;
import com.wms.system.domain.vo.SysPermissionVo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Permission converter.
 * Converts SysPermission entities into permission view objects.
 */
@Component
public class SysPermissionConverter {

    /**
     * Converts one permission entity into a view object.
     *
     * @param permission permission entity
     * @return permission view object
     */
    public SysPermissionVo toVo(SysPermission permission) {
        return toVo(permission, null);
    }

    /**
     * Converts one permission entity and fills the related menu name.
     *
     * @param permission permission entity
     * @param menuName related menu name
     * @return permission view object
     */
    public SysPermissionVo toVo(SysPermission permission, String menuName) {
        SysPermissionVo vo = new SysPermissionVo();
        vo.setId(permission.getId());
        vo.setPermName(permission.getPermName());
        vo.setPermCode(permission.getPermCode());
        vo.setPermType(permission.getPermType());
        vo.setParentId(permission.getParentId());
        vo.setMenuId(permission.getMenuId());
        vo.setMenuName(menuName);
        vo.setStatus(permission.getStatus());
        vo.setCreateTime(permission.getCreateTime());
        return vo;
    }

    /**
     * Converts a permission list and fills menu names from the provided map.
     *
     * @param permissions permission entities
     * @param menuNameMap menu ID to menu name map
     * @return permission view objects
     */
    public List<SysPermissionVo> toVoList(List<SysPermission> permissions, Map<Long, String> menuNameMap) {
        return permissions.stream()
                .map(permission -> toVo(permission, menuNameMap.get(permission.getMenuId())))
                .toList();
    }
}
