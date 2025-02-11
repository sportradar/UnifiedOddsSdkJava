/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.markets;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.internal.caching.ProfileCache;
import com.sportradar.utils.Urn;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created on 21/06/2017.
 * // TODO @eti: Javadoc
 */
@SuppressWarnings({ "ClassDataAbstractionCoupling", "ClassFanOutComplexity", "ReturnCount" })
public class NameExpressionFactoryImpl implements NameExpressionFactory {

    private static final Pattern SEQUENCED_COMPETITOR_REGEX_PATTERN = Pattern.compile("\\Acompetitor[12]");
    private static final String EVENT_OPERAND_PLACEHOLDER = "event";
    private final OperandFactory operandFactory;
    private final ProfileCache profileCache;

    @Inject
    public NameExpressionFactoryImpl(OperandFactory operandFactory, ProfileCache profileCache) {
        Preconditions.checkNotNull(operandFactory);
        Preconditions.checkNotNull(profileCache);

        this.operandFactory = operandFactory;
        this.profileCache = profileCache;
    }

    @Override
    public NameExpression buildExpression(
        SportEvent sportEvent,
        Map<String, String> specifiers,
        String operator,
        String operand
    ) {
        Preconditions.checkNotNull(sportEvent);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(operand));

        if (operator == null) {
            ensureSpecifiersNotNullOrEmpty(specifiers);
            return new CardinalNameExpression(operandFactory.buildOperand(specifiers, operand));
        }

        switch (operator) {
            case "+":
                ensureSpecifiersNotNullOrEmpty(specifiers);
                return new PlusNameExpression(operandFactory.buildOperand(specifiers, operand));
            case "-":
                ensureSpecifiersNotNullOrEmpty(specifiers);
                return new MinusNameExpression(operandFactory.buildOperand(specifiers, operand));
            case "$":
                return buildEntityNameExpression(operand, sportEvent);
            case "!":
                ensureSpecifiersNotNullOrEmpty(specifiers);
                return new OrdinalNameExpression(operandFactory.buildOperand(specifiers, operand));
            case "%":
                ensureSpecifiersNotNullOrEmpty(specifiers);
                return buildProfileExpression(operand, specifiers);
            default:
                throw new IllegalArgumentException(
                    "Operator " + operator + " is not supported. Supported operators are: +,-,$,!,%"
                );
        }
    }

    private NameExpression buildEntityNameExpression(String operand, SportEvent sportEvent) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(operand));
        Preconditions.checkNotNull(sportEvent);

        if (SEQUENCED_COMPETITOR_REGEX_PATTERN.matcher(operand).find()) {
            return new EntityNameExpression(operand, sportEvent);
        } else if (EVENT_OPERAND_PLACEHOLDER.equals(operand)) {
            return new SportEventNameExpression(sportEvent);
        }

        throw new IllegalArgumentException(
            "operand:" +
            operand +
            " is not a valid operand for $ operator. Valid operators are: 'competitor1', 'competitor2'"
        );
    }

    private NameExpression buildProfileExpression(String operand, Map<String, String> specifiers) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(operand));

        Operand operandDefinition = operandFactory.buildOperand(specifiers, operand);
        if ("server".equals(operand)) {
            return new CompetitorProfileExpression(profileCache, operandDefinition);
        }
        Urn id = Urn.parse(operandDefinition.getStringValue());
        if (id.getType().equals("player")) {
            return new PlayerProfileExpression(profileCache, operandDefinition);
        } else {
            return new CompetitorProfileExpression(profileCache, operandDefinition);
        }
    }

    private void ensureSpecifiersNotNullOrEmpty(Map<String, String> specifiers) {
        if (specifiers == null || specifiers.isEmpty()) {
            throw new IllegalArgumentException("Specifiers can not be null or empty");
        }
    }
}
