package com.wms.business.domain.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Order DTO validation tests.
 */
@DisplayName("Business order DTO validation")
class OrderDtoValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidator() {
        validatorFactory.close();
    }

    @Test
    @DisplayName("all stock-moving orders should reject empty detail lists")
    void shouldRejectEmptyDetails() {
        assertHasPropertyViolation(validInboundWithDetails(Collections.emptyList()), "details");
        assertHasPropertyViolation(validOutboundWithDetails(Collections.emptyList()), "details");
        assertHasPropertyViolation(validTransferWithDetails(Collections.emptyList()), "details");
        assertHasPropertyViolation(validScrapWithDetails(Collections.emptyList()), "details");
        assertHasPropertyViolation(validReturnWithDetails(Collections.emptyList()), "details");
    }

    @Test
    @DisplayName("all stock-moving order details should reject non-positive quantities")
    void shouldRejectNonPositiveQuantities() {
        assertHasPropertyViolation(validInboundWithDetails(List.of(inboundDetail(0))), "details[0].quantity");
        assertHasPropertyViolation(validOutboundWithDetails(List.of(outboundDetail(0))), "details[0].quantity");
        assertHasPropertyViolation(validTransferWithDetails(List.of(transferDetail(0))), "details[0].quantity");
        assertHasPropertyViolation(validScrapWithDetails(List.of(scrapDetail(0))), "details[0].quantity");
        assertHasPropertyViolation(validReturnWithDetails(List.of(returnDetail(0))), "details[0].quantity");
    }

    @Test
    @DisplayName("order type and return condition should stay in supported ranges")
    void shouldRejectOutOfRangeTypeFields() {
        InboundOrderDto inbound = validInboundWithDetails(List.of(inboundDetail(1)));
        inbound.setOrderType(0);
        assertHasPropertyViolation(inbound, "orderType");

        OutboundOrderDto outbound = validOutboundWithDetails(List.of(outboundDetail(1)));
        outbound.setOrderType(4);
        assertHasPropertyViolation(outbound, "orderType");

        ReturnOrderDto.ReturnDetailDto returnDetail = returnDetail(1);
        returnDetail.setConditionStatus(5);
        assertHasPropertyViolation(validReturnWithDetails(List.of(returnDetail)), "details[0].conditionStatus");
    }

    private void assertHasPropertyViolation(Object dto, String propertyPath) {
        Set<ConstraintViolation<Object>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> propertyPath.equals(v.getPropertyPath().toString())),
                "expected violation for property " + propertyPath + ", got " + violations);
    }

    private InboundOrderDto validInboundWithDetails(List<InboundOrderDto.InboundDetailDto> details) {
        InboundOrderDto dto = new InboundOrderDto();
        dto.setWarehouseId(1L);
        dto.setOrderType(1);
        dto.setDetails(details);
        return dto;
    }

    private InboundOrderDto.InboundDetailDto inboundDetail(int quantity) {
        InboundOrderDto.InboundDetailDto detail = new InboundOrderDto.InboundDetailDto();
        detail.setItemId(1L);
        detail.setBinId(10L);
        detail.setQuantity(quantity);
        return detail;
    }

    private OutboundOrderDto validOutboundWithDetails(List<OutboundOrderDto.OutboundDetailDto> details) {
        OutboundOrderDto dto = new OutboundOrderDto();
        dto.setWarehouseId(1L);
        dto.setOrderType(1);
        dto.setDetails(details);
        return dto;
    }

    private OutboundOrderDto.OutboundDetailDto outboundDetail(int quantity) {
        OutboundOrderDto.OutboundDetailDto detail = new OutboundOrderDto.OutboundDetailDto();
        detail.setItemId(1L);
        detail.setBinId(10L);
        detail.setQuantity(quantity);
        return detail;
    }

    private TransferOrderDto validTransferWithDetails(List<TransferOrderDto.TransferDetailDto> details) {
        TransferOrderDto dto = new TransferOrderDto();
        dto.setFromWarehouseId(1L);
        dto.setToWarehouseId(2L);
        dto.setDetails(details);
        return dto;
    }

    private TransferOrderDto.TransferDetailDto transferDetail(int quantity) {
        TransferOrderDto.TransferDetailDto detail = new TransferOrderDto.TransferDetailDto();
        detail.setItemId(1L);
        detail.setFromBinId(10L);
        detail.setToBinId(20L);
        detail.setQuantity(quantity);
        return detail;
    }

    private ScrapOrderDto validScrapWithDetails(List<ScrapOrderDto.ScrapDetailDto> details) {
        ScrapOrderDto dto = new ScrapOrderDto();
        dto.setWarehouseId(1L);
        dto.setScrapReason("damaged");
        dto.setDetails(details);
        return dto;
    }

    private ScrapOrderDto.ScrapDetailDto scrapDetail(int quantity) {
        ScrapOrderDto.ScrapDetailDto detail = new ScrapOrderDto.ScrapDetailDto();
        detail.setItemId(1L);
        detail.setBinId(10L);
        detail.setQuantity(quantity);
        return detail;
    }

    private ReturnOrderDto validReturnWithDetails(List<ReturnOrderDto.ReturnDetailDto> details) {
        ReturnOrderDto dto = new ReturnOrderDto();
        dto.setOutboundOrderId(1L);
        dto.setDetails(details);
        return dto;
    }

    private ReturnOrderDto.ReturnDetailDto returnDetail(int quantity) {
        ReturnOrderDto.ReturnDetailDto detail = new ReturnOrderDto.ReturnDetailDto();
        detail.setItemId(1L);
        detail.setBinId(10L);
        detail.setQuantity(quantity);
        return detail;
    }
}
