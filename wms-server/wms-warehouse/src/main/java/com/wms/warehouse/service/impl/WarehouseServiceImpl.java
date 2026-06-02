package com.wms.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.constant.BizConstants;
import com.wms.common.constant.DelFlagConstants;
import com.wms.common.exception.BizException;
import com.wms.common.storage.StorageStrategy;
import com.wms.common.util.LogicDeleteHelper;
import com.wms.common.util.SequenceGenerator;
import com.wms.warehouse.domain.constant.LayoutElementConstants;
import com.wms.warehouse.domain.constant.WarehouseConstants;
import com.wms.warehouse.domain.dto.WarehouseDto;
import com.wms.warehouse.domain.entity.WmsWarehouse;
import com.wms.warehouse.domain.vo.WarehouseBackgroundVo;
import com.wms.warehouse.domain.vo.WarehouseVo;
import com.wms.warehouse.converter.WarehouseConverter;
import com.wms.warehouse.mapper.WmsWarehouseMapper;
import com.wms.warehouse.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 库房服务实现类
 * 处理库房CRUD、分页查询、列表查询、底图管理等业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WmsWarehouseMapper wmsWarehouseMapper;
    private final SequenceGenerator sequenceGenerator;
    private final WarehouseConverter warehouseConverter;
    private final StorageStrategy storageStrategy;

    /**
     * 查询所有库房列表
     *
     * @return 库房VO列表
     */
    @Override
    public List<WarehouseVo> listAll() {
        LambdaQueryWrapper<WmsWarehouse> wrapper = new LambdaQueryWrapper<WmsWarehouse>()
                .orderByDesc(WmsWarehouse::getCreateTime);
        List<WmsWarehouse> list = wmsWarehouseMapper.selectList(wrapper);
        return list.stream().map(warehouseConverter::toVo).collect(Collectors.toList());
    }

    /**
     * 分页查询库房
     *
     * @param pageParam 分页参数
     * @param status    状态(可选)
     * @param keyword   关键字(可选，模糊匹配名称/编码)
     * @return 库房分页结果
     */
    @Override
    public PageResult<WarehouseVo> page(PageParam pageParam, Integer status, String keyword) {
        LambdaQueryWrapper<WmsWarehouse> wrapper = new LambdaQueryWrapper<WmsWarehouse>();
        // 按状态筛选
        if (status != null) {
            wrapper.eq(WmsWarehouse::getStatus, status);
        }
        // 按关键字模糊搜索(名称/编码)
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w
                    .like(WmsWarehouse::getWarehouseName, keyword)
                    .or().like(WmsWarehouse::getWarehouseCode, keyword)
            );
        }
        wrapper.orderByDesc(WmsWarehouse::getCreateTime);

        Page<WmsWarehouse> page = wmsWarehouseMapper.selectPage(
                new Page<>(pageParam.getPage(), pageParam.getSize()), wrapper);

        PageResult<WarehouseVo> result = new PageResult<>();
        result.setRecords(page.getRecords().stream().map(warehouseConverter::toVo).collect(Collectors.toList()));
        result.setTotal(page.getTotal());
        result.setPage(pageParam.getPage());
        result.setSize(pageParam.getSize());
        return result;
    }

    /**
     * 新增库房
     * 自动生成库房编码，默认状态为启用
     *
     * @param dto 库房新增参数
     * @return 新增后的库房VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public WarehouseVo create(WarehouseDto dto) {
        WmsWarehouse warehouse = new WmsWarehouse();
        copyDtoToEntity(dto, warehouse);
        // 自动生成库房编码: KF + 年月日 + 4位流水号
        warehouse.setWarehouseCode(generateWarehouseCode());
        // 默认状态为启用
        if (warehouse.getStatus() == null) {
            warehouse.setStatus(BizConstants.STATUS_ENABLED);
        }
        wmsWarehouseMapper.insert(warehouse);
        return warehouseConverter.toVo(warehouse);
    }

    /**
     * 更新库房
     * 编辑时不修改编码
     *
     * @param id  库房ID
     * @param dto 库房更新参数
     * @return 更新后的库房VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public WarehouseVo update(Long id, WarehouseDto dto) {
        WmsWarehouse existing = wmsWarehouseMapper.selectById(id);
        if (existing == null) {
            throw new BizException("库房不存在");
        }
        if (existing.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("库房已删除");
        }
        copyDtoToEntity(dto, existing);
        existing.setId(id);
        // 编辑时不修改编码
        existing.setWarehouseCode(null);
        wmsWarehouseMapper.updateById(existing);
        return warehouseConverter.toVo(existing);
    }

    /**
     * 删除库房(逻辑删除)
     *
     * @param id 库房ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        WmsWarehouse existing = wmsWarehouseMapper.selectById(id);
        if (existing == null) {
            throw new BizException("库房不存在");
        }
        if (existing.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("库房已删除");
        }
        // delFlag 是 @TableLogic 字段，必须显式 SET 才能真正写入删除标记
        LogicDeleteHelper.markDeleted(wmsWarehouseMapper, WmsWarehouse.class, id);
    }

    /**
     * 生成库房编码: KF + 年月日 + 4位流水号
     * 基于Redis INCR原子操作保证并发安全
     * 示例: KF202605140001
     */
    private String generateWarehouseCode() {
        return sequenceGenerator.next(WarehouseConstants.WAREHOUSE_CODE_PREFIX);
    }

    /**
     * DTO属性拷贝到Entity
     */
    private void copyDtoToEntity(WarehouseDto dto, WmsWarehouse entity) {
        entity.setWarehouseName(dto.getWarehouseName());
        entity.setWarehouseCode(dto.getWarehouseCode());
        entity.setAddress(dto.getAddress());
        entity.setManager(dto.getManager());
        entity.setPhone(dto.getPhone());
        entity.setArea(dto.getArea());
        entity.setStatus(dto.getStatus());
        entity.setRemark(dto.getRemark());
    }

    /**
     * 上传库房底图
     * 校验文件类型（JPG/PNG/SVG）和大小（≤10MB），生成版本号（yyyyMMddHHmmss），
     * 上传至MinIO路径 warehouse-layout/{warehouseId}/background/{version}.{ext}，
     * 更新layoutBackgroundVersion，上传失败时保留原底图不变
     *
     * @param warehouseId 库房ID
     * @param file        底图文件
     * @return 底图上传结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public WarehouseBackgroundVo uploadBackground(Long warehouseId, MultipartFile file) {
        // 校验库房存在
        WmsWarehouse warehouse = wmsWarehouseMapper.selectById(warehouseId);
        if (warehouse == null) {
            throw new BizException("库房不存在");
        }
        if (warehouse.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("库房已删除");
        }

        // 校验文件是否为空
        if (file == null || file.isEmpty()) {
            throw new BizException("底图文件不能为空");
        }

        // 校验文件大小
        if (file.getSize() > LayoutElementConstants.BACKGROUND_MAX_SIZE) {
            throw new BizException("底图文件大小超过10MB限制");
        }

        // 校验文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new BizException("底图文件名无效");
        }
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (!isValidExtension(extension)) {
            throw new BizException("仅支持JPG/PNG/SVG格式");
        }

        // 校验Content-Type
        String contentType = file.getContentType();
        if (contentType == null || !isValidContentType(contentType)) {
            throw new BizException("仅支持JPG/PNG/SVG格式");
        }

        // 生成版本号: yyyyMMddHHmmss
        String version = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        // 构建MinIO对象路径: warehouse-layout/{warehouseId}/background/{version}.{ext}
        String objectName = String.format(LayoutElementConstants.BACKGROUND_PATH_TEMPLATE,
                warehouseId, version, extension);

        // 保存当前版本号用于回滚
        String oldVersion = warehouse.getLayoutBackgroundVersion();

        try {
            // 上传文件至MinIO
            InputStream inputStream = file.getInputStream();
            String backgroundUrl = storageStrategy.upload(
                    LayoutElementConstants.BACKGROUND_BUCKET,
                    objectName,
                    inputStream,
                    contentType,
                    file.getSize()
            );

            // 更新库房的layoutBackgroundVersion
            LambdaUpdateWrapper<WmsWarehouse> updateWrapper = new LambdaUpdateWrapper<WmsWarehouse>()
                    .eq(WmsWarehouse::getId, warehouseId)
                    .set(WmsWarehouse::getLayoutBackgroundVersion, version);
            wmsWarehouseMapper.update(null, updateWrapper);

            WarehouseBackgroundVo result = new WarehouseBackgroundVo();
            result.setWarehouseId(warehouseId);
            result.setLayoutBackgroundVersion(version);
            result.setBackgroundUrl(backgroundUrl);
            return result;
        } catch (BizException e) {
            // 业务异常直接抛出
            throw e;
        } catch (Exception e) {
            // 上传失败时保留原底图不变，回滚版本号
            log.error("底图上传失败: warehouseId={}, version={}", warehouseId, version, e);
            throw new BizException("底图上传失败，请重试");
        }
    }

    /**
     * 获取库房底图文件流
     * 从MinIO读取文件流返回，不暴露MinIO内部地址
     * 若库房无底图（layoutBackgroundVersion为空），返回null
     *
     * @param warehouseId 库房ID
     * @return 底图文件流和Content-Type，无底图时返回null
     */
    @Override
    public BackgroundInputStream getBackground(Long warehouseId) {
        WmsWarehouse warehouse = wmsWarehouseMapper.selectById(warehouseId);
        if (warehouse == null) {
            throw new BizException("库房不存在");
        }
        if (warehouse.getLayoutBackgroundVersion() == null
                || warehouse.getLayoutBackgroundVersion().isBlank()) {
            // 无底图版本号，返回null表示404
            return null;
        }

        // 根据版本号查找MinIO中的文件
        // 由于扩展名不存储在版本号中，需要遍历允许的扩展名尝试获取
        String version = warehouse.getLayoutBackgroundVersion();
        String objectName = null;
        String contentType = null;

        for (int i = 0; i < LayoutElementConstants.BACKGROUND_ALLOWED_EXTENSIONS.length; i++) {
            String ext = LayoutElementConstants.BACKGROUND_ALLOWED_EXTENSIONS[i];
            String candidateName = String.format(LayoutElementConstants.BACKGROUND_PATH_TEMPLATE,
                    warehouseId, version, ext);
            try {
                InputStream inputStream = storageStrategy.download(
                        LayoutElementConstants.BACKGROUND_BUCKET, candidateName);
                // 找到文件
                objectName = candidateName;
                contentType = LayoutElementConstants.BACKGROUND_ALLOWED_CONTENT_TYPES[i];
                return new BackgroundInputStream(inputStream, contentType, objectName);
            } catch (Exception e) {
                // 该扩展名文件不存在，尝试下一个
                log.debug("尝试获取底图文件: {}, 不存在", candidateName);
            }
        }

        // 所有扩展名都未找到
        log.warn("底图文件未找到: warehouseId={}, version={}", warehouseId, version);
        return null;
    }

    /**
     * 删除库房底图引用
     * 将layoutBackgroundVersion置空，MinIO旧文件保留不删除（支持回滚）
     *
     * @param warehouseId 库房ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBackground(Long warehouseId) {
        WmsWarehouse warehouse = wmsWarehouseMapper.selectById(warehouseId);
        if (warehouse == null) {
            throw new BizException("库房不存在");
        }
        if (warehouse.getLayoutBackgroundVersion() == null
                || warehouse.getLayoutBackgroundVersion().isBlank()) {
            throw new BizException("库房无底图");
        }

        // 将layoutBackgroundVersion置空，MinIO旧文件保留不删除
        LambdaUpdateWrapper<WmsWarehouse> updateWrapper = new LambdaUpdateWrapper<WmsWarehouse>()
                .eq(WmsWarehouse::getId, warehouseId)
                .set(WmsWarehouse::getLayoutBackgroundVersion, (String) null);
        wmsWarehouseMapper.update(null, updateWrapper);
    }

    /**
     * 校验文件扩展名是否合法
     *
     * @param extension 文件扩展名
     * @return 是否合法
     */
    private boolean isValidExtension(String extension) {
        return Arrays.stream(LayoutElementConstants.BACKGROUND_ALLOWED_EXTENSIONS)
                .anyMatch(allowed -> allowed.equalsIgnoreCase(extension));
    }

    /**
     * 校验Content-Type是否合法
     *
     * @param contentType Content-Type
     * @return 是否合法
     */
    private boolean isValidContentType(String contentType) {
        return Arrays.stream(LayoutElementConstants.BACKGROUND_ALLOWED_CONTENT_TYPES)
                .anyMatch(allowed -> allowed.equalsIgnoreCase(contentType));
    }

}
