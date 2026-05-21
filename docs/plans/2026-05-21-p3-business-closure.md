# P3 业务闭环实现计划

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 完成P3业务闭环阶段所有开发任务：归还/报废/调拨单据补全Converter+update+delete接口、审批流程模块全量开发、BizTypeEnum补RETURN、机器-备件关联、长期闲置检测定时任务。

**Architecture:** 基于现有SpringBoot+MyBatis-Plus架构，审批模块(wms-approval)采用策略模式支持多业务类型审批流程；业务单据修复遵循InboundService的完整CRUD模板；机器-备件关联在item模块新增Entity/Mapper/Service/Controller全套；闲置检测用@Scheduled定时任务扫描库存表。

**Tech Stack:** SpringBoot 3.x, MyBatis-Plus, Lombok, Swagger/OpenAPI 3, Spring Event, Redis(SequenceGenerator), @Scheduled

---

## 阶段一：业务单据修复（Return/Scrap/Transfer补全）

### Task 1: BizTypeEnum 补充 RETURN(5, "归还")

**Files:**
- Modify: `wms-server/wms-common/src/main/java/com/wms/common/enums/BizTypeEnum.java:8-24`

**Step 1: 添加RETURN枚举值**

```java
// BizTypeEnum.java - 在TRANSFER(4, "调拨")后添加
RETURN(5, "归还");
```

**Step 2: 验证编译**

Run: `cd wms-server && mvn compile -pl wms-common -q`
Expected: BUILD SUCCESS

**Step 3: Commit**

```bash
git add wms-server/wms-common/src/main/java/com/wms/common/enums/BizTypeEnum.java
git commit -m "feat(common): BizTypeEnum补充RETURN(5)归还业务类型"
```

---

### Task 2: 创建 ReturnOrderConverter

**Files:**
- Create: `wms-server/wms-business/src/main/java/com/wms/business/converter/ReturnOrderConverter.java`

**Step 1: 创建Converter类（参照InboundOrderConverter模式）**

```java
package com.wms.business.converter;

import com.wms.business.domain.entity.WmsReturnDetail;
import com.wms.business.domain.entity.WmsReturnOrder;
import com.wms.business.domain.vo.ReturnOrderVo;
import com.wms.item.domain.entity.WmsItem;
import com.wms.item.mapper.WmsItemMapper;
import com.wms.business.domain.entity.WmsOutboundOrder;
import com.wms.business.mapper.WmsOutboundOrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 归还单转换器
 * 负责WmsReturnOrder实体与ReturnOrderVo之间的转换，关联信息从预查询的Map中填充以避免N+1
 */
@Component
@RequiredArgsConstructor
public class ReturnOrderConverter {

    private final WmsOutboundOrderMapper wmsOutboundOrderMapper;
    private final WmsItemMapper wmsItemMapper;

    /**
     * WmsReturnOrder实体转ReturnOrderVo(填充出库单号)
     *
     * @param order 归还单实体
     * @param outboundOrderMap 出库单ID到实体的映射
     * @return 归还单VO
     */
    public ReturnOrderVo toVo(WmsReturnOrder order, Map<Long, WmsOutboundOrder> outboundOrderMap) {
        ReturnOrderVo vo = new ReturnOrderVo();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setOutboundOrderId(order.getOutboundOrderId());
        vo.setStatus(order.getStatus());
        vo.setReceiver(order.getReceiver());
        vo.setRemark(order.getRemark());
        vo.setCreateTime(order.getCreateTime());
        vo.setCreateBy(order.getCreateBy());
        if (order.getOutboundOrderId() != null) {
            WmsOutboundOrder outboundOrder = outboundOrderMap.get(order.getOutboundOrderId());
            if (outboundOrder == null) {
                outboundOrder = wmsOutboundOrderMapper.selectById(order.getOutboundOrderId());
            }
            if (outboundOrder != null) {
                vo.setOutboundOrderNo(outboundOrder.getOrderNo());
            }
        }
        return vo;
    }

    /**
     * WmsReturnDetail实体转ReturnDetailVo(填充物品名称)
     *
     * @param detail 归还明细实体
     * @return 归还明细VO
     */
    public ReturnOrderVo.ReturnDetailVo toDetailVo(WmsReturnDetail detail) {
        ReturnOrderVo.ReturnDetailVo vo = new ReturnOrderVo.ReturnDetailVo();
        vo.setId(detail.getId());
        vo.setItemId(detail.getItemId());
        vo.setQuantity(detail.getQuantity());
        vo.setConditionStatus(detail.getConditionStatus());
        if (detail.getItemId() != null) {
            WmsItem item = wmsItemMapper.selectById(detail.getItemId());
            if (item != null) {
                vo.setItemName(item.getItemName());
                vo.setItemCode(item.getItemCode());
            }
        }
        return vo;
    }

    /**
     * 批量转换归还明细列表
     *
     * @param details 归还明细实体列表
     * @return 归还明细VO列表
     */
    public List<ReturnOrderVo.ReturnDetailVo> toDetailVoList(List<WmsReturnDetail> details) {
        return details.stream().map(this::toDetailVo).collect(Collectors.toList());
    }
}
```

