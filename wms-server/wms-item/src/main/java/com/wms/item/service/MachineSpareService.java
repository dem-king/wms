package com.wms.item.service;

import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.item.domain.dto.MachineSpareDto;
import com.wms.item.domain.vo.MachineSpareVo;

import java.util.List;

/**
 * 机器-备件关联服务接口
 */
public interface MachineSpareService {

    /**
     * 分页查询机器-备件关联
     *
     * @param pageParam 分页参数
     * @param machineName 机器名称(可选，模糊匹配)
     * @param spareItemId 备件物品ID(可选)
     * @return 分页结果
     */
    PageResult<MachineSpareVo> pageList(PageParam pageParam, String machineName, Long spareItemId);

    /**
     * 根据ID获取机器-备件关联详情
     *
     * @param id 主键
     * @return 机器-备件关联VO
     */
    MachineSpareVo getById(Long id);

    /**
     * 新增机器-备件关联
     *
     * @param dto 新增参数
     * @return 新增后的VO
     */
    MachineSpareVo create(MachineSpareDto dto);

    /**
     * 更新机器-备件关联
     *
     * @param id 主键
     * @param dto 更新参数
     * @return 更新后的VO
     */
    MachineSpareVo update(Long id, MachineSpareDto dto);

    /**
     * 删除机器-备件关联(逻辑删除)
     *
     * @param id 主键
     */
    void delete(Long id);

    /**
     * 按机器编号查询备件列表
     *
     * @param machineCode 机器编号
     * @return 备件关联VO列表
     */
    List<MachineSpareVo> listByMachineCode(String machineCode);
}
