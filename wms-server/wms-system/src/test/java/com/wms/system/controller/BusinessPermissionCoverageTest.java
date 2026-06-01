package com.wms.system.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 业务权限覆盖架构测试。
 * 验证菜单对应的业务接口使用具体权限码，而不是仅校验登录态。
 */
@DisplayName("业务权限覆盖测试")
class BusinessPermissionCoverageTest {

    private static final List<String> TARGET_CONTROLLERS = List.of(
            "../wms-warehouse/src/main/java/com/wms/warehouse/controller/WarehouseController.java",
            "../wms-warehouse/src/main/java/com/wms/warehouse/controller/AreaController.java",
            "../wms-warehouse/src/main/java/com/wms/warehouse/controller/CabinetController.java",
            "../wms-warehouse/src/main/java/com/wms/warehouse/controller/BinController.java",
            "../wms-item/src/main/java/com/wms/item/controller/ItemController.java",
            "../wms-item/src/main/java/com/wms/item/controller/CategoryController.java",
            "../wms-item/src/main/java/com/wms/item/controller/TagController.java",
            "../wms-item/src/main/java/com/wms/item/controller/LabelController.java",
            "../wms-item/src/main/java/com/wms/item/controller/StockController.java",
            "../wms-item/src/main/java/com/wms/item/controller/MachineSpareController.java",
            "../wms-business/src/main/java/com/wms/business/controller/InboundController.java",
            "../wms-business/src/main/java/com/wms/business/controller/OutboundController.java",
            "../wms-business/src/main/java/com/wms/business/controller/ReturnController.java",
            "../wms-business/src/main/java/com/wms/business/controller/ScrapController.java",
            "../wms-business/src/main/java/com/wms/business/controller/TransferController.java",
            "../wms-approval/src/main/java/com/wms/approval/controller/ApprovalController.java",
            "../wms-approval/src/main/java/com/wms/approval/controller/ApprovalConfigController.java",
            "../wms-monitor/src/main/java/com/wms/monitor/controller/StockAlertController.java",
            "../wms-monitor/src/main/java/com/wms/monitor/controller/OverdueReturnController.java",
            "../wms-report/src/main/java/com/wms/report/controller/InboundReportController.java",
            "../wms-report/src/main/java/com/wms/report/controller/OutboundReportController.java",
            "../wms-report/src/main/java/com/wms/report/controller/StockReportController.java",
            "../wms-report/src/main/java/com/wms/report/controller/ReturnReportController.java",
            "../wms-report/src/main/java/com/wms/report/controller/ScrapReportController.java",
            "../wms-report/src/main/java/com/wms/report/controller/TransferReportController.java",
            "../wms-report/src/main/java/com/wms/report/controller/AlertReportController.java",
            "../wms-report/src/main/java/com/wms/report/controller/CostAccountController.java",
            "../wms-report/src/main/java/com/wms/report/controller/ReportExportController.java"
    );

    @Test
    @DisplayName("业务菜单接口不应只使用登录态或ADMIN角色校验")
    void businessControllersShouldUseConcretePermissionCodes() throws IOException {
        List<String> weakControllers = TARGET_CONTROLLERS.stream()
                .filter(BusinessPermissionCoverageTest::containsWeakPermission)
                .toList();

        assertEquals(List.of(), weakControllers);
    }

    private static boolean containsWeakPermission(String sourcePath) {
        try {
            String source = Files.readString(Path.of(sourcePath));
            return source.contains("@PreAuthorize(\"isAuthenticated()\")")
                    || source.contains("@PreAuthorize(\"hasRole('ADMIN')\")");
        } catch (IOException ex) {
            throw new IllegalStateException("读取Controller失败: " + sourcePath, ex);
        }
    }
}