**Step 2: 修改ReturnServiceImpl，删除private toOrderVo/toDetailVo/getOrderDetails方法，改用Converter**

关键修改点：
- 注入 `ReturnOrderConverter returnOrderConverter`
- `pageOrders`中：`returnOrderConverter.toVo(order, outboundOrderMap)` 批量Map避免N+1
- `getOrderById`中：`returnOrderConverter.toVo(order, Map.of())` + `returnOrderConverter.toDetailVoList(...)`
- `createOrder`中：同上
- `submitOrder`中：同上
- 删除 `toOrderVo`、`toDetailVo`、`getOrderDetails` 三个private方法
- 新增 `updateOrder`、`deleteOrder` 方法（参照InboundServiceImpl模式）

**Step 3: 修改ReturnService接口，补充updateOrder/deleteOrder方法签名**

```java
/**
 * 更新归还单(仅草稿状态)
 *
 * @param id 归还单ID
 * @param dto 归还单更新参数
 * @return 更新后的归还单VO
 */
ReturnOrderVo updateOrder(Long id, ReturnOrderDto dto);

/**
 * 删除归还单(仅草稿状态)
 *
 * @param id 归还单ID
 */
void deleteOrder(Long id);
```

**Step 4: 修改ReturnController，补充submit/update/delete接口**

补充三个接口：
```java
@Operation(summary = "提交归还单")
@PostMapping("/{id}/submit")
@PreAuthorize("isAuthenticated()")
@OperLog(module = "business", type = "提交", desc = "提交归还单")
public R<Void> submitOrder(@PathVariable Long id) {
    returnService.submitOrder(id);
    return R.ok();
}

@Operation(summary = "更新归还单")
@PutMapping("/{id}")
@PreAuthorize("isAuthenticated()")
@OperLog(module = "business", type = "更新", desc = "更新归还单")
public R<ReturnOrderVo> updateOrder(@PathVariable Long id, @Valid @RequestBody ReturnOrderDto dto) {
    return R.ok(returnService.updateOrder(id, dto));
}

@Operation(summary = "删除归还单")
@DeleteMapping("/{id}")
@PreAuthorize("isAuthenticated()")
@OperLog(module = "business", type = "删除", desc = "删除归还单")
public R<Void> deleteOrder(@PathVariable Long id) {
    returnService.deleteOrder(id);
    return R.ok();
}
```

**Step 5: 修改ReturnServiceImpl.submitOrder返回值改为void（对齐Scrap/Transfer）**

ReturnService.submitOrder当前返回ReturnOrderVo，需改为void对齐其他单据的submit模式。

**Step 6: 验证编译**

Run: `cd wms-server && mvn compile -pl wms-business -am -q`
Expected: BUILD SUCCESS

**Step 7: Commit**

```bash
git add -A wms-server/wms-business/src/main/java/com/wms/business/converter/ReturnOrderConverter.java
git add -A wms-server/wms-business/src/main/java/com/wms/business/service/ wms-server/wms-business/src/main/java/com/wms/business/controller/ReturnController.java
git commit -m "feat(business): 归还单补全Converter/update/delete/submit接口"
```

---

### Task 3: 创建 ScrapOrderConverter + 补全update/delete接口

**Files:**
- Create: `wms-server/wms-business/src/main/java/com/wms/business/converter/ScrapOrderConverter.java`
- Modify: `wms-server/wms-business/src/main/java/com/wms/business/service/impl/ScrapServiceImpl.java`
- Modify: `wms-server/wms-business/src/main/java/com/wms/business/service/ScrapService.java`
- Modify: `wms-server/wms-business/src/main/java/com/wms/business/controller/ScrapController.java`
- Modify: `wms-server/wms-business/src/main/java/com/wms/business/domain/vo/ScrapOrderVo.java`
- Modify: `wms-server/wms-business/src/main/java/com/wms/business/domain/dto/ScrapOrderDto.java`

**Step 1: ScrapOrderVo补充warehouseId/warehouseName字段**

