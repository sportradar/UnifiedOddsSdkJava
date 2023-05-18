/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

/**
 * Defines possible validation results
 */
@SuppressWarnings({ "NoEnumTrailingComma" })
public enum ValidationResult {
    /**
     * The validation was successful, the validated object is valid
     */
    Success,

    /**
     * The validation detected some minor issues, but the validated object can still be used for further processing
     */
    ProblemsDetected,

    /**
     * The validation failed, the validated object is not valid
     */
    Failure,
}
