/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.commoniam;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.UnsupportedEncodingException;

public class Jwts {

    @SuppressWarnings("MagicNumber")
    public static KeyPairs.SignedData parseJwtSignedDataComponents(String jwtToken)
        throws UnsupportedEncodingException {
        String[] parts = jwtToken.split("\\.");
        assertThat(parts).hasSize(3);

        String header = parts[0];
        String payload = parts[1];
        String signature = parts[2];

        byte[] signatureBytes = java.util.Base64.getUrlDecoder().decode(signature);
        String signingInput = header + "." + payload;
        byte[] signingInputBytes = signingInput.getBytes("UTF-8");

        return new KeyPairs.SignedData(signatureBytes, signingInputBytes);
    }
}