```java
/** 所属库房ID */
@Schema(description = "所属库房ID")
private Long warehouseId;

/** 所属库房名称 */
@Schema(description = "所属库房名称")
private String warehouseName;
```

**Step 2: ScrapOrderDto补充warehouseId字段**

```java
/** 所属库房ID */
@NotNull(message = "所属库房ID不能为空")
@Schema(description = "所属库房ID")
private Long warehouseId;
```

**Step 3: 创建ScrapOrderConverter（参照InboundOrderConverter模式，关联warehouse+item）**

Converter包含：
- `toVo(WmsScrapOrder, Map<Long, WmsWarehouse>)` - 填充warehouseName
- `toDetailVo(WmsScrapDetail)` - 填充itemName/itemCode
- `toDetailVoList(List<WmsScrapDetail>)`

**Step 4: 修改ScrapServiceImpl - 删除private toOrderVo/toDetailVo/getOrderDetails，改用Converter**

- 注入 `ScrapOrderConverter`
- 补全 `updateOrder`、`deleteOrder` 方法
- createOrder中补充 `order.setWarehouseId(dto.getWarehouseId())`
- pageOrders中使用批量Map避免N+1

**Step 5: 修改ScrapService接口，补充updateOrder/deleteOrder**

**Step 6: 修改ScrapController，补充update/delete接口**

```java
@Operation(summary = "更新报废单")
@PutMapping("/{id}")
@PreAuthorize("isAuthenticated()")
@OperLog(module = "business", type = "更新", desc = "更新报废单")
public R<ScrapOrderVo> updateOrder(@PathVariable Long id, @Valid @RequestBody ScrapOrderDto dto) {
    return R.ok(scrapService.updateOrder(id, dto));
}

@Operation(summary = "删除报废单")
@DeleteMapping("/{id}")
@PreAuthorize("isAuthenticated()")
@OperLog(module = "business", type = "删除", desc = "删除报废单")
public R<Void> deleteOrder(@PathVariable Long id) {
    scrapService.deleteOrder(id);
    return R.ok();
}
```

**Step 7: 验证编译**

Run: `cd wms-server && mvn compile -pl wms-business -am -q`
Expected: BUILD SUCCESS

**Step 8: Commit**

```bash
git add -A
git commit -m "feat(business): 报废单补全Converter/update/delete接口+Vo补warehouseId"
```

---

### Task 4: 创建 TransferOrderConverter + 补全update/delete接口

**Files:**
- Create: `wms-server/wms-business/src/main/java/com/wms/business/converter/TransferOrderConverter.java`
- Modify: `wms-server/wms-business/src/main/java/com/wms/business/service/impl/TransferServiceImpl.java`
- Modify: `wms-server/wms-business/src/main/java/com/wms/business/service/TransferService.java`
- Modify: `wms-server/wms-business/src/main/java/com/wms/business/controller/TransferController.java`

**Step 1: 创建TransferOrderConverter（关联warehouse+item）**

Converter包含：
- `toVo(WmsTransferOrder, Map<Long, WmsWarehouse>)` - 填充fromWarehouseName/toWarehouseName
- `toDetailVo(WmsTransferDetail)` - 填充itemName/itemCode
- `toDetailVoList(List<WmsTransferDetail>)`

**Step 2: 修改TransferServiceImpl - 删除private toOrderVo/toDetailVo/getOrderDetails，改用Converter**

- 注入 `TransferOrderConverter`
- 补全 `updateOrder`、`deleteOrder` 方法
- pageOrders中使用批量Map避免N+1

**Step 3: 修改TransferService接口，补充updateOrder/deleteOrder**

**Step 4: 修改TransferController，补充update/delete接口**

**Step 5: 验证编译**

Run: `cd wms-server && mvn compile -pl wms-business -am -q`
Expected: BUILD SUCCESS

**Step 6: Commit**

```bash
git add -A
git commit -m "feat(business): 调拨单补全Converter/update/delete接口"
```

---

## 阶段二：审批流程模块（wms-approval）

### Task 5: 审批模块pom补全依赖 + SQL建表

**Files:**
- Modify: `wms-server/wms-approval/pom.xml`
- Create: `wms-server/wms-approval/src/main/resources/sql/approval_tables.sql`

**Step 1: pom.xml补全依赖**

需添加：mybatis-plus-boot-starter、spring-boot-starter-web、spring-boot-starter-security、swagger/openapi、wms-warehouse(跨模块引用)、wms-item(跨模块引用)、wms-business(跨模块引用)

> 注意：跨模块引用需确认父pom中已有这些模块声明。若存在循环依赖风险，审批模块仅依赖wms-common，通过接口回调或事件机制与business模块交互。

