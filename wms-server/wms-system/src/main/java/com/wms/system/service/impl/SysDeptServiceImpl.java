package com.wms.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.common.exception.BizException;
import com.wms.common.constant.BizConstants;
import com.wms.common.constant.DelFlagConstants;
import com.wms.system.domain.dto.SysDeptDto;
import com.wms.system.domain.entity.SysDepartment;
import com.wms.system.domain.vo.SysDeptVo;
import com.wms.system.mapper.SysDepartmentMapper;
import com.wms.system.service.SysDeptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 部门服务实现类
 * 处理部门CRUD、部门树构建等业务逻辑
 */
@Service
@RequiredArgsConstructor
public class SysDeptServiceImpl implements SysDeptService {

    private final SysDepartmentMapper sysDepartmentMapper;

    @Override
    public List<SysDeptVo> listTree() {
        List<SysDepartment> allDepts = sysDepartmentMapper.selectList(
                new LambdaQueryWrapper<SysDepartment>()
                        .orderByAsc(SysDepartment::getSortOrder)
        );
        return buildTree(allDepts);
    }

    @Override
    public List<SysDeptVo> tree() {
        return listTree();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysDeptVo create(SysDeptDto dto) {
        // 校验部门编码唯一性
        checkDeptCodeUnique(dto.getDeptCode(), null);
        SysDepartment dept = new SysDepartment();
        copyDtoToEntity(dto, dept);
        // 新增部门默认启用
        if (dept.getStatus() == null) {
            dept.setStatus(BizConstants.STATUS_ENABLED);
        }
        sysDepartmentMapper.insert(dept);
        return toVo(dept);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysDeptVo update(Long id, SysDeptDto dto) {
        SysDepartment existing = sysDepartmentMapper.selectById(id);
        if (existing == null || existing.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("部门不存在");
        }
        // 校验部门编码唯一性(排除自身)
        checkDeptCodeUnique(dto.getDeptCode(), id);
        copyDtoToEntity(dto, existing);
        existing.setId(id);
        sysDepartmentMapper.updateById(existing);
        return toVo(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        SysDepartment existing = sysDepartmentMapper.selectById(id);
        if (existing == null || existing.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("部门不存在");
        }
        // 校验是否存在子部门
        Long childCount = sysDepartmentMapper.selectCount(
                new LambdaQueryWrapper<SysDepartment>()
                        .eq(SysDepartment::getParentId, id)
        );
        if (childCount > 0) {
            throw new BizException("存在子部门，无法删除");
        }
        // 逻辑删除部门
        SysDepartment updateDept = new SysDepartment();
        updateDept.setId(id);
        updateDept.setDelFlag(DelFlagConstants.DELETED);
    
        sysDepartmentMapper.updateById(updateDept);
    }

    /**
     * 将部门列表构建为树形结构
     *
     * @param depts 部门列表
     * @return 树形部门列表
     */
    private List<SysDeptVo> buildTree(List<SysDepartment> depts) {
        List<SysDeptVo> voList = depts.stream().map(this::toVo).collect(Collectors.toList());
        Map<Long, List<SysDeptVo>> groupedByParent = voList.stream()
                .collect(Collectors.groupingBy(SysDeptVo::getParentId));
        voList.forEach(vo -> vo.setChildren(groupedByParent.getOrDefault(vo.getId(), Collections.emptyList())));
        // 返回顶级部门(parentId=0)
        return groupedByParent.getOrDefault(BizConstants.TOP_PARENT_ID, Collections.emptyList());
    }

    /**
     * 校验部门编码唯一性
     *
     * @param deptCode  部门编码
     * @param excludeId 排除的部门ID(更新时排除自身)
     */
    private void checkDeptCodeUnique(String deptCode, Long excludeId) {
        LambdaQueryWrapper<SysDepartment> wrapper = new LambdaQueryWrapper<SysDepartment>()
                .eq(SysDepartment::getDeptCode, deptCode);
        if (excludeId != null) {
            wrapper.ne(SysDepartment::getId, excludeId);
        }
        Long count = sysDepartmentMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BizException("部门编码已存在: " + deptCode);
        }
    }

    /**
     * DTO属性拷贝到Entity
     */
    private void copyDtoToEntity(SysDeptDto dto, SysDepartment entity) {
        entity.setDeptName(dto.getDeptName());
        entity.setDeptCode(dto.getDeptCode());
        entity.setParentId(dto.getParentId());
        entity.setSortOrder(dto.getSortOrder());
        entity.setLeader(dto.getLeader());
        entity.setPhone(dto.getPhone());
        entity.setStatus(dto.getStatus());
    }

    /**
     * SysDepartment实体转SysDeptVo
     */
    private SysDeptVo toVo(SysDepartment dept) {
        SysDeptVo vo = new SysDeptVo();
        vo.setId(dept.getId());
        vo.setDeptName(dept.getDeptName());
        vo.setDeptCode(dept.getDeptCode());
        vo.setParentId(dept.getParentId());
        vo.setSortOrder(dept.getSortOrder());
        vo.setLeader(dept.getLeader());
        vo.setPhone(dept.getPhone());
        vo.setStatus(dept.getStatus());
        vo.setCreateTime(dept.getCreateTime());
        return vo;
    }
}
