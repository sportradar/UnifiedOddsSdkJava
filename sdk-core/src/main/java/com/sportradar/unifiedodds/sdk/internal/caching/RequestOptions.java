/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.caching;

import java.util.Objects;

public class RequestOptions {

    private final ExecutionPath executionPath;

    private RequestOptions(ExecutionPath executionPath) {
        this.executionPath = executionPath;
    }

    public ExecutionPath getExecutionPath() {
        return executionPath;
    }

    public static RequestOptionsBuilder requestOptions() {
        return new RequestOptionsBuilder();
    }

    public static final class RequestOptionsBuilder {

        private ExecutionPath executionPath;

        public RequestOptionsBuilder setExecutionPath(ExecutionPath path) {
            this.executionPath = path;
            return this;
        }

        public RequestOptions build() {
            return new RequestOptions(executionPath);
        }
    }
}