**Step 2: SQL建表脚本**

```sql
-- 审批配置表：定义每种业务类型的审批流程
CREATE TABLE wms_approval_config (
    `id`              BIGINT       NOT NULL COMMENT '主键(雪花ID)',
    `biz_type`        TINYINT      NOT NULL COMMENT '业务类型(1-入库 2-出库 3-报废 4-调拨 5-归还)',
    `enabled`         TINYINT      DEFAULT 1 COMMENT '是否启用(0-否 1-是)',
    `auto_approve`    TINYINT      DEFAULT 0 COMMENT '是否免审(0-否 1-是)',
    `config_name`     VARCHAR(128) NOT NULL COMMENT '配置名称',
    `remark`          VARCHAR(512) DEFAULT '' COMMENT '备注',
    `del_flag`        TINYINT      DEFAULT 0 COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)  DEFAULT '' COMMENT '创建人',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)  DEFAULT '' COMMENT '更新人',
    PRIMARY KEY (`id`)
) COMMENT='审批配置表';

-- 审批节点配置表：定义审批流程的每个节点
CREATE TABLE wms_approval_node (
    `id`              BIGINT       NOT NULL COMMENT '主键(雪花ID)',
    `config_id`       BIGINT       NOT NULL COMMENT '审批配置ID',
    `step_order`      INT          NOT NULL COMMENT '节点顺序(从1开始)',
    `node_name`       VARCHAR(128) NOT NULL COMMENT '节点名称',
    `approver_type`   TINYINT      NOT NULL COMMENT '审批人类型(1-指定角色 2-指定用户 3-库房管理员)',
    `approver_id`     BIGINT       DEFAULT NULL COMMENT '审批人/角色ID',
    `del_flag`        TINYINT      DEFAULT 0 COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)  DEFAULT '' COMMENT '创建人',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)  DEFAULT '' COMMENT '更新人',
    PRIMARY KEY (`id`)
) COMMENT='审批节点配置表';

-- 审批单表：每次业务单据提交审批后生成
CREATE TABLE wms_approval_order (
    `id`              BIGINT       NOT NULL COMMENT '主键(雪花ID)',
    `biz_id`          BIGINT       NOT NULL COMMENT '业务单据ID',
    `biz_type`        TINYINT      NOT NULL COMMENT '业务类型(1-入库 2-出库 3-报废 4-调拨 5-归还)',
    `status`          TINYINT      NOT NULL DEFAULT 0 COMMENT '审批状态(0-待审批 1-审批中 2-已通过 3-已驳回 4-已撤回)',
    `applicant_id`    BIGINT       DEFAULT NULL COMMENT '申请人ID',
    `current_step`    INT          DEFAULT 1 COMMENT '当前审批节点(从1开始)',
    `total_steps`     INT          DEFAULT 0 COMMENT '总审批节点数',
    `remark`          VARCHAR(512) DEFAULT '' COMMENT '备注',
    `del_flag`        TINYINT      DEFAULT 0 COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)  DEFAULT '' COMMENT '创建人',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)  DEFAULT '' COMMENT '更新人',
    PRIMARY KEY (`id`),
    KEY `idx_biz` (`biz_id`, `biz_type`)
) COMMENT='审批单表';

-- 审批记录表：每个审批节点的审批结果
CREATE TABLE wms_approval_record (
    `id`              BIGINT       NOT NULL COMMENT '主键(雪花ID)',
    `approval_id`     BIGINT       NOT NULL COMMENT '审批单ID',
    `step_order`      INT          NOT NULL COMMENT '节点顺序',
    `approver_id`     BIGINT       DEFAULT NULL COMMENT '审批人ID',
    `approver_name`   VARCHAR(64)  DEFAULT '' COMMENT '审批人姓名',
    `result`          TINYINT      DEFAULT NULL COMMENT '审批结果(1-通过 2-驳回)',
    `opinion`         VARCHAR(512) DEFAULT '' COMMENT '审批意见',
    `approve_time`    DATETIME     DEFAULT NULL COMMENT '审批时间',
    `del_flag`        TINYINT      DEFAULT 0 COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)  DEFAULT '' COMMENT '创建人',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)  DEFAULT '' COMMENT '更新人',
    PRIMARY KEY (`id`),
    KEY `idx_approval_id` (`approval_id`)
) COMMENT='审批记录表';
```

**Step 3: Commit**

```bash
git add -A
git commit -m "feat(approval): 审批模块pom补全依赖+SQL建表脚本"
```

---

### Task 6: 审批模块Entity层

