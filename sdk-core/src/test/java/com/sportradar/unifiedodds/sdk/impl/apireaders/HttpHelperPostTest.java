/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.apireaders;

import com.sportradar.unifiedodds.sdk.internal.impl.apireaders.HttpHelper;

public class HttpHelperPostTest extends HttpHelperWithStubbedHttpClientTest {

    @Override
    InvokeHelpersHttpMethod httpMethodInvocationOn(HttpHelper httpHelper) {
        return path -> httpHelper.post(path);
    }
}
