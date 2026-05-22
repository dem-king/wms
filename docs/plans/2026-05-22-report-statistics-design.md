# 入库/出库/库存/借还统计报表 — 设计文档

> P4-33：入库/出库/库存/借还统计报表 | 模块：wms-report | 优先级：P1

---

## 一、需求概述

实现入库、出库、库存、借还四类统计报表，支持：
- **筛选维度**：时间范围（必填）+ 库房（可选）+ 物品分类（可选）
- **统计粒度**：汇总统计（总量/总金额/按分类分组）+ 趋势统计（按日/月时间序列）
- **数据方式**：预聚合 + 定时刷新（每日定时聚合业务数据到报表表，查询直接读取报表表）

---

## 二、方案选型

**选定方案A：按单据类型分表预聚合**

理由：入库/出库/归还是"流量型"数据（每日新增量），库存是"存量型"数据（每日快照），语义不同分表更清晰；各表独立不会互相影响，后续加报表（如报废/调拨）只需加表即可。

---

## 三、预聚合表设计

### 3.1 入库日聚合表 `report_inbound_daily`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键（雪花ID） |
| stat_date | DATE | 统计日期 |
| warehouse_id | BIGINT | 库房ID |
| category_id | BIGINT | 物品分类ID |
| total_quantity | INT | 入库总数量 |
| total_amount | DECIMAL(12,2) | 入库总金额 |
| order_count | INT | 入库单据数 |
| del_flag | TINYINT | 逻辑删除(0-正常 1-已删除) |
| create_time | DATETIME | 创建时间 |
| create_by | VARCHAR(64) | 创建人 |
| update_time | DATETIME | 更新时间 |
| update_by | VARCHAR(64) | 更新人 |

### 3.2 出库日聚合表 `report_outbound_daily`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键（雪花ID） |
| stat_date | DATE | 统计日期 |
| warehouse_id | BIGINT | 库房ID |
| category_id | BIGINT | 物品分类ID |
| total_quantity | INT | 出库总数量 |
| total_amount | DECIMAL(12,2) | 出库总金额 |
| order_count | INT | 出库单据数 |
| del_flag | TINYINT | 逻辑删除(0-正常 1-已删除) |
| create_time | DATETIME | 创建时间 |
| create_by | VARCHAR(64) | 创建人 |
| update_time | DATETIME | 更新时间 |
| update_by | VARCHAR(64) | 更新人 |

### 3.3 库存日快照表 `report_stock_daily`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键（雪花ID） |
| stat_date | DATE | 快照日期 |
| warehouse_id | BIGINT | 库房ID |
| category_id | BIGINT | 物品分类ID |
| total_quantity | INT | 库存总数量 |
| total_amount | DECIMAL(12,2) | 库存总金额 |
| del_flag | TINYINT | 逻辑删除(0-正常 1-已删除) |
| create_time | DATETIME | 创建时间 |
| create_by | VARCHAR(64) | 创建人 |
| update_time | DATETIME | 更新时间 |
| update_by | VARCHAR(64) | 更新人 |

### 3.4 归还日聚合表 `report_return_daily`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键（雪花ID） |
| stat_date | DATE | 统计日期 |
| warehouse_id | BIGINT | 库房ID |
| category_id | BIGINT | 物品分类ID |
| total_quantity | INT | 归还总数量 |
| normal_quantity | INT | 正常归还数量 |
| damaged_quantity | INT | 损坏归还数量 |
| order_count | INT | 归还单据数 |
| del_flag | TINYINT | 逻辑删除(0-正常 1-已删除) |
| create_time | DATETIME | 创建时间 |
| create_by | VARCHAR(64) | 创建人 |
| update_time | DATETIME | 更新时间 |
| update_by | VARCHAR(64) | 更新人 |

---

## 四、API设计

### 4.1 统一查询参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| startDate | LocalDate | 是 | 开始日期 |
| endDate | LocalDate | 是 | 结束日期 |
| warehouseId | Long | 否 | 库房ID |
| categoryId | Long | 否 | 物品分类ID |
| trendType | String | 否 | 趋势类型：DAILY/MONTHLY（仅趋势接口） |

### 4.2 入库统计 `/report/inbound`

| 方法 | 路径 | 说明 | 返回 |
|------|------|------|------|
| GET | /summary | 汇总统计 | InboundSummaryVo |
| GET | /trend | 趋势统计 | InboundTrendVo |
| GET | /category-distribution | 分类分布 | InboundDistributionVo |

### 4.3 出库统计 `/report/outbound` — 同上3个端点

### 4.4 库存统计 `/report/stock` — 同上3个端点

### 4.5 借还统计 `/report/return` — 同上3个端点，额外含正常/损坏归还比例

---

## 五、VO结构设计

### 5.1 入库统计VO（其他类似）

