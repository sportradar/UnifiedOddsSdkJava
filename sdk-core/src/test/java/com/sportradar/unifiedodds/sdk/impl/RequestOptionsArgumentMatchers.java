/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import static org.mockito.ArgumentMatchers.argThat;

import com.sportradar.unifiedodds.sdk.internal.caching.ExecutionPath;
import com.sportradar.unifiedodds.sdk.internal.caching.RequestOptions;
import org.mockito.ArgumentMatcher;

public class RequestOptionsArgumentMatchers {

    public static RequestOptions executionPathEq(RequestOptions expected) {
        ArgumentMatcher<RequestOptions> argumentMatcherArgumentMatcher = options ->
            options != null && options.getExecutionPath() == expected.getExecutionPath();
        return argThat(argumentMatcherArgumentMatcher);
    }

    public static RequestOptions executionPathEq(ExecutionPath expected) {
        ArgumentMatcher<RequestOptions> argumentMatcherArgumentMatcher = options ->
            options != null && options.getExecutionPath() == expected;
        return argThat(argumentMatcherArgumentMatcher);
    }
}
