/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.markets;

import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import java.util.Map;

/**
 * Created on 15/06/2017.
 * // TODO @eti: Javadoc
 */
public interface NameExpressionFactory {
    NameExpression buildExpression(
        SportEvent sportEvent,
        Map<String, String> specifiers,
        String operator,
        String operand
    );
}
