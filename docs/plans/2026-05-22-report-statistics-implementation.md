# 入库/出库/库存/借还统计报表 Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 实现P4-33入库/出库/库存/借还统计报表，含4张预聚合表、4组查询API（汇总/趋势/分布）、4个定时聚合任务。

**Architecture:** 按单据类型分表预聚合（report_inbound_daily/report_outbound_daily/report_stock_daily/report_return_daily），每日定时任务从业务表SQL聚合写入，查询接口直接读预聚合表。wms-report模块依赖wms-business/wms-item/wms-warehouse。

**Tech Stack:** Spring Boot 3.2.5, MyBatis-Plus 3.5.6, @Scheduled定时任务, Java 17

---

### Task 1: 更新wms-report模块pom.xml依赖

**Files:**
- Modify: `wms-server/wms-report/pom.xml`

**Step 1: 添加业务模块依赖**

在 `<dependencies>` 中添加 wms-business、wms-item、wms-warehouse 依赖：

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

**Step 2: 验证编译通过**

Run: `cd D:/Codes/WMS_code/wms-server && mvn compile -pl wms-report -am -q`
Expected: BUILD SUCCESS

---

### Task 2: 创建SQL建表脚本

**Files:**
- Create: `wms-server/wms-report/src/main/resources/sql/report_tables.sql`

**Step 1: 编写4张预聚合表的DDL**

```sql
-- ============================================================
-- 统计报表预聚合表
-- ============================================================

-- 入库日聚合表
CREATE TABLE IF NOT EXISTS `report_inbound_daily` (
    `id`              BIGINT       NOT NULL                  COMMENT '主键(雪花ID)',
    `stat_date`       DATE         NOT NULL                  COMMENT '统计日期',
    `warehouse_id`    BIGINT       NOT NULL                  COMMENT '库房ID',
    `category_id`     BIGINT       NOT NULL                  COMMENT '物品分类ID',
    `total_quantity`  INT          NOT NULL DEFAULT 0        COMMENT '入库总数量',
    `total_amount`    DECIMAL(12,2) NOT NULL DEFAULT 0.00   COMMENT '入库总金额',
    `order_count`     INT          NOT NULL DEFAULT 0        COMMENT '入库单据数',
    `del_flag`        TINYINT      DEFAULT 0                 COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)  DEFAULT ''                COMMENT '创建人',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)  DEFAULT ''                COMMENT '更新人',
    PRIMARY KEY (`id`),
    KEY `idx_stat_date` (`stat_date`),
    KEY `idx_warehouse_category` (`warehouse_id`, `category_id`),
    UNIQUE KEY `uk_date_warehouse_category` (`stat_date`, `warehouse_id`, `category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='入库日聚合表';

