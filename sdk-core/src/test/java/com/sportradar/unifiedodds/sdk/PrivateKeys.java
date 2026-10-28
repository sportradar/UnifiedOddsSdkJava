/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import lombok.val;

public class PrivateKeys {

    public static PrivateKey anyPrivateKey() {
        try {
            val keyGen = KeyPairGenerator.getInstance("RSA");
            final int keySize = 2048;
            keyGen.initialize(keySize);
            val keyPair = keyGen.generateKeyPair();
            return keyPair.getPrivate();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to create dummy private key", e);
        }
    }
}