```java
// 汇总VO
InboundSummaryVo {
    Integer totalQuantity;       // 总入库数量
    BigDecimal totalAmount;     // 总入库金额
    Integer orderCount;         // 入库单据数
    List<CategorySummaryItem> categorySummaryList;  // 按分类汇总
}

CategorySummaryItem {
    Long categoryId;
    String categoryName;
    Integer quantity;
    BigDecimal amount;
}

// 趋势VO
InboundTrendVo {
    String trendType;           // DAILY / MONTHLY
    List<TrendItem> trendList;
}

TrendItem {
    String date;                // 日期或月份标识
    Integer quantity;
    BigDecimal amount;
}

// 分布VO
InboundDistributionVo {
    List<DistributionItem> distributionList;
}

DistributionItem {
    Long categoryId;
    String categoryName;
    Integer quantity;
    BigDecimal amount;
    BigDecimal percentage;      // 占比(%)
}
```

### 5.2 借还统计VO（额外字段）

```java
ReturnSummaryVo {
    Integer totalQuantity;
    Integer normalQuantity;     // 正常归还数
    Integer damagedQuantity;    // 损坏归还数
    BigDecimal normalRate;      // 正常归还率(%)
    Integer orderCount;
    List<CategorySummaryItem> categorySummaryList;
}
```

---

## 六、定时任务设计

| 任务类 | Cron表达式 | 执行时间 | 逻辑 |
|--------|-----------|---------|------|
| InboundDailyAggregationTask | `0 1 0 * * ?` | 每日00:01 | 查询前一日已完成入库单据，按库房+分类SQL聚合，写入report_inbound_daily |
| OutboundDailyAggregationTask | `0 2 0 * * ?` | 每日00:02 | 同上，出库单据 |
| StockDailySnapshotTask | `0 3 0 * * ?` | 每日00:03 | 查询当前wms_stock，按库房+分类汇总，写入report_stock_daily |
| ReturnDailyAggregationTask | `0 4 0 * * ?` | 每日00:04 | 查询前一日已完成归还单据，按库房+分类SQL聚合 |

### 聚合策略

- **幂等性**：写入前先删除同一stat_date的已有记录（先删后插），保证重复执行结果一致
- **SQL层面聚合**：使用`SELECT warehouse_id, category_id, SUM(quantity), SUM(amount) ... GROUP BY`，避免全表查询到内存
- **分类关联**：通过物品表`wms_item`的`category_id`字段关联分类

---

## 七、模块依赖变更

wms-report 的 pom.xml 新增依赖：

```xml
<dependency>
    <groupId>com.wms</groupId>
    <artifactId>wms-business</artifactId>
</dependency>
<dependency>
    <groupId>com.wms</groupId>
    <artifactId>wms-item</artifactId>
</dependency>
<dependency>
    <groupId>com.wms</groupId>
    <artifactId>wms-warehouse</artifactId>
</dependency>
```

---

## 八、代码分层

```
com.wms.report/
├── controller/
│   ├── InboundReportController.java
│   ├── OutboundReportController.java
│   ├── StockReportController.java
│   └── ReturnReportController.java
├── converter/
│   └── ReportConverter.java
├── domain/
│   ├── constant/
│   │   └── ReportConstants.java
│   ├── dto/
│   │   └── ReportQueryDto.java
│   ├── entity/
│   │   ├── ReportInboundDaily.java
│   │   ├── ReportOutboundDaily.java
│   │   ├── ReportStockDaily.java
│   │   └── ReportReturnDaily.java
│   └── vo/
│       ├── InboundReportVo.java
│       ├── OutboundReportVo.java
│       ├── StockReportVo.java
│       └── ReturnReportVo.java
├── mapper/
│   ├── ReportInboundDailyMapper.java
│   ├── ReportOutboundDailyMapper.java
│   ├── ReportStockDailyMapper.java
│   └── ReportReturnDailyMapper.java
├── service/
│   ├── InboundReportService.java
│   ├── OutboundReportService.java
│   ├── StockReportService.java
│   ├── ReturnReportService.java
│   └── impl/
│       ├── InboundReportServiceImpl.java
│       ├── OutboundReportServiceImpl.java
│       ├── StockReportServiceImpl.java
│       └── ReturnReportServiceImpl.java
└── task/
    ├── InboundDailyAggregationTask.java
    ├── OutboundDailyAggregationTask.java
    ├── StockDailySnapshotTask.java
    └── ReturnDailyAggregationTask.java
```

---

## 九、关键设计约束

- Controller不含业务逻辑，仅校验参数+调用Service
- 所有查询接口加 `@DataScope + @PreAuthorize("isAuthenticated()")`
- 返回VO而非Entity
- 无魔法数字，使用ReportConstants常量类
- 聚合Task中禁止N+1查询，使用SQL层面GROUP BY聚合
- 预聚合表数据用物理删除（报表中间数据无需逻辑删除），业务表数据仍用逻辑删除
- Entity字段必须有@Schema+JavaDoc注释，类和public方法必须有JavaDoc
