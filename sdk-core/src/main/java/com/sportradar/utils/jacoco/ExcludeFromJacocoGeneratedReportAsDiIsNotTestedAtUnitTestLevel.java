/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.utils.jacoco;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.TYPE })
public @interface ExcludeFromJacocoGeneratedReportAsDiIsNotTestedAtUnitTestLevel {
}
