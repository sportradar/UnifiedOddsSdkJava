/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example.common;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@SuppressWarnings({ "HideUtilityClassConstructor" })
public final class Pkcs8PrivateKeyLoader {

    private static final String BEGIN_PKCS8 = "-----BEGIN PRIVATE KEY-----";
    private static final String END_PKCS8 = "-----END PRIVATE KEY-----";

    private Pkcs8PrivateKeyLoader() {}

    public static PrivateKey loadFromPath(String pemPath) throws Exception {
        Path path = Paths.get(pemPath);
        String pemContent = new String(Files.readAllBytes(path), StandardCharsets.US_ASCII).trim();
        return parse(pemContent);
    }

    public static PrivateKey parse(String pemContent) throws Exception {
        if (pemContent == null || pemContent.trim().isEmpty()) {
            throw new IllegalArgumentException("PEM content is empty.");
        }

        int start = pemContent.indexOf(BEGIN_PKCS8);
        int end = pemContent.indexOf(END_PKCS8);
        if (start < 0 || end < 0 || end <= start) {
            throw new IllegalArgumentException("Not a PKCS#8 PEM: missing BEGIN/END PRIVATE KEY markers.");
        }

        String base64 = pemContent.substring(start + BEGIN_PKCS8.length(), end).replaceAll("\\s", "");
        byte[] der = Base64.getDecoder().decode(base64);

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(der);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }
}
