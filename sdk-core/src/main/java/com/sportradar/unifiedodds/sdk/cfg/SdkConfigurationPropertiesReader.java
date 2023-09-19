/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

import com.google.common.collect.Maps;
import com.sportradar.unifiedodds.sdk.impl.util.files.ResourceReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({ "AvoidNoArgumentSuperConstructorCall", "ConstantName" })
public class SdkConfigurationPropertiesReader extends SdkConfigurationReader {

    private static final Logger logger = LoggerFactory.getLogger(SdkConfigurationPropertiesReader.class);
    private static final String SDK_PROPERTIES_FILENAME = "/UFSdkConfiguration.properties";

    private final String filename;
    private final ResourceReader resourceReader;

    public SdkConfigurationPropertiesReader(ResourceReader resourceReader) {
        this.resourceReader = resourceReader;
        filename = SDK_PROPERTIES_FILENAME;
    }

    public SdkConfigurationPropertiesReader(ResourceReader resourceReader, String filename) {
        this.resourceReader = resourceReader;
        this.filename = "/" + filename;
    }

    @Override
    Map<String, String> readConfiguration() {
        Properties prop = new Properties();

        InputStream in = resourceReader.readAsInputStream(filename);
        try {
            if (in != null) {
                prop.load(in);
            }
        } catch (IOException e) {
            logger.warn("SDK properties file loading failed, exc:", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // already closed,...
                }
            }
        }

        Map<String, String> result = Maps.newHashMapWithExpectedSize(prop.size());
        prop.stringPropertyNames().forEach(p -> result.put(p, prop.getProperty(p)));

        return result;
    }
}
