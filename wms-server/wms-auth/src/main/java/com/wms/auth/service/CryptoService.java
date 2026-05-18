package com.wms.auth.service;

import com.wms.auth.domain.vo.RsaKeyPairResp;

public interface CryptoService {

    RsaKeyPairResp generateRsaKeyPair();

    String decryptPassword(String encryptedPassword);

    String hashPassword(String rawPassword);

    boolean verifyPassword(String rawPassword, String hashedPassword);
}
