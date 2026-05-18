package com.wms.auth.controller;

import com.wms.auth.domain.vo.RsaKeyPairResp;
import com.wms.auth.service.CryptoService;
import com.wms.common.domain.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "加密管理")
@RestController
@RequestMapping("/auth/crypto")
@RequiredArgsConstructor
public class CryptoController {

    private final CryptoService cryptoService;

    @Operation(summary = "获取RSA公钥")
    @GetMapping("/rsa-public-key")
    public R<RsaKeyPairResp> getRsaPublicKey() {
        return R.ok(cryptoService.generateRsaKeyPair());
    }
}
