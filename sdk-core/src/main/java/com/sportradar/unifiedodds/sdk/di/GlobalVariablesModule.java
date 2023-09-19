/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.di;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.sportradar.unifiedodds.sdk.impl.rabbitconnection.SingleInstanceAmqpConnectionFactory;
import com.sportradar.unifiedodds.sdk.impl.util.files.ResourceReader;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Properties;

public class GlobalVariablesModule implements Module {

    private ResourceReader resourceReader;

    public GlobalVariablesModule(ResourceReader resourceReader) {
        this.resourceReader = resourceReader;
    }

    @Override
    public void configure(Binder binder) {
        binder.bindConstant().annotatedWith(Names.named("version")).to(loadVersion());
    }

    @Provides
    @Named("sdkStartupTime")
    @Singleton
    private Instant provideSdkStartupTime() {
        return Instant.now();
    }

    private String loadVersion() {
        try {
            InputStream is = resourceReader.readAsInputStream("/sr-sdk-version.properties");
            Properties props = new Properties();
            props.load(is);
            is.close();
            return props.getProperty("version");
        } catch (IOException ioe) {
            return "0.0";
        }
    }
}
