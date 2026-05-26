package com.wms.system.converter;

import com.wms.system.domain.entity.SysMenu;
import com.wms.system.domain.vo.MenuTreeVo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单转换器
 * SysMenu实体与MenuTreeVo之间的转换逻辑
 */
@Component
public class SysMenuConverter {

    /**
     * SysMenu实体转MenuTreeVo
     *
     * @param menu 菜单实体
     * @return 菜单树VO
     */
    public MenuTreeVo toVo(SysMenu menu) {
        MenuTreeVo vo = new MenuTreeVo();
        vo.setId(menu.getId());
        vo.setMenuName(menu.getMenuName());
        vo.setMenuCode(menu.getMenuCode());
        vo.setParentId(menu.getParentId());
        vo.setMenuType(menu.getMenuType());
        vo.setPath(menu.getPath());
        vo.setComponent(menu.getComponent());
        vo.setRedirect(menu.getRedirect());
        vo.setIcon(menu.getIcon());
        vo.setIsExternal(menu.getIsExternal());
        vo.setIsCache(menu.getIsCache());
        vo.setVisible(menu.getVisible());
        vo.setSortOrder(menu.getSortOrder());
        vo.setPermCode(menu.getPermCode());
        return vo;
    }

    /**
     * 批量转换菜单实体列表
     *
     * @param menus 菜单实体列表
     * @return 菜单树VO列表
     */
    public List<MenuTreeVo> toVoList(List<SysMenu> menus) {
        return menus.stream().map(this::toVo).collect(Collectors.toList());
    }
}
