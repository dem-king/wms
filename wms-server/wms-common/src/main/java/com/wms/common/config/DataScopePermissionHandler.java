package com.wms.common.config;

import com.baomidou.mybatisplus.extension.plugins.handler.MultiDataPermissionHandler;
import com.wms.common.context.DataScopeContext;
import com.wms.common.datascope.DataScopeCondition;
import com.wms.common.datascope.DataScopeSqlBuilder;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;

/**
 * MyBatis-Plus数据权限处理器。
 */
public class DataScopePermissionHandler implements MultiDataPermissionHandler {

    private final DataScopeSqlBuilder sqlBuilder = new DataScopeSqlBuilder();

    /**
     * 为当前表构建数据权限表达式。
     *
     * @param table 当前表
     * @param where 原始where条件
     * @param mappedStatementId MyBatis statement id
     * @return 数据权限表达式
     */
    @Override
    public Expression getSqlSegment(Table table, Expression where, String mappedStatementId) {
        DataScopeCondition condition = DataScopeContext.get();
        if (condition == null) {
            return null;
        }
        String alias = table.getAlias() == null ? table.getName() : table.getAlias().getName();
        String sqlSegment = sqlBuilder.buildSqlSegment(table.getName(), alias, condition);
        if (sqlSegment == null) {
            return null;
        }
        try {
            return CCJSqlParserUtil.parseCondExpression(sqlSegment);
        } catch (Exception e) {
            throw new IllegalStateException("构建数据权限SQL表达式失败: " + sqlSegment, e);
        }
    }
}