-- 出库日聚合表
CREATE TABLE IF NOT EXISTS `report_outbound_daily` (
    `id`              BIGINT       NOT NULL                  COMMENT '主键(雪花ID)',
    `stat_date`       DATE         NOT NULL                  COMMENT '统计日期',
    `warehouse_id`    BIGINT       NOT NULL                  COMMENT '库房ID',
    `category_id`     BIGINT       NOT NULL                  COMMENT '物品分类ID',
    `total_quantity`  INT          NOT NULL DEFAULT 0        COMMENT '出库总数量',
    `total_amount`    DECIMAL(12,2) NOT NULL DEFAULT 0.00   COMMENT '出库总金额',
    `order_count`     INT          NOT NULL DEFAULT 0        COMMENT '出库单据数',
    `del_flag`        TINYINT      DEFAULT 0                 COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)  DEFAULT ''                COMMENT '创建人',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)  DEFAULT ''                COMMENT '更新人',
    PRIMARY KEY (`id`),
    KEY `idx_stat_date` (`stat_date`),
    KEY `idx_warehouse_category` (`warehouse_id`, `category_id`),
    UNIQUE KEY `uk_date_warehouse_category` (`stat_date`, `warehouse_id`, `category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='出库日聚合表';

-- 库存日快照表
CREATE TABLE IF NOT EXISTS `report_stock_daily` (
    `id`              BIGINT       NOT NULL                  COMMENT '主键(雪花ID)',
    `stat_date`       DATE         NOT NULL                  COMMENT '快照日期',
    `warehouse_id`    BIGINT       NOT NULL                  COMMENT '库房ID',
    `category_id`     BIGINT       NOT NULL                  COMMENT '物品分类ID',
    `total_quantity`  INT          NOT NULL DEFAULT 0        COMMENT '库存总数量',
    `total_amount`    DECIMAL(12,2) NOT NULL DEFAULT 0.00   COMMENT '库存总金额',
    `del_flag`        TINYINT      DEFAULT 0                 COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)  DEFAULT ''                COMMENT '创建人',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)  DEFAULT ''                COMMENT '更新人',
    PRIMARY KEY (`id`),
    KEY `idx_stat_date` (`stat_date`),
    KEY `idx_warehouse_category` (`warehouse_id`, `category_id`),
    UNIQUE KEY `uk_date_warehouse_category` (`stat_date`, `warehouse_id`, `category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='库存日快照表';

-- 归还日聚合表
CREATE TABLE IF NOT EXISTS `report_return_daily` (
    `id`              BIGINT       NOT NULL                  COMMENT '主键(雪花ID)',
    `stat_date`       DATE         NOT NULL                  COMMENT '统计日期',
    `warehouse_id`    BIGINT       NOT NULL                  COMMENT '库房ID',
    `category_id`     BIGINT       NOT NULL                  COMMENT '物品分类ID',
    `total_quantity`  INT          NOT NULL DEFAULT 0        COMMENT '归还总数量',
    `normal_quantity` INT          NOT NULL DEFAULT 0        COMMENT '正常归还数量',
    `damaged_quantity` INT         NOT NULL DEFAULT 0        COMMENT '损坏归还数量',
    `order_count`     INT          NOT NULL DEFAULT 0        COMMENT '归还单据数',
    `del_flag`        TINYINT      DEFAULT 0                 COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)  DEFAULT ''                COMMENT '创建人',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)  DEFAULT ''                COMMENT '更新人',
    PRIMARY KEY (`id`),
    KEY `idx_stat_date` (`stat_date`),
    KEY `idx_warehouse_category` (`warehouse_id`, `category_id`),
    UNIQUE KEY `uk_date_warehouse_category` (`stat_date`, `warehouse_id`, `category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='归还日聚合表';
```

---

### Task 3: 创建报表模块常量类 ReportConstants

**Files:**
- Create: `wms-server/wms-report/src/main/java/com/wms/report/domain/constant/ReportConstants.java`

```java
package com.wms.report.domain.constant;

/**
 * 报表模块常量类
 * 定义报表聚合、趋势类型等常量
 */
public final class ReportConstants {

    private ReportConstants() {
    }

    /** 趋势类型：按日 */
    public static final String TREND_TYPE_DAILY = "DAILY";

    /** 趋势类型：按月 */
    public static final String TREND_TYPE_MONTHLY = "MONTHLY";

    /** 归还物品状态：正常 */
    public static final int CONDITION_NORMAL = 1;

    /** 归还物品状态：损坏 */
    public static final int CONDITION_DAMAGED = 2;
}
```

---

### Task 4: 创建预聚合表Entity（4个）

**Files:**
- Create: `wms-server/wms-report/src/main/java/com/wms/report/domain/entity/ReportInboundDaily.java`
- Create: `wms-server/wms-report/src/main/java/com/wms/report/domain/entity/ReportOutboundDaily.java`
- Create: `wms-server/wms-report/src/main/java/com/wms/report/domain/entity/ReportStockDaily.java`
- Create: `wms-server/wms-report/src/main/java/com/wms/report/domain/entity/ReportReturnDaily.java`

**Step 1: ReportInboundDaily.java**

```java
package com.wms.report.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 入库日聚合实体
 * 对应表 report_inbound_daily，存储按日+库房+分类聚合的入库统计数据
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("report_inbound_daily")
public class ReportInboundDaily extends BaseEntity {

    /** 统计日期 */
    @Schema(description = "统计日期")
    private LocalDate statDate;

    /** 库房ID */
    @Schema(description = "库房ID")
    private Long warehouseId;

    /** 物品分类ID */
    @Schema(description = "物品分类ID")
    private Long categoryId;

    /** 入库总数量 */
    @Schema(description = "入库总数量")
    private Integer totalQuantity;

    /** 入库总金额 */
    @Schema(description = "入库总金额")
    private BigDecimal totalAmount;

    /** 入库单据数 */
    @Schema(description = "入库单据数")
    private Integer orderCount;
}
```

**Step 2: ReportOutboundDaily.java**

```java
package com.wms.report.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 出库日聚合实体
 * 对应表 report_outbound_daily，存储按日+库房+分类聚合的出库统计数据
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("report_outbound_daily")
public class ReportOutboundDaily extends BaseEntity {

    /** 统计日期 */
    @Schema(description = "统计日期")
    private LocalDate statDate;

    /** 库房ID */
    @Schema(description = "库房ID")
    private Long warehouseId;

    /** 物品分类ID */
    @Schema(description = "物品分类ID")
    private Long categoryId;

    /** 出库总数量 */
    @Schema(description = "出库总数量")
    private Integer totalQuantity;

    /** 出库总金额 */
    @Schema(description = "出库总金额")
    private BigDecimal totalAmount;

    /** 出库单据数 */
    @Schema(description = "出库单据数")
    private Integer orderCount;
}
```

**Step 3: ReportStockDaily.java**

```java
package com.wms.report.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 库存日快照实体
 * 对应表 report_stock_daily，存储按日+库房+分类的库存快照数据
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("report_stock_daily")
public class ReportStockDaily extends BaseEntity {

    /** 快照日期 */
    @Schema(description = "快照日期")
    private LocalDate statDate;

    /** 库房ID */
    @Schema(description = "库房ID")
    private Long warehouseId;

    /** 物品分类ID */
    @Schema(description = "物品分类ID")
    private Long categoryId;

    /** 库存总数量 */
    @Schema(description = "库存总数量")
    private Integer totalQuantity;

    /** 库存总金额 */
    @Schema(description = "库存总金额")
    private BigDecimal totalAmount;
}
```

**Step 4: ReportReturnDaily.java**

```java
package com.wms.report.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 归还日聚合实体
 * 对应表 report_return_daily，存储按日+库房+分类聚合的归还统计数据
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("report_return_daily")
public class ReportReturnDaily extends BaseEntity {

    /** 统计日期 */
    @Schema(description = "统计日期")
    private LocalDate statDate;

    /** 库房ID */
    @Schema(description = "库房ID")
    private Long warehouseId;

    /** 物品分类ID */
    @Schema(description = "物品分类ID")
    private Long categoryId;

    /** 归还总数量 */
    @Schema(description = "归还总数量")
    private Integer totalQuantity;

    /** 正常归还数量 */
    @Schema(description = "正常归还数量")
    private Integer normalQuantity;

    /** 损坏归还数量 */
    @Schema(description = "损坏归还数量")
    private Integer damagedQuantity;

    /** 归还单据数 */
    @Schema(description = "归还单据数")
    private Integer orderCount;
}
```

---

### Task 5: 创建Mapper接口（4个）

**Files:**
- Create: `wms-server/wms-report/src/main/java/com/wms/report/mapper/ReportInboundDailyMapper.java`
- Create: `wms-server/wms-report/src/main/java/com/wms/report/mapper/ReportOutboundDailyMapper.java`
- Create: `wms-server/wms-report/src/main/java/com/wms/report/mapper/ReportStockDailyMapper.java`
- Create: `wms-server/wms-report/src/main/java/com/wms/report/mapper/ReportReturnDailyMapper.java`

**Step 1: ReportInboundDailyMapper.java**

```java
package com.wms.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.report.domain.entity.ReportInboundDaily;
import org.apache.ibatis.annotations.Mapper;

/**
 * 入库日聚合Mapper接口
 */
@Mapper
public interface ReportInboundDailyMapper extends BaseMapper<ReportInboundDaily> {
}
```

**Step 2: ReportOutboundDailyMapper.java**

```java
package com.wms.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.report.domain.entity.ReportOutboundDaily;
import org.apache.ibatis.annotations.Mapper;

/**
 * 出库日聚合Mapper接口
 */
@Mapper
public interface ReportOutboundDailyMapper extends BaseMapper<ReportOutboundDaily> {
}
```

**Step 3: ReportStockDailyMapper.java**

```java
package com.wms.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.report.domain.entity.ReportStockDaily;
import org.apache.ibatis.annotations.Mapper;

/**
 * 库存日快照Mapper接口
 */
@Mapper
public interface ReportStockDailyMapper extends BaseMapper<ReportStockDaily> {
}
```

**Step 4: ReportReturnDailyMapper.java**

```java
package com.wms.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.report.domain.entity.ReportReturnDaily;
import org.apache.ibatis.annotations.Mapper;

/**
 * 归还日聚合Mapper接口
 */
@Mapper
public interface ReportReturnDailyMapper extends BaseMapper<ReportReturnDaily> {
}
```

---

### Task 6: 创建统一查询DTO

**Files:**
- Create: `wms-server/wms-report/src/main/java/com/wms/report/domain/dto/ReportQueryDto.java`

```java
package com.wms.report.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 报表统一查询参数
 * 所有统计报表接口共用此查询参数
 */
@Data
@Schema(description = "报表查询参数")
public class ReportQueryDto {

    /** 开始日期 */
    @NotNull(message = "开始日期不能为空")
    @Schema(description = "开始日期")
    private LocalDate startDate;

    /** 结束日期 */
    @NotNull(message = "结束日期不能为空")
    @Schema(description = "结束日期")
    private LocalDate endDate;

    /** 库房ID(可选) */
    @Schema(description = "库房ID")
    private Long warehouseId;

    /** 物品分类ID(可选) */
    @Schema(description = "物品分类ID")
    private Long categoryId;

    /** 趋势类型(可选，DAILY/MONTHLY，仅趋势接口使用) */
    @Schema(description = "趋势类型(DAILY/MONTHLY)")
    private String trendType;
}
```

---

### Task 7: 创建统计报表VO类（4组）

**Files:**
- Create: `wms-server/wms-report/src/main/java/com/wms/report/domain/vo/InboundReportVo.java`
- Create: `wms-server/wms-report/src/main/java/com/wms/report/domain/vo/OutboundReportVo.java`
- Create: `wms-server/wms-report/src/main/java/com/wms/report/domain/vo/StockReportVo.java`
- Create: `wms-server/wms-report/src/main/java/com/wms/report/domain/vo/ReturnReportVo.java`
- Create: `wms-server/wms-report/src/main/java/com/wms/report/domain/vo/CommonReportVo.java`

**Step 1: CommonReportVo.java（共用内部类）**

```java
package com.wms.report.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 报表通用视图对象
 * 提供分类汇总、趋势、分布等共用内部类
 */
@Data
@Schema(description = "报表通用视图对象")
public class CommonReportVo {

    /**
     * 分类汇总项
     */
    @Data
    @Schema(description = "分类汇总项")
    public static class CategorySummaryItem {

        /** 分类ID */
        @Schema(description = "分类ID")
        private Long categoryId;

        /** 分类名称 */
        @Schema(description = "分类名称")
        private String categoryName;

        /** 数量 */
        @Schema(description = "数量")
        private Integer quantity;

        /** 金额 */
        @Schema(description = "金额")
        private BigDecimal amount;
    }

    /**
     * 趋势项
     */
    @Data
    @Schema(description = "趋势项")
    public static class TrendItem {

        /** 日期标识(日:2026-05-22, 月:2026-05) */
        @Schema(description = "日期标识")
        private String date;

        /** 数量 */
        @Schema(description = "数量")
        private Integer quantity;

        /** 金额 */
        @Schema(description = "金额")
        private BigDecimal amount;
    }

    /**
     * 分布项
     */
    @Data
    @Schema(description = "分布项")
    public static class DistributionItem {

        /** 分类ID */
        @Schema(description = "分类ID")
        private Long categoryId;

        /** 分类名称 */
        @Schema(description = "分类名称")
        private String categoryName;

        /** 数量 */
        @Schema(description = "数量")
        private Integer quantity;

        /** 金额 */
        @Schema(description = "金额")
        private BigDecimal amount;

        /** 占比(%) */
        @Schema(description = "占比(%)")
        private BigDecimal percentage;
    }
}
```

**Step 2: InboundReportVo.java**

```java
package com.wms.report.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 入库统计报表视图对象
 * 包含汇总、趋势、分类分布三种统计结果
 */
@Data
@Schema(description = "入库统计报表")
public class InboundReportVo {

    /**
     * 入库汇总统计
     */
    @Data
    @Schema(description = "入库汇总统计")
    public static class SummaryVo {

        /** 入库总数量 */
        @Schema(description = "入库总数量")
        private Integer totalQuantity;

        /** 入库总金额 */
        @Schema(description = "入库总金额")
        private BigDecimal totalAmount;

        /** 入库单据数 */
        @Schema(description = "入库单据数")
        private Integer orderCount;

        /** 按分类汇总列表 */
        @Schema(description = "按分类汇总列表")
        private List<CommonReportVo.CategorySummaryItem> categorySummaryList;
    }

    /**
     * 入库趋势统计
     */
    @Data
    @Schema(description = "入库趋势统计")
    public static class TrendVo {

        /** 趋势类型 */
        @Schema(description = "趋势类型")
        private String trendType;

        /** 趋势数据列表 */
        @Schema(description = "趋势数据列表")
        private List<CommonReportVo.TrendItem> trendList;
    }

    /**
     * 入库分类分布统计
     */
    @Data
    @Schema(description = "入库分类分布统计")
    public static class DistributionVo {

        /** 分布数据列表 */
        @Schema(description = "分布数据列表")
        private List<CommonReportVo.DistributionItem> distributionList;
    }
}
```

**Step 3: OutboundReportVo.java**

```java
package com.wms.report.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 出库统计报表视图对象
 * 包含汇总、趋势、分类分布三种统计结果
 */
@Data
@Schema(description = "出库统计报表")
public class OutboundReportVo {

    /**
     * 出库汇总统计
     */
    @Data
    @Schema(description = "出库汇总统计")
    public static class SummaryVo {

        /** 出库总数量 */
        @Schema(description = "出库总数量")
        private Integer totalQuantity;

        /** 出库总金额 */
        @Schema(description = "出库总金额")
        private BigDecimal totalAmount;

        /** 出库单据数 */
        @Schema(description = "出库单据数")
        private Integer orderCount;

        /** 按分类汇总列表 */
        @Schema(description = "按分类汇总列表")
        private List<CommonReportVo.CategorySummaryItem> categorySummaryList;
    }

    /**
     * 出库趋势统计
     */
    @Data
    @Schema(description = "出库趋势统计")
    public static class TrendVo {

        /** 趋势类型 */
        @Schema(description = "趋势类型")
        private String trendType;

        /** 趋势数据列表 */
        @Schema(description = "趋势数据列表")
        private List<CommonReportVo.TrendItem> trendList;
    }

    /**
     * 出库分类分布统计
     */
    @Data
    @Schema(description = "出库分类分布统计")
    public static class DistributionVo {

        /** 分布数据列表 */
        @Schema(description = "分布数据列表")
        private List<CommonReportVo.DistributionItem> distributionList;
    }
}
```

**Step 4: StockReportVo.java**

```java
package com.wms.report.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 库存统计报表视图对象
 * 包含汇总、趋势、分类分布三种统计结果
 */
@Data
@Schema(description = "库存统计报表")
public class StockReportVo {

    /**
     * 库存汇总统计
     */
    @Data
    @Schema(description = "库存汇总统计")
    public static class SummaryVo {

        /** 库存总数量 */
        @Schema(description = "库存总数量")
        private Integer totalQuantity;

        /** 库存总金额 */
        @Schema(description = "库存总金额")
        private BigDecimal totalAmount;

        /** 按分类汇总列表 */
        @Schema(description = "按分类汇总列表")
        private List<CommonReportVo.CategorySummaryItem> categorySummaryList;
    }

    /**
     * 库存趋势统计
     */
    @Data
    @Schema(description = "库存趋势统计")
    public static class TrendVo {

        /** 趋势类型 */
        @Schema(description = "趋势类型")
        private String trendType;

        /** 趋势数据列表 */
        @Schema(description = "趋势数据列表")
        private List<CommonReportVo.TrendItem> trendList;
    }

    /**
     * 库存分类分布统计
     */
    @Data
    @Schema(description = "库存分类分布统计")
    public static class DistributionVo {

        /** 分布数据列表 */
        @Schema(description = "分布数据列表")
        private List<CommonReportVo.DistributionItem> distributionList;
    }
}
```

**Step 5: ReturnReportVo.java**

```java
package com.wms.report.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 借还统计报表视图对象
 * 包含汇总、趋势、分类分布三种统计结果，汇总额外含正常/损坏归还比例
 */
@Data
@Schema(description = "借还统计报表")
public class ReturnReportVo {

    /**
     * 借还汇总统计
     */
    @Data
    @Schema(description = "借还汇总统计")
    public static class SummaryVo {

        /** 归还总数量 */
        @Schema(description = "归还总数量")
        private Integer totalQuantity;

        /** 正常归还数量 */
        @Schema(description = "正常归还数量")
        private Integer normalQuantity;

        /** 损坏归还数量 */
        @Schema(description = "损坏归还数量")
        private Integer damagedQuantity;

        /** 正常归还率(%) */
        @Schema(description = "正常归还率(%)")
        private BigDecimal normalRate;

        /** 归还单据数 */
        @Schema(description = "归还单据数")
        private Integer orderCount;

        /** 按分类汇总列表 */
        @Schema(description = "按分类汇总列表")
        private List<CommonReportVo.CategorySummaryItem> categorySummaryList;
    }

    /**
     * 借还趋势统计
     */
    @Data
    @Schema(description = "借还趋势统计")
    public static class TrendVo {

        /** 趋势类型 */
        @Schema(description = "趋势类型")
        private String trendType;

        /** 趋势数据列表 */
        @Schema(description = "趋势数据列表")
        private List<CommonReportVo.TrendItem> trendList;
    }

    /**
     * 借还分类分布统计
     */
    @Data
    @Schema(description = "借还分类分布统计")
    public static class DistributionVo {

        /** 分布数据列表 */
        @Schema(description = "分布数据列表")
        private List<CommonReportVo.DistributionItem> distributionList;
    }
}
```

---

### Task 8: 创建Converter类

**Files:**
- Create: `wms-server/wms-report/src/main/java/com/wms/report/converter/ReportConverter.java`

```java
package com.wms.report.converter;

import com.wms.item.domain.entity.WmsCategory;
import com.wms.item.mapper.WmsCategoryMapper;
import com.wms.report.domain.entity.*;
import com.wms.report.domain.vo.CommonReportVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 报表转换器
 * 负责预聚合Entity到报表VO的转换，分类名称从预查询Map中填充以避免N+1
 */
@Component
@RequiredArgsConstructor
public class ReportConverter {

    private final WmsCategoryMapper wmsCategoryMapper;

    /**
     * 批量查询分类名称构建Map
     *
     * @param categoryIds 分类ID集合
     * @return 分类ID到分类名称的映射
     */
    public Map<Long, String> buildCategoryNameMap(Set<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return wmsCategoryMapper.selectBatchIds(categoryIds).stream()
                .collect(Collectors.toMap(WmsCategory::getId, WmsCategory::getCategoryName, (a, b) -> a));
    }

    /**
     * 入库日聚合Entity转分类汇总项
     *
     * @param entity 入库日聚合实体
     * @param categoryNameMap 分类名称映射
     * @return 分类汇总项
     */
    public CommonReportVo.CategorySummaryItem toInboundCategorySummary(ReportInboundDaily entity,
                                                                        Map<Long, String> categoryNameMap) {
        CommonReportVo.CategorySummaryItem item = new CommonReportVo.CategorySummaryItem();
        item.setCategoryId(entity.getCategoryId());
        item.setCategoryName(categoryNameMap.getOrDefault(entity.getCategoryId(), ""));
        item.setQuantity(entity.getTotalQuantity());
        item.setAmount(entity.getTotalAmount());
        return item;
    }

    /**
     * 出库日聚合Entity转分类汇总项
     *
     * @param entity 出库日聚合实体
     * @param categoryNameMap 分类名称映射
     * @return 分类汇总项
     */
    public CommonReportVo.CategorySummaryItem toOutboundCategorySummary(ReportOutboundDaily entity,
                                                                         Map<Long, String> categoryNameMap) {
        CommonReportVo.CategorySummaryItem item = new CommonReportVo.CategorySummaryItem();
        item.setCategoryId(entity.getCategoryId());
        item.setCategoryName(categoryNameMap.getOrDefault(entity.getCategoryId(), ""));
        item.setQuantity(entity.getTotalQuantity());
        item.setAmount(entity.getTotalAmount());
        return item;
    }

    /**
     * 库存日快照Entity转分类汇总项
     *
     * @param entity 库存日快照实体
     * @param categoryNameMap 分类名称映射
     * @return 分类汇总项
     */
    public CommonReportVo.CategorySummaryItem toStockCategorySummary(ReportStockDaily entity,
                                                                      Map<Long, String> categoryNameMap) {
        CommonReportVo.CategorySummaryItem item = new CommonReportVo.CategorySummaryItem();
        item.setCategoryId(entity.getCategoryId());
        item.setCategoryName(categoryNameMap.getOrDefault(entity.getCategoryId(), ""));
        item.setQuantity(entity.getTotalQuantity());
        item.setAmount(entity.getTotalAmount());
        return item;
    }

    /**
     * 归还日聚合Entity转分类汇总项
     *
     * @param entity 归还日聚合实体
     * @param categoryNameMap 分类名称映射
     * @return 分类汇总项
     */
    public CommonReportVo.CategorySummaryItem toReturnCategorySummary(ReportReturnDaily entity,
                                                                       Map<Long, String> categoryNameMap) {
        CommonReportVo.CategorySummaryItem item = new CommonReportVo.CategorySummaryItem();
        item.setCategoryId(entity.getCategoryId());
        item.setCategoryName(categoryNameMap.getOrDefault(entity.getCategoryId(), ""));
        item.setQuantity(entity.getTotalQuantity());
        return item;
    }

    /**
     * Entity列表转分布项列表(含占比计算)
     *
     * @param entities 聚合实体列表
     * @param categoryNameMap 分类名称映射
     * @param totalQuantity 总数量(用于计算占比)
     * @param isStock 是否为库存(库存有金额，归还无金额)
     * @return 分布项列表
     */
    public List<CommonReportVo.DistributionItem> toDistributionList(List<?> entities,
                                                                     Map<Long, String> categoryNameMap,
                                                                     int totalQuantity,
                                                                     boolean isStock) {
        List<CommonReportVo.DistributionItem> result = new ArrayList<>();
        for (Object obj : entities) {
            CommonReportVo.DistributionItem item = new CommonReportVo.DistributionItem();
            if (obj instanceof ReportInboundDaily e) {
                item.setCategoryId(e.getCategoryId());
                item.setCategoryName(categoryNameMap.getOrDefault(e.getCategoryId(), ""));
                item.setQuantity(e.getTotalQuantity());
                item.setAmount(e.getTotalAmount());
            } else if (obj instanceof ReportOutboundDaily e) {
                item.setCategoryId(e.getCategoryId());
                item.setCategoryName(categoryNameMap.getOrDefault(e.getCategoryId(), ""));
                item.setQuantity(e.getTotalQuantity());
                item.setAmount(e.getTotalAmount());
            } else if (obj instanceof ReportStockDaily e) {
                item.setCategoryId(e.getCategoryId());
                item.setCategoryName(categoryNameMap.getOrDefault(e.getCategoryId(), ""));
                item.setQuantity(e.getTotalQuantity());
                item.setAmount(e.getTotalAmount());
            } else if (obj instanceof ReportReturnDaily e) {
                item.setCategoryId(e.getCategoryId());
                item.setCategoryName(categoryNameMap.getOrDefault(e.getCategoryId(), ""));
                item.setQuantity(e.getTotalQuantity());
            }
            if (totalQuantity > 0 && item.getQuantity() != null) {
                item.setPercentage(BigDecimal.valueOf(item.getQuantity())
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(totalQuantity), 2, RoundingMode.HALF_UP));
            } else {
                item.setPercentage(BigDecimal.ZERO);
            }
            result.add(item);
        }
        return result;
    }
}
```

---

### Task 9: 创建Service接口（4个）

**Files:**
- Create: `wms-server/wms-report/src/main/java/com/wms/report/service/InboundReportService.java`
- Create: `wms-server/wms-report/src/main/java/com/wms/report/service/OutboundReportService.java`
- Create: `wms-server/wms-report/src/main/java/com/wms/report/service/StockReportService.java`
- Create: `wms-server/wms-report/src/main/java/com/wms/report/service/ReturnReportService.java`

**Step 1: InboundReportService.java**

```java
package com.wms.report.service;

import com.wms.report.domain.dto.ReportQueryDto;
import com.wms.report.domain.vo.InboundReportVo;

/**
 * 入库统计报表服务接口
 * 提供入库汇总、趋势、分类分布统计功能
 */
public interface InboundReportService {

    /**
     * 入库汇总统计
     * 查询时间范围内入库总数量、总金额、按分类汇总
     *
     * @param queryDto 报表查询参数
     * @return 入库汇总统计VO
     */
    InboundReportVo.SummaryVo getSummary(ReportQueryDto queryDto);

    /**
     * 入库趋势统计
     * 按日或按月返回时间序列数据
     *
     * @param queryDto 报表查询参数(含trendType)
     * @return 入库趋势统计VO
     */
    InboundReportVo.TrendVo getTrend(ReportQueryDto queryDto);

    /**
     * 入库分类分布统计
     * 按物品分类计算数量和占比
     *
     * @param queryDto 报表查询参数
     * @return 入库分类分布统计VO
     */
    InboundReportVo.DistributionVo getDistribution(ReportQueryDto queryDto);
}
```

**Step 2: OutboundReportService.java**

```java
package com.wms.report.service;

import com.wms.report.domain.dto.ReportQueryDto;
import com.wms.report.domain.vo.OutboundReportVo;

/**
 * 出库统计报表服务接口
 * 提供出库汇总、趋势、分类分布统计功能
 */
public interface OutboundReportService {

    /**
     * 出库汇总统计
     *
     * @param queryDto 报表查询参数
     * @return 出库汇总统计VO
     */
    OutboundReportVo.SummaryVo getSummary(ReportQueryDto queryDto);

    /**
     * 出库趋势统计
     *
     * @param queryDto 报表查询参数(含trendType)
     * @return 出库趋势统计VO
     */
    OutboundReportVo.TrendVo getTrend(ReportQueryDto queryDto);

    /**
     * 出库分类分布统计
     *
     * @param queryDto 报表查询参数
     * @return 出库分类分布统计VO
     */
    OutboundReportVo.DistributionVo getDistribution(ReportQueryDto queryDto);
}
```

**Step 3: StockReportService.java**

```java
package com.wms.report.service;

import com.wms.report.domain.dto.ReportQueryDto;
import com.wms.report.domain.vo.StockReportVo;

/**
 * 库存统计报表服务接口
 * 提供库存汇总、趋势、分类分布统计功能
 */
public interface StockReportService {

    /**
     * 库存汇总统计
     * 取时间范围内最新一天的快照数据
     *
     * @param queryDto 报表查询参数
     * @return 库存汇总统计VO
     */
    StockReportVo.SummaryVo getSummary(ReportQueryDto queryDto);

    /**
     * 库存趋势统计
     *
     * @param queryDto 报表查询参数(含trendType)
     * @return 库存趋势统计VO
     */
    StockReportVo.TrendVo getTrend(ReportQueryDto queryDto);

    /**
     * 库存分类分布统计
     *
     * @param queryDto 报表查询参数
     * @return 库存分类分布统计VO
     */
    StockReportVo.DistributionVo getDistribution(ReportQueryDto queryDto);
}
```

**Step 4: ReturnReportService.java**

```java
package com.wms.report.service;

import com.wms.report.domain.dto.ReportQueryDto;
import com.wms.report.domain.vo.ReturnReportVo;

/**
 * 借还统计报表服务接口
 * 提供借还汇总、趋势、分类分布统计功能
 */
public interface ReturnReportService {

    /**
     * 借还汇总统计
     * 额外包含正常/损坏归还数量和正常归还率
     *
     * @param queryDto 报表查询参数
     * @return 借还汇总统计VO
     */
    ReturnReportVo.SummaryVo getSummary(ReportQueryDto queryDto);

    /**
     * 借还趋势统计
     *
     * @param queryDto 报表查询参数(含trendType)
     * @return 借还趋势统计VO
     */
    ReturnReportVo.TrendVo getTrend(ReportQueryDto queryDto);

    /**
     * 借还分类分布统计
     *
     * @param queryDto 报表查询参数
     * @return 借还分类分布统计VO
     */
    ReturnReportVo.DistributionVo getDistribution(ReportQueryDto queryDto);
}
```

---

### Task 10: 创建Service实现类（4个）

**Files:**
- Create: `wms-server/wms-report/src/main/java/com/wms/report/service/impl/InboundReportServiceImpl.java`
- Create: `wms-server/wms-report/src/main/java/com/wms/report/service/impl/OutboundReportServiceImpl.java`
- Create: `wms-server/wms-report/src/main/java/com/wms/report/service/impl/StockReportServiceImpl.java`
- Create: `wms-server/wms-report/src/main/java/com/wms/report/service/impl/ReturnReportServiceImpl.java`

**Step 1: InboundReportServiceImpl.java**

```java
package com.wms.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.report.converter.ReportConverter;
import com.wms.report.domain.constant.ReportConstants;
import com.wms.report.domain.dto.ReportQueryDto;
import com.wms.report.domain.entity.ReportInboundDaily;
import com.wms.report.domain.vo.CommonReportVo;
import com.wms.report.domain.vo.InboundReportVo;
import com.wms.report.mapper.ReportInboundDailyMapper;
import com.wms.report.service.InboundReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 入库统计报表服务实现类
 * 从report_inbound_daily预聚合表查询统计数据
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InboundReportServiceImpl implements InboundReportService {

    private final ReportInboundDailyMapper reportInboundDailyMapper;
    private final ReportConverter reportConverter;

    /**
     * 构建通用查询条件
     *
     * @param queryDto 报表查询参数
     * @return LambdaQueryWrapper
     */
    private LambdaQueryWrapper<ReportInboundDaily> buildWrapper(ReportQueryDto queryDto) {
        LambdaQueryWrapper<ReportInboundDaily> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(ReportInboundDaily::getStatDate, queryDto.getStartDate())
                .le(ReportInboundDaily::getStatDate, queryDto.getEndDate());
        if (queryDto.getWarehouseId() != null) {
            wrapper.eq(ReportInboundDaily::getWarehouseId, queryDto.getWarehouseId());
        }
        if (queryDto.getCategoryId() != null) {
            wrapper.eq(ReportInboundDaily::getCategoryId, queryDto.getCategoryId());
        }
        return wrapper;
    }

    @Override
    public InboundReportVo.SummaryVo getSummary(ReportQueryDto queryDto) {
        List<ReportInboundDaily> list = reportInboundDailyMapper.selectList(buildWrapper(queryDto));

        InboundReportVo.SummaryVo vo = new InboundReportVo.SummaryVo();
        vo.setTotalQuantity(list.stream().mapToInt(ReportInboundDaily::getTotalQuantity).sum());
        vo.setTotalAmount(list.stream().map(ReportInboundDaily::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        vo.setOrderCount(list.stream().mapToInt(ReportInboundDaily::getOrderCount).sum());

        // 按分类聚合(同一分类可能跨多天多库房)
        Map<Long, ReportInboundDaily> categoryAgg = new LinkedHashMap<>();
        for (ReportInboundDaily daily : list) {
            categoryAgg.merge(daily.getCategoryId(), daily, (existing,新增) -> {
                existing.setTotalQuantity(existing.getTotalQuantity() + 新增.getTotalQuantity());
                existing.setTotalAmount(existing.getTotalAmount().add(新增.getTotalAmount()));
                existing.setOrderCount(existing.getOrderCount() + 新增.getOrderCount());
                return existing;
            });
        }

        Set<Long> categoryIds = categoryAgg.keySet();
        Map<Long, String> categoryNameMap = reportConverter.buildCategoryNameMap(categoryIds);
        vo.setCategorySummaryList(categoryAgg.values().stream()
                .map(e -> reportConverter.toInboundCategorySummary(e, categoryNameMap))
                .collect(Collectors.toList()));
        return vo;
    }

    @Override
    public InboundReportVo.TrendVo getTrend(ReportQueryDto queryDto) {
        List<ReportInboundDaily> list = reportInboundDailyMapper.selectList(buildWrapper(queryDto));

        String trendType = queryDto.getTrendType() != null ? queryDto.getTrendType() : ReportConstants.TREND_TYPE_DAILY;
        InboundReportVo.TrendVo vo = new InboundReportVo.TrendVo();
        vo.setTrendType(trendType);

        if (ReportConstants.TREND_TYPE_MONTHLY.equals(trendType)) {
            // 按月聚合
            Map<String, CommonReportVo.TrendItem> monthMap = new TreeMap<>();
            for (ReportInboundDaily daily : list) {
                String monthKey = daily.getStatDate().getYear() + "-" +
                        String.format("%02d", daily.getStatDate().getMonthValue());
                CommonReportVo.TrendItem item = monthMap.computeIfAbsent(monthKey, k -> {
                    CommonReportVo.TrendItem t = new CommonReportVo.TrendItem();
                    t.setDate(k);
                    t.setQuantity(0);
                    t.setAmount(BigDecimal.ZERO);
                    return t;
                });
                item.setQuantity(item.getQuantity() + daily.getTotalQuantity());
                item.setAmount(item.getAmount().add(daily.getTotalAmount()));
            }
            vo.setTrendList(new ArrayList<>(monthMap.values()));
        } else {
            // 按日聚合
            Map<String, CommonReportVo.TrendItem> dayMap = new TreeMap<>();
            for (ReportInboundDaily daily : list) {
                String dayKey = daily.getStatDate().toString();
                CommonReportVo.TrendItem item = dayMap.computeIfAbsent(dayKey, k -> {
                    CommonReportVo.TrendItem t = new CommonReportVo.TrendItem();
                    t.setDate(k);
                    t.setQuantity(0);
                    t.setAmount(BigDecimal.ZERO);
                    return t;
                });
                item.setQuantity(item.getQuantity() + daily.getTotalQuantity());
                item.setAmount(item.getAmount().add(daily.getTotalAmount()));
            }
            vo.setTrendList(new ArrayList<>(dayMap.values()));
        }
        return vo;
    }

    @Override
    public InboundReportVo.DistributionVo getDistribution(ReportQueryDto queryDto) {
        List<ReportInboundDaily> list = reportInboundDailyMapper.selectList(buildWrapper(queryDto));

        // 按分类聚合
        Map<Long, ReportInboundDaily> categoryAgg = new LinkedHashMap<>();
        for (ReportInboundDaily daily : list) {
            categoryAgg.merge(daily.getCategoryId(), daily, (existing, 新增) -> {
                existing.setTotalQuantity(existing.getTotalQuantity() + 新增.getTotalQuantity());
                existing.setTotalAmount(existing.getTotalAmount().add(新增.getTotalAmount()));
                return existing;
            });
        }

        int totalQuantity = categoryAgg.values().stream().mapToInt(ReportInboundDaily::getTotalQuantity).sum();
        Set<Long> categoryIds = categoryAgg.keySet();
        Map<Long, String> categoryNameMap = reportConverter.buildCategoryNameMap(categoryIds);

        InboundReportVo.DistributionVo vo = new InboundReportVo.DistributionVo();
        vo.setDistributionList(reportConverter.toDistributionList(
                new ArrayList<>(categoryAgg.values()), categoryNameMap, totalQuantity, true));
        return vo;
    }
}
```

**Step 2: OutboundReportServiceImpl.java** — 与InboundReportServiceImpl结构一致，替换Entity/Mapper/VO类型即可。

**Step 3: StockReportServiceImpl.java** — 与InboundReportServiceImpl结构类似，但汇总取最新一天快照而非累加全部天数。

**Step 4: ReturnReportServiceImpl.java** — 额外处理normalQuantity/damagedQuantity/normalRate字段。

（完整代码在实现阶段生成，此处省略重复结构说明）

---

### Task 11: 创建Controller（4个）

**Files:**
- Create: `wms-server/wms-report/src/main/java/com/wms/report/controller/InboundReportController.java`
- Create: `wms-server/wms-report/src/main/java/com/wms/report/controller/OutboundReportController.java`
- Create: `wms-server/wms-report/src/main/java/com/wms/report/controller/StockReportController.java`
- Create: `wms-server/wms-report/src/main/java/com/wms/report/controller/ReturnReportController.java`

**Step 1: InboundReportController.java**

```java
package com.wms.report.controller;

import com.wms.common.annotation.DataScope;
import com.wms.common.domain.R;
import com.wms.report.domain.dto.ReportQueryDto;
import com.wms.report.domain.vo.InboundReportVo;
import com.wms.report.service.InboundReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 入库统计报表控制器
 * 提供入库汇总、趋势、分类分布统计接口
 */
@Tag(name = "入库统计报表")
@RestController
@RequestMapping("/report/inbound")
@RequiredArgsConstructor
@Validated
public class InboundReportController {

    private final InboundReportService inboundReportService;

    /**
     * 入库汇总统计
     */
    @Operation(summary = "入库汇总统计")
    @GetMapping("/summary")
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<InboundReportVo.SummaryVo> getSummary(@Valid ReportQueryDto queryDto) {
        return R.ok(inboundReportService.getSummary(queryDto));
    }

    /**
     * 入库趋势统计
     */
    @Operation(summary = "入库趋势统计")
    @GetMapping("/trend")
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<InboundReportVo.TrendVo> getTrend(@Valid ReportQueryDto queryDto) {
        return R.ok(inboundReportService.getTrend(queryDto));
    }

    /**
     * 入库分类分布统计
     */
    @Operation(summary = "入库分类分布统计")
    @GetMapping("/category-distribution")
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<InboundReportVo.DistributionVo> getDistribution(@Valid ReportQueryDto queryDto) {
        return R.ok(inboundReportService.getDistribution(queryDto));
    }
}
```

其余3个Controller结构一致，替换Service/VO类型即可。

---

### Task 12: 创建定时聚合任务（4个）

**Files:**
- Create: `wms-server/wms-report/src/main/java/com/wms/report/task/InboundDailyAggregationTask.java`
- Create: `wms-server/wms-report/src/main/java/com/wms/report/task/OutboundDailyAggregationTask.java`
- Create: `wms-server/wms-report/src/main/java/com/wms/report/task/StockDailySnapshotTask.java`
- Create: `wms-server/wms-report/src/main/java/com/wms/report/task/ReturnDailyAggregationTask.java`

**Step 1: InboundDailyAggregationTask.java**

```java
package com.wms.report.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.business.domain.entity.WmsInboundDetail;
import com.wms.business.domain.entity.WmsInboundOrder;
import com.wms.business.mapper.WmsInboundDetailMapper;
import com.wms.business.mapper.WmsInboundOrderMapper;
import com.wms.common.enums.OrderStatusEnum;
import com.wms.item.domain.entity.WmsItem;
import com.wms.item.mapper.WmsItemMapper;
import com.wms.report.domain.entity.ReportInboundDaily;
import com.wms.report.mapper.ReportInboundDailyMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 入库日聚合定时任务
 * 每日00:01执行，聚合前一天已完成的入库单据到report_inbound_daily表
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InboundDailyAggregationTask {

    private final WmsInboundOrderMapper wmsInboundOrderMapper;
    private final WmsInboundDetailMapper wmsInboundDetailMapper;
    private final WmsItemMapper wmsItemMapper;
    private final ReportInboundDailyMapper reportInboundDailyMapper;

    /**
     * 入库日聚合定时任务
     * 查询前一天状态为已完成的入库单，按库房+分类聚合写入report_inbound_daily
     */
    @Scheduled(cron = "0 1 0 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void aggregate() {
        LocalDate statDate = LocalDate.now().minusDays(1);
        log.info("开始入库日聚合，统计日期: {}", statDate);

        try {
            // 查询前一天完成的入库单
            LocalDateTime dayStart = statDate.atStartOfDay();
            LocalDateTime dayEnd = statDate.plusDays(1).atStartOfDay();
            LambdaQueryWrapper<WmsInboundOrder> orderWrapper = new LambdaQueryWrapper<>();
            orderWrapper.eq(WmsInboundOrder::getStatus, OrderStatusEnum.COMPLETED.getCode())
                    .ge(WmsInboundOrder::getUpdateTime, dayStart)
                    .lt(WmsInboundOrder::getUpdateTime, dayEnd);
            List<WmsInboundOrder> orders = wmsInboundOrderMapper.selectList(orderWrapper);

            if (orders.isEmpty()) {
                log.info("入库日聚合完成，无已完成入库单，统计日期: {}", statDate);
                return;
            }

            // 收集所有明细
            Set<Long> orderIds = orders.stream().map(WmsInboundOrder::getId).collect(Collectors.toSet());
            LambdaQueryWrapper<WmsInboundDetail> detailWrapper = new LambdaQueryWrapper<>();
            detailWrapper.in(WmsInboundDetail::getOrderId, orderIds);
            List<WmsInboundDetail> allDetails = wmsInboundDetailMapper.selectList(detailWrapper);

            // 批量查询物品获取分类ID
            Set<Long> itemIds = allDetails.stream().map(WmsInboundDetail::getItemId).collect(Collectors.toSet());
            Map<Long, Long> itemCategoryMap = new HashMap<>();
            if (!itemIds.isEmpty()) {
                itemCategoryMap = wmsItemMapper.selectBatchIds(itemIds).stream()
                        .collect(Collectors.toMap(WmsItem::getId, WmsItem::getCategoryId, (a, b) -> a));
            }

            // 按库房+分类聚合
            Map<String, ReportInboundDaily> aggMap = new LinkedHashMap<>();
            for (WmsInboundDetail detail : allDetails) {
                // 从明细所属订单获取warehouseId
                WmsInboundOrder order = orders.stream()
                        .filter(o -> o.getId().equals(detail.getOrderId())).findFirst().orElse(null);
                if (order == null) continue;

                Long categoryId = itemCategoryMap.getOrDefault(detail.getItemId(), 0L);
                String key = order.getWarehouseId() + "_" + categoryId;

                ReportInboundDaily agg = aggMap.computeIfAbsent(key, k -> {
                    ReportInboundDaily r = new ReportInboundDaily();
                    r.setStatDate(statDate);
                    r.setWarehouseId(order.getWarehouseId());
                    r.setCategoryId(categoryId);
                    r.setTotalQuantity(0);
                    r.setTotalAmount(BigDecimal.ZERO);
                    r.setOrderCount(0);
                    return r;
                });
                agg.setTotalQuantity(agg.getTotalQuantity() + detail.getQuantity());
                agg.setTotalAmount(agg.getTotalAmount().add(
                        detail.getAmount() != null ? detail.getAmount() : BigDecimal.ZERO));
            }

            // 计算每个库房+分类组合的订单数
            for (WmsInboundOrder order : orders) {
                // 统计该订单涉及的分类组合数，每个组合的orderCount+1
                Set<String> orderCategoryKeys = allDetails.stream()
                        .filter(d -> d.getOrderId().equals(order.getId()))
                        .map(d -> {
                            Long categoryId = itemCategoryMap.getOrDefault(d.getItemId(), 0L);
                            return order.getWarehouseId() + "_" + categoryId;
                        })
                        .collect(Collectors.toSet());
                for (String key : orderCategoryKeys) {
                    ReportInboundDaily agg = aggMap.get(key);
                    if (agg != null) {
                        agg.setOrderCount(agg.getOrderCount() + 1);
                    }
                }
            }

            // 先删后插保证幂等
            LambdaQueryWrapper<ReportInboundDaily> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(ReportInboundDaily::getStatDate, statDate);
            reportInboundDailyMapper.delete(deleteWrapper);

            for (ReportInboundDaily agg : aggMap.values()) {
                reportInboundDailyMapper.insert(agg);
            }

            log.info("入库日聚合完成，统计日期: {}，聚合记录数: {}", statDate, aggMap.size());
        } catch (Exception e) {
            log.error("入库日聚合失败，统计日期: {}", statDate, e);
            throw e;
        }
    }
}
```

**Step 2: OutboundDailyAggregationTask.java** — 类似结构，查询WmsOutboundOrder(状态COMPLETED)+WmsOutboundDetail，注意出库明细无amount字段，金额设为BigDecimal.ZERO。

**Step 3: StockDailySnapshotTask.java** — 查询wms_stock表当前库存，按warehouseId+categoryId聚合写入report_stock_daily。

**Step 4: ReturnDailyAggregationTask.java** — 查询WmsReturnOrder(COMPLETED)+WmsReturnDetail，通过关联出库单获取warehouseId，额外统计normalQuantity/damagedQuantity。

---

### Task 13: 删除占位文件并编译验证

**Files:**
- Delete: 各目录下的 `.gitkeep` 文件
- Verify: 全项目编译通过

**Step 1: 清理.gitkeep占位文件**

Run: `find D:/Codes/WMS_code/wms-server/wms-report -name ".gitkeep" -delete`

**Step 2: 全项目编译验证**

Run: `cd D:/Codes/WMS_code/wms-server && mvn compile -q`
Expected: BUILD SUCCESS

---

### Task 14: 数据库执行DDL建表

**Step 1: 执行report_tables.sql**

手动执行 `wms-server/wms-report/src/main/resources/sql/report_tables.sql` 到MySQL数据库。

---

## 执行顺序总结

1. Task 1: 更新pom.xml依赖
2. Task 2: 创建SQL建表脚本
3. Task 3: 创建ReportConstants常量类
4. Task 4: 创建4个Entity
5. Task 5: 创建4个Mapper
6. Task 6: 创建ReportQueryDto
7. Task 7: 创建5个VO类(CommonReportVo + 4个报表Vo)
8. Task 8: 创建ReportConverter
9. Task 9: 创建4个Service接口
10. Task 10: 创建4个Service实现类
11. Task 11: 创建4个Controller
12. Task 12: 创建4个定时聚合Task
13. Task 13: 清理占位文件+编译验证
14. Task 14: 执行DDL建表
