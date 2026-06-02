package com.wms.common.util;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.common.constant.DelFlagConstants;
import com.wms.common.domain.BaseEntity;

import java.util.Collection;

/**
 * Logic delete helper.
 * Uses an explicit UpdateWrapper SET for del_flag because @TableLogic fields are skipped by normal updateById paths.
 */
public final class LogicDeleteHelper {

    private static final String ID_COLUMN = "id";
    private static final String DEL_FLAG_COLUMN = "del_flag";

    private LogicDeleteHelper() {
    }

    /**
     * Mark one row as logically deleted by primary key.
     *
     * @param mapper      MyBatis-Plus Mapper
     * @param entityClass entity type
     * @param id          primary key
     * @param <T>         entity type
     * @return affected row count
     */
    public static <T extends BaseEntity> int markDeleted(BaseMapper<T> mapper, Class<T> entityClass, Long id) {
        UpdateWrapper<T> wrapper = deletedWrapper();
        wrapper.eq(ID_COLUMN, id);
        return mapper.update(newEntity(entityClass, id), wrapper);
    }

    /**
     * Mark rows as logically deleted by primary keys.
     *
     * @param mapper      MyBatis-Plus Mapper
     * @param entityClass entity type
     * @param ids         primary keys
     * @param <T>         entity type
     * @return affected row count
     */
    public static <T extends BaseEntity> int markDeletedByIds(
            BaseMapper<T> mapper,
            Class<T> entityClass,
            Collection<Long> ids
    ) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        UpdateWrapper<T> wrapper = deletedWrapper();
        wrapper.in(ID_COLUMN, ids);
        return mapper.update(newEntity(entityClass, null), wrapper);
    }

    /**
     * Mark rows as logically deleted by ids carried by the entity collection.
     *
     * @param mapper      MyBatis-Plus Mapper
     * @param entityClass entity type
     * @param entities    entities to delete
     * @param <T>         entity type
     * @return affected row count
     */
    public static <T extends BaseEntity> int markDeletedEntities(
            BaseMapper<T> mapper,
            Class<T> entityClass,
            Collection<T> entities
    ) {
        if (entities == null || entities.isEmpty()) {
            return 0;
        }
        return markDeletedByIds(
                mapper,
                entityClass,
                entities.stream().map(BaseEntity::getId).toList()
        );
    }

    private static <T extends BaseEntity> UpdateWrapper<T> deletedWrapper() {
        UpdateWrapper<T> wrapper = new UpdateWrapper<>();
        wrapper.set(DEL_FLAG_COLUMN, DelFlagConstants.DELETED);
        return wrapper;
    }

    private static <T extends BaseEntity> T newEntity(Class<T> entityClass, Long id) {
        try {
            T entity = entityClass.getDeclaredConstructor().newInstance();
            entity.setId(id);
            return entity;
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException("Entity class must provide a no-arg constructor: " + entityClass.getName(), e);
        }
    }
}