**Files:**
- Create: `wms-server/wms-approval/src/main/java/com/wms/approval/domain/entity/WmsApprovalConfig.java`
- Create: `wms-server/wms-approval/src/main/java/com/wms/approval/domain/entity/WmsApprovalNode.java`
- Create: `wms-server/wms-approval/src/main/java/com/wms/approval/domain/entity/WmsApprovalOrder.java`
- Create: `wms-server/wms-approval/src/main/java/com/wms/approval/domain/entity/WmsApprovalRecord.java`

**Step 1: 创建4个Entity类**

所有Entity extends BaseEntity，使用@Data/@EqualsAndHashCode(callSuper=true)/@TableName/@Schema注解。
字段与SQL建表脚本一一对应（不含公共字段，由BaseEntity提供）。

**Step 2: 验证编译**

Run: `cd wms-server && mvn compile -pl wms-approval -am -q`
Expected: BUILD SUCCESS

**Step 3: Commit**

```bash
git add -A
git commit -m "feat(approval): 审批模块Entity层-Config/Node/Order/Record"
```

---

### Task 7: 审批模块Mapper层

**Files:**
- Create: `wms-server/wms-approval/src/main/java/com/wms/approval/mapper/WmsApprovalConfigMapper.java`
- Create: `wms-server/wms-approval/src/main/java/com/wms/approval/mapper/WmsApprovalNodeMapper.java`
- Create: `wms-server/wms-approval/src/main/java/com/wms/approval/mapper/WmsApprovalOrderMapper.java`
- Create: `wms-server/wms-approval/src/main/java/com/wms/approval/mapper/WmsApprovalRecordMapper.java`

每个Mapper: `extends BaseMapper<Entity>`, `@Mapper`

**Commit:**

```bash
git add -A && git commit -m "feat(approval): 审批模块Mapper层"
```

---

### Task 8: 审批模块Dto/Vo/Converter层

**Files:**
- Create: `wms-server/wms-approval/src/main/java/com/wms/approval/domain/dto/ApprovalConfigDto.java`
- Create: `wms-server/wms-approval/src/main/java/com/wms/approval/domain/dto/ApprovalActionDto.java` (审批操作：通过/驳回/撤回)
- Create: `wms-server/wms-approval/src/main/java/com/wms/approval/domain/vo/ApprovalConfigVo.java`
- Create: `wms-server/wms-approval/src/main/java/com/wms/approval/domain/vo/ApprovalOrderVo.java`
- Create: `wms-server/wms-approval/src/main/java/com/wms/approval/domain/constant/ApprovalConstants.java`
- Create: `wms-server/wms-approval/src/main/java/com/wms/approval/converter/ApprovalConfigConverter.java`
- Create: `wms-server/wms-approval/src/main/java/com/wms/approval/converter/ApprovalOrderConverter.java`

**ApprovalConstants:**
```java
/**
 * 审批常量类
 */
public final class ApprovalConstants {
    private ApprovalConstants() {}
    /** 审批结果：通过 */
    public static final int RESULT_APPROVED = 1;
    /** 审批结果：驳回 */
    public static final int RESULT_REJECTED = 2;
    /** 审批状态：待审批 */
    public static final int STATUS_PENDING = 0;
    /** 审批状态：审批中 */
    public static final int STATUS_APPROVING = 1;
    /** 审批状态：已通过 */
    public static final int STATUS_APPROVED = 2;
    /** 审批状态：已驳回 */
    public static final int STATUS_REJECTED = 3;
    /** 审批状态：已撤回 */
    public static final int STATUS_REVOKED = 4;
    /** 审批人类型：指定角色 */
    public static final int APPROVER_TYPE_ROLE = 1;
    /** 审批人类型：指定用户 */
    public static final int APPROVER_TYPE_USER = 2;
    /** 审批人类型：库房管理员 */
    public static final int APPROVER_TYPE_WAREHOUSE_ADMIN = 3;
}
```

**Commit:**

```bash
git add -A && git commit -m "feat(approval): 审批模块Dto/Vo/Converter/Constants层"
```

---

### Task 9: 审批模块Service层

**Files:**
- Create: `wms-server/wms-approval/src/main/java/com/wms/approval/service/ApprovalConfigService.java` (审批配置CRUD)
- Create: `wms-server/wms-approval/src/main/java/com/wms/approval/service/ApprovalService.java` (审批核心：发起/通过/驳回/撤回)
- Create: `wms-server/wms-approval/src/main/java/com/wms/approval/service/impl/ApprovalConfigServiceImpl.java`
- Create: `wms-server/wms-approval/src/main/java/com/wms/approval/service/impl/ApprovalServiceImpl.java`

