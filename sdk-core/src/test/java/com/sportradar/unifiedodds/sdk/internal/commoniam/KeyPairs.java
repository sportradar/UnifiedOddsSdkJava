/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.commoniam;

import static org.assertj.core.api.Assertions.assertThat;

import java.security.*;

public class KeyPairs {

    @SuppressWarnings("MagicNumber")
    public static KeyPair rsaKeyPair2048keySize() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    public static void verifySignatureWithPublicKey(SignedData signedData1, PublicKey publicKey)
        throws Exception {
        SignedData signedData = signedData1;

        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(publicKey);
        sig.update(signedData.getSigningInputBytes());
        boolean isValid = sig.verify(signedData.getSignatureBytes());

        assertThat(isValid)
            .withFailMessage(
                "JWT signature verification failed - token was not properly signed with the private key"
            )
            .isTrue();
    }

    public static class SignedData {

        private final byte[] signatureBytes;
        private final byte[] signingInputBytes;

        public SignedData(byte[] signatureBytes, byte[] signingInputBytes) {
            this.signatureBytes = signatureBytes;
            this.signingInputBytes = signingInputBytes;
        }

        public byte[] getSignatureBytes() {
            return signatureBytes;
        }

        public byte[] getSigningInputBytes() {
            return signingInputBytes;
        }
    }
}
