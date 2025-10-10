/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example;

import static com.sportradar.unifiedodds.sdk.cfg.UofClientAuthentication.privateKeyJwt;

import com.sportradar.unifiedodds.example.common.GlobalEventsListener;
import com.sportradar.unifiedodds.example.common.MessageListener;
import com.sportradar.unifiedodds.sdk.MessageInterest;
import com.sportradar.unifiedodds.sdk.UofSdk;
import com.sportradar.unifiedodds.sdk.UofSessionBuilder;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.unifiedodds.sdk.managers.MarketDescriptionManager;
import com.sportradar.unifiedodds.sdk.managers.ProducerManager;
import com.sportradar.unifiedodds.sdk.managers.SportDataProvider;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

/**
 * The following example is a very simple example that just connects to the Unified Odds Feed and
 * prints out some information about all the messages it receives.
 */
@SuppressWarnings({ "HideUtilityClassConstructor", "MagicNumber", "MethodLength" })
public class BasicUofSdkExampleMain {

    public static void main(String[] args) throws Exception {
        // first you need to prepare a configuration suitable to your use. The most important thing is
        // the authentication configuration.
        // 1. generate a private/public key pair
        // 2. upload the public key to Sportradar, so it can be used to authenticate your service.
        // 3. Sportradar will provide you with a key id and service id (OAuth client id).
        // 4. In order to start SDK you need both:
        //   a. access token and
        //   b. set up client authentication using the private key, signing key id and client id.
        // Both of them need to belong to the same environment e.g. Integration.

        String pkcs8RsaSha256PrivateKeyPem =
            "-----BEGIN PRIVATE KEY-----\n" +
            "your PKCS#8 RSA SHA256 private key PEM file content\n" +
            "-----END PRIVATE KEY-----";

        PrivateKey privateKey = parsePkcs8RsaUnencryptedPrivateKeyPem(pkcs8RsaSha256PrivateKeyPem);

        UofConfiguration config = UofSdk
            .getUofConfigurationBuilder()
            .setClientAuthentication(
                privateKeyJwt()
                    .setSigningKeyId("yourSigningKeyId") // Key id issued after registering the Public Key with Sportradar
                    .setClientId("yourServiceId") // Sportradar Service Id
                    .setPrivateKey(privateKey)
                    .build()
            )
            .buildConfigFromSdkProperties();
        // create the new feed
        UofSdk uofSdk = new UofSdk(new GlobalEventsListener(), config);

        // access the producer manager
        ProducerManager producerManager = uofSdk.getProducerManager();

        // set the last received message timestamp through the producer - if known
        // (as an example, we set the last message received timestamp as 2 days ago)
        /*Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -2);
        producerManager.setProducerRecoveryFromTimestamp(1,
                cal.getTime().getTime());*/

        // with the marketManager you can access various data about the available markets
        MarketDescriptionManager marketManager = uofSdk.getMarketDescriptionManager();

        // With the sportDataProvider helper you can access various data about the ongoing events
        SportDataProvider sportDataProvider = uofSdk.getSportDataProvider();

        // In this example we will create 1 session which will receive all messages from all active producers
        // We can accomplish this with the UofSessionBuilder class.
        UofSessionBuilder sessionBuilder = uofSdk.getSessionBuilder();

        MessageListener listener = new MessageListener("AllMessages");
        sessionBuilder.setListener(listener).setMessageInterest(MessageInterest.AllMessages).build();

        // Open the feed with all the built sessions
        uofSdk.open();

        // The messages will now arrive in a separate thread to the MessageListener

        // Let's sleep awhile (30 minutes) and see what gets printed.
        Thread.sleep(1000 * 60 * 30L);

        // finally we close the feed.
        uofSdk.close();
    }

    public static PrivateKey parsePkcs8RsaUnencryptedPrivateKeyPem(String pemFileContent) throws Exception {
        int start = pemFileContent.indexOf("-----BEGIN PRIVATE KEY-----");
        int end = pemFileContent.indexOf("-----END PRIVATE KEY-----");
        if (start < 0 || end < 0) {
            throw new IllegalArgumentException("Not a PKCS#8 PEM: missing BEGIN/END PRIVATE KEY markers.");
        }

        String base64 = pemFileContent
            .substring(start + "-----BEGIN PRIVATE KEY-----".length(), end)
            .replaceAll("\\s", "");
        byte[] der = Base64.getDecoder().decode(base64);

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(der);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }
}