**ApprovalService核心方法：**

```java
/**
 * 发起审批
 * 查找bizType对应的审批配置，若免审则直接通过，否则创建审批单并流转到第一个节点
 *
 * @param bizId 业务单据ID
 * @param bizType 业务类型(引用BizTypeEnum)
 * @return 审批单VO
 */
ApprovalOrderVo startApproval(Long bizId, int bizType);

/**
 * 审批通过
 * 当前节点审批通过，若为最后节点则审批单标记已通过，否则流转到下一节点
 *
 * @param approvalId 审批单ID
 * @param dto 审批操作参数(含意见)
 */
void approve(Long approvalId, ApprovalActionDto dto);

/**
 * 审批驳回
 * 当前节点审批驳回，审批单标记已驳回
 *
 * @param approvalId 审批单ID
 * @param dto 审批操作参数(含意见)
 */
void reject(Long approvalId, ApprovalActionDto dto);

/**
 * 撤回审批
 * 仅申请人可撤回，且仅待审批/审批中状态可撤回
 *
 * @param approvalId 审批单ID
 */
void revoke(Long approvalId);

/**
 * 根据业务单据ID和类型查询审批单
 *
 * @param bizId 业务单据ID
 * @param bizType 业务类型
 * @return 审批单VO(含审批记录)
 */
ApprovalOrderVo getByBiz(Long bizId, int bizType);
```

**ApprovalServiceImpl核心逻辑：**
- `startApproval`: 查ApprovalConfig(bizType) → 若免审(autoApprove=1)则直接返回已通过 → 查ApprovalNode列表 → 创建ApprovalOrder(status=APPROVING, currentStep=1) → 返回Vo
- `approve`: 校验当前节点审批人 → 写ApprovalRecord(result=APPROVED) → 若currentStep==totalSteps则ApprovalOrder.status=APPROVED → 否则currentStep++ → **发布审批通过事件**
- `reject`: 校验当前节点审批人 → 写ApprovalRecord(result=REJECTED) → ApprovalOrder.status=REJECTED → **发布审批驳回事件**
- `revoke`: 校验申请人 → ApprovalOrder.status=REVOKED

**Commit:**

```bash
git add -A && git commit -m "feat(approval): 审批模块Service层-核心审批流程逻辑"
```

---

### Task 10: 审批模块Controller层

**Files:**
- Create: `wms-server/wms-approval/src/main/java/com/wms/approval/controller/ApprovalConfigController.java`
- Create: `wms-server/wms-approval/src/main/java/com/wms/approval/controller/ApprovalController.java`

所有方法必须有@DataScope/@PreAuthorize/@OperLog/@Valid，参照InboundController模式。

**ApprovalController接口清单：**
- `GET /approval` - 分页查询审批单
- `GET /approval/{id}` - 审批单详情(含记录)
- `GET /approval/biz` - 按业务单据查询审批单(bizId+bizType)
- `POST /approval/{id}/approve` - 审批通过
- `POST /approval/{id}/reject` - 审批驳回
- `POST /approval/{id}/revoke` - 撤回审批

**ApprovalConfigController接口清单：**
- `GET /approval/config` - 分页查询审批配置
- `GET /approval/config/{id}` - 配置详情(含节点列表)
- `POST /approval/config` - 新增配置
- `PUT /approval/config/{id}` - 更新配置
- `DELETE /approval/config/{id}` - 删除配置(逻辑删除)

**Commit:**

```bash
git add -A && git commit -m "feat(approval): 审批模块Controller层"
```

---

### Task 11: 审批事件集成 - 业务单据submitOrder对接审批流程

**Files:**
- Create: `wms-server/wms-business/src/main/java/com/wms/business/event/ApprovalResultEvent.java` (审批结果事件)
- Create: `wms-server/wms-business/src/main/java/com/wms/business/listener/ApprovalResultEventListener.java` (审批结果监听器)
- Modify: `wms-server/wms-business/src/main/java/com/wms/business/service/impl/ScrapServiceImpl.java` (submitOrder改为发起审批)
- Modify: `wms-server/wms-business/src/main/java/com/wms/business/service/impl/TransferServiceImpl.java` (submitOrder改为发起审批)
- Modify: `wms-server/wms-business/src/main/java/com/wms/business/service/impl/ReturnServiceImpl.java` (submitOrder改为发起审批)

**核心改造思路：**

