/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

import com.sportradar.unifiedodds.sdk.impl.util.files.ResourceReader;
import com.sportradar.unifiedodds.sdk.impl.util.javaclass.ClassResolver;
import java.io.InputStream;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

@SuppressWarnings({ "AvoidNoArgumentSuperConstructorCall", "ConstantName", "LambdaBodyLength" })
public class SdkConfigurationYamlReader extends SdkConfigurationReader {

    private static final Logger logger = LoggerFactory.getLogger(SdkConfigurationYamlReader.class);
    private static final String SDK_YAML_FILENAME = "/application.yml";
    private static final String ROOT_SPORTRADAR_TAG = "sportradar";
    private static final String SECOND_LEVEL_TAG = "sdk";
    private static final String THIRD_LEVEL_TAG = "uf";

    private final Yaml yaml;
    private final ResourceReader resourceReader;
    private final String filename;
    private final ClassResolver classResolver;

    SdkConfigurationYamlReader(ClassResolver classResolver, Yaml yaml, ResourceReader resourceReader) {
        this.classResolver = classResolver;
        this.yaml = yaml;
        this.resourceReader = resourceReader;
        this.filename = SDK_YAML_FILENAME;
    }

    SdkConfigurationYamlReader(
        ClassResolver classResolver,
        Yaml yaml,
        ResourceReader resourceReader,
        String filename
    ) {
        this.classResolver = classResolver;
        this.yaml = yaml;
        this.resourceReader = resourceReader;
        this.filename = "/" + filename;
    }

    @Override
    Map<String, String> readConfiguration() {
        isYamlReaderDependencyPresent();

        InputStream in = resourceReader.readAsInputStream(filename);
        if (in != null) {
            Iterable<Object> objects = yaml.loadAll(in);
            return tryFindSrYamlProperties(objects);
        }

        return Collections.emptyMap();
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> tryFindSrYamlProperties(Iterable<Object> objects) {
        for (Object object : objects) {
            if (object instanceof Map) {
                Map<String, Object> castedMap = (Map<String, Object>) object;
                if (
                    castedMap.containsKey(ROOT_SPORTRADAR_TAG) &&
                    castedMap.get(ROOT_SPORTRADAR_TAG) instanceof Map
                ) {
                    return provideParsedMap((Map<String, Object>) castedMap.get(ROOT_SPORTRADAR_TAG));
                }
            }
        }

        logger.warn(
            "Could not find valid UF SDK YAML root property({}) in the provided '{}'",
            ROOT_SPORTRADAR_TAG,
            filename
        );

        return Collections.emptyMap();
    }

    @SuppressWarnings("unchecked")
    private static Map<String, String> provideParsedMap(Map<String, Object> objects) {
        Object sdkObject = objects.get(SECOND_LEVEL_TAG);
        if (!(sdkObject instanceof Map)) {
            return Collections.emptyMap();
        }

        Object ufObject = ((Map<String, Object>) sdkObject).get(THIRD_LEVEL_TAG);
        if (!(ufObject instanceof Map)) {
            return Collections.emptyMap();
        }

        Map<String, Object> sdkConfiguration = (Map<String, Object>) ufObject;

        return mapYamlEntriesToSdkMap(sdkConfiguration);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, String> mapYamlEntriesToSdkMap(Map<String, Object> sdkConfiguration) {
        Map<String, String> result = new HashMap<>();
        sdkConfiguration.forEach((k, v) -> {
            if (v instanceof String) {
                result.put(prepKey(k), (String) v);
            } else if (v instanceof List) {
                result.put(prepKey(k), prepListEntry((List) v));
            } else if (v instanceof Integer) {
                result.put(prepKey(k), String.valueOf(v));
            } else if (v instanceof Boolean) {
                result.put(prepKey(k), String.valueOf(v));
            } else if (v instanceof Map) {
                result.putAll(prepareMapEntries(k, (Map<String, Object>) v));
            } else {
                logger.warn("Unknown YAML entry format. Key: {}, Value: {}, Type: {}", k, v, v.getClass());
            }
        });

        return result;
    }

    private static Map<String, String> prepareMapEntries(String parentKey, Map<String, Object> values) {
        Map<String, String> result = new HashMap<>();

        values.forEach((k, v) -> {
            String preparedKey = parentKey + k.substring(0, 1).toUpperCase() + k.substring(1);
            result.put(prepKey(preparedKey), String.valueOf(v));
        });

        return result;
    }

    private static String prepListEntry(List values) {
        StringJoiner sj = new StringJoiner(",");
        for (Object value : values) {
            sj.add(String.valueOf(value));
        }

        return sj.toString();
    }

    private static String prepKey(String key) {
        return "uf.sdk." + key;
    }

    private void isYamlReaderDependencyPresent() {
        try {
            classResolver.resolveByName("org.yaml.snakeyaml.Yaml");
        } catch (IllegalStateException ex) {
            throw new IllegalStateException("Yaml configuration reader dependency missing", ex);
        }
    }
}
