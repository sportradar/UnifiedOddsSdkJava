/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.apireaders;

public class HttpHelperPutTest extends HttpHelperWithStubbedHttpClientTest {

    @Override
    InvokeHelpersHttpMethod httpMethodInvocationOn(HttpHelper httpHelper) {
        return path -> httpHelper.put(path);
    }
}