1. `ApprovalResultEvent`: 包含bizId, bizType, result(APPROVED/REJECTED)
2. `ApprovalResultEventListener`: 监听审批通过事件 → 根据bizType分发到对应的库存同步逻辑
   - SCRAP: 扣减库存(STOCK_SYNC_OUT)
   - TRANSFER: 调出库房出库+调入库房入库
   - RETURN: 归还入库(STOCK_SYNC_IN)
   - INBOUND/OUTBOUND: 已有逻辑，此处预留
3. 各ServiceImpl的submitOrder改造：
   - 状态从DRAFT→PENDING（不变）
   - 不再直接跳转COMPLETED和发布库存事件
   - 改为调用 `approvalService.startApproval(orderId, bizType)` 发起审批
   - 库存同步延迟到审批通过后由事件监听器处理

**Commit:**

```bash
git add -A && git commit -m "feat(business): 业务单据submitOrder对接审批流程+审批结果事件监听"
```

---

## 阶段三：机器-备件关联

### Task 12: 机器-备件关联Entity/Mapper/Dto/Vo/Converter

**Files:**
- Create: `wms-server/wms-item/src/main/java/com/wms/item/domain/entity/WmsMachineSpare.java`
- Create: `wms-server/wms-item/src/main/java/com/wms/item/mapper/WmsMachineSpareMapper.java`
- Create: `wms-server/wms-item/src/main/java/com/wms/item/domain/dto/MachineSpareDto.java`
- Create: `wms-server/wms-item/src/main/java/com/wms/item/domain/vo/MachineSpareVo.java`
- Create: `wms-server/wms-item/src/main/java/com/wms/item/converter/MachineSpareConverter.java`

**WmsMachineSpare Entity:**
```java
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_machine_spare")
public class WmsMachineSpare extends BaseEntity {
    /** 机器名称 */
    @Schema(description = "机器名称")
    private String machineName;
    /** 机器编号 */
    @Schema(description = "机器编号")
    private String machineCode;
    /** 备件物品ID */
    @Schema(description = "备件物品ID")
    private Long spareItemId;
    /** 数量 */
    @Schema(description = "数量")
    private Integer quantity;
    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}
```

**SQL建表:**
```sql
CREATE TABLE wms_machine_spare (
    `id`              BIGINT       NOT NULL COMMENT '主键(雪花ID)',
    `machine_name`    VARCHAR(128) NOT NULL COMMENT '机器名称',
    `machine_code`    VARCHAR(64)  NOT NULL COMMENT '机器编号',
    `spare_item_id`   BIGINT       NOT NULL COMMENT '备件物品ID',
    `quantity`        INT          DEFAULT 1 COMMENT '数量',
    `remark`          VARCHAR(512) DEFAULT '' COMMENT '备注',
    `del_flag`        TINYINT      DEFAULT 0 COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)  DEFAULT '' COMMENT '创建人',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)  DEFAULT '' COMMENT '更新人',
    PRIMARY KEY (`id`),
    KEY `idx_machine_code` (`machine_code`),
    KEY `idx_spare_item_id` (`spare_item_id`)
) COMMENT='机器-备件关联表';
```

**Commit:**

```bash
git add -A && git commit -m "feat(item): 机器-备件关联Entity/Mapper/Dto/Vo/Converter+SQL建表"
```

---

### Task 13: 机器-备件关联Service/Controller

**Files:**
- Create: `wms-server/wms-item/src/main/java/com/wms/item/service/MachineSpareService.java`
- Create: `wms-server/wms-item/src/main/java/com/wms/item/service/impl/MachineSpareServiceImpl.java`
- Create: `wms-server/wms-item/src/main/java/com/wms/item/controller/MachineSpareController.java`

**MachineSpareService方法：**
- `pageList(PageParam, String machineName, Long spareItemId)` - 分页查询
- `getById(Long id)` - 详情
- `create(MachineSpareDto)` - 新增
- `update(Long id, MachineSpareDto)` - 更新
- `delete(Long id)` - 逻辑删除
- `listByMachineCode(String machineCode)` - 按机器编号查询备件列表

**Controller所有方法加@DataScope/@PreAuthorize/@OperLog/@Valid**

**Commit:**

```bash
git add -A && git commit -m "feat(item): 机器-备件关联Service/Controller全套CRUD"
```

---

## 阶段四：长期闲置检测

### Task 14: 闲置检测定时任务

**Files:**
- Create: `wms-server/wms-item/src/main/java/com/wms/item/task/IdleCheckTask.java`
- Modify: `wms-server/wms-item/src/main/java/com/wms/item/service/ItemService.java` (补充idleMark/cancelIdle方法)
- Modify: `wms-server/wms-item/src/main/java/com/wms/item/service/impl/ItemServiceImpl.java`

