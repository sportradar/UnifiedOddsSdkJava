/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.rabbit.integration;

import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import lombok.Value;
import lombok.val;

@Value
@SuppressWarnings({ "VisibilityModifier", "MagicNumber" })
public class CommonIamData {

    String clientId;
    String keyId;
    PrivateKey privateKey;

    public static CommonIamData with(final String username, final String password, String privateKeyBase64) {
        return new CommonIamData(username, password, parseBase64PrivateKey(privateKeyBase64));
    }

    public static CommonIamData any() {
        return with("SomeName", "someKeyId", anyPrivateKeyAsBase64());
    }

    private static String anyPrivateKeyAsBase64() {
        try {
            val keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            val keyPair = keyGen.generateKeyPair();
            val privateKey = keyPair.getPrivate();
            val encoded = privateKey.getEncoded();
            return Base64.getEncoder().encodeToString(encoded);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to create dummy private key", e);
        }
    }

    private static PrivateKey parseBase64PrivateKey(String base64PrivateKey) {
        try {
            val cleanBase64 = base64PrivateKey.replaceAll("\\s", "");
            val keyBytes = Base64.getDecoder().decode(cleanBase64);
            val keySpec = new PKCS8EncodedKeySpec(keyBytes);
            val keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid base64 encoding in private key", e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("RSA algorithm not available", e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException("Invalid private key format - expected PKCS#8", e);
        }
    }
}
