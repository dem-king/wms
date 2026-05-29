package com.wms.common.datascope;

import lombok.Getter;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 查询执行时生效的数据范围条件。
 */
@Getter
public class DataScopeCondition {

    private final boolean allData;

    private final Set<Long> deptIds;

    private final Long userId;

    private final boolean selfScope;

    private DataScopeCondition(boolean allData, Collection<Long> deptIds, Long userId, boolean selfScope) {
        this.allData = allData;
        this.deptIds = deptIds == null ? Set.of() : new LinkedHashSet<>(deptIds);
        this.userId = userId;
        this.selfScope = selfScope;
    }

    /**
     * 构建全部数据范围条件。
     *
     * @return 全部数据范围条件
     */
    public static DataScopeCondition all() {
        return new DataScopeCondition(true, Set.of(), null, false);
    }

    /**
     * 构建受限数据范围条件。
     *
     * @param deptIds   可访问部门ID集合
     * @param userId    当前用户ID
     * @param selfScope 是否允许本人数据
     * @return 受限数据范围条件
     */
    public static DataScopeCondition restricted(Collection<Long> deptIds, Long userId, boolean selfScope) {
        return new DataScopeCondition(false, deptIds, userId, selfScope);
    }

    /**
     * 判断是否存在任何限制条件。
     *
     * @return true表示存在限制条件
     */
    public boolean hasRestriction() {
        return !allData && (!deptIds.isEmpty() || (selfScope && userId != null));
    }
}