**IdleCheckTask:**
```java
/**
 * 长期闲置检测定时任务
 * 扫描库存表，将超出闲置阈值的物品标记为闲置状态
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class IdleCheckTask {

    private final WmsStockMapper wmsStockMapper;
    private final WmsItemMapper wmsItemMapper;
    private final LabelConstants labelConstants;

    /**
     * 每天凌晨2点执行闲置检测
     * 扫描库存表lastOutboundTime超过IDLE_THRESHOLD_DAYS天未出库的物品，标记为闲置
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void checkIdleItems() {
        LocalDateTime threshold = LocalDateTime.now()
                .minusDays(LabelConstants.IDLE_THRESHOLD_DAYS);
        // 查询超期未出库的库存记录
        List<WmsStock> idleStocks = wmsStockMapper.selectList(
                new LambdaQueryWrapper<WmsStock>()
                        .lt(WmsStock::getLastOutboundTime, threshold)
                        .ne(WmsStock::getLastOutboundTime, null));
        for (WmsStock stock : idleStocks) {
            WmsItem item = wmsItemMapper.selectById(stock.getItemId());
            if (item != null && item.getStatus() != ItemStatusEnum.IDLE.getCode()) {
                item.setStatus(ItemStatusEnum.IDLE.getCode());
                wmsItemMapper.updateById(item);
                log.info("物品{}已标记为闲置，最后出库时间：{}",
                        item.getItemCode(), stock.getLastOutboundTime());
            }
        }
    }
}
```

**注意：** 需确认SpringBoot主启动类或配置类上有 `@EnableScheduling` 注解。

**Commit:**

```bash
git add -A && git commit -m "feat(item): 长期闲置检测定时任务+ItemService补充闲置标记方法"
```

---

### Task 15: 异常归还登记功能

**Files:**
- Modify: `wms-server/wms-business/src/main/java/com/wms/business/domain/entity/WmsReturnDetail.java` (确认conditionStatus字段可覆盖异常场景)
- Modify: `wms-server/wms-business/src/main/java/com/wms/business/domain/vo/ReturnOrderVo.java` (ReturnDetailVo补充abnormalRemark字段)
- Modify: `wms-server/wms-business/src/main/java/com/wms/business/domain/dto/ReturnOrderDto.java` (ReturnDetailDto补充abnormalRemark字段)

异常归还场景通过conditionStatus字段区分：
- IN_STOCK(1)=正常归还
- DAMAGED(4)=损坏归还
- LOST(5)=丢失不归还

新增abnormalRemark字段记录异常说明。

**Commit:**

```bash
git add -A && git commit -m "feat(business): 异常归还登记-补充abnormalRemark字段"
```

---

## 阶段五：集成验证

### Task 16: 全量编译验证 + 模块依赖检查

**Step 1: 全量编译**

Run: `cd wms-server && mvn compile -q`
Expected: BUILD SUCCESS

**Step 2: 检查循环依赖**

确认wms-approval不循环依赖wms-business。若存在循环依赖，需通过事件机制解耦：
- approval模块仅依赖wms-common
- business模块通过ApplicationEventPublisher发布审批请求事件
- approval模块监听审批请求事件并处理
- 审批结果通过ApprovalResultEvent回调business模块

**Step 3: Commit**

```bash
git add -A && git commit -m "chore: P3业务闭环阶段全量编译验证通过"
```

---

## 实现顺序总结

| 优先级 | Task | 预计工作量 | 模块 |
|--------|------|-----------|------|
| P0 | Task 1: BizTypeEnum补RETURN | 5min | common |
| P0 | Task 2: ReturnConverter+接口补全 | 30min | business |
| P0 | Task 3: ScrapConverter+接口补全 | 30min | business |
| P0 | Task 4: TransferConverter+接口补全 | 30min | business |
| P1 | Task 5: 审批pom+SQL建表 | 15min | approval |
| P1 | Task 6-10: 审批Entity→Controller全量开发 | 2h | approval |
| P1 | Task 11: 审批事件集成 | 1h | business+approval |
| P2 | Task 12-13: 机器-备件关联 | 1h | item |
| P2 | Task 14: 闲置检测定时任务 | 30min | item |
| P2 | Task 15: 异常归还登记 | 15min | business |
| P3 | Task 16: 集成验证 | 30min | 全局 |

**依赖关系：**
- Task 1 无依赖，可最先开始
- Task 2/3/4 互相独立，可并行
- Task 5-10 顺序执行（审批模块自底向上）
- Task 11 依赖 Task 10（审批Service完成后才能集成）
- Task 12-15 互相独立，可并行
- Task 16 依赖所有前置Task
