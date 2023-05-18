/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.markets;

import com.google.common.base.Preconditions;
import com.ibm.icu.text.RuleBasedNumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created on 22/06/2017.
 * // TODO @eti: Javadoc
 */
public class OrdinalNameExpression implements NameExpression {

    private final Operand operand;

    OrdinalNameExpression(Operand operand) {
        Preconditions.checkNotNull(operand);

        this.operand = operand;
    }

    @Override
    public String buildName(Locale locale) {
        // TODO maybe we should "cache" RuleBasedNumberFormat objects
        int intValue = operand.getIntValue();

        // first check Ordinal, then Spellout
        RuleBasedNumberFormat nf = new RuleBasedNumberFormat(locale, RuleBasedNumberFormat.ORDINAL);
        String ordinalRule = getOrdinalRuleName(nf);
        if (ordinalRule == null) {
            nf = new RuleBasedNumberFormat(locale, RuleBasedNumberFormat.SPELLOUT);
            ordinalRule = getSpelloutRuleName(nf);

            // if the ordinal rule cannot be found, we return the int value in 'ordinal format: 3. instead of 3rd
            if (ordinalRule == null) {
                return intValue + ".";
            }
        }

        String result = nf.format(intValue, ordinalRule);
        return result;
    }

    /**
     * Try to extract the rule name for "expand ordinal" from the given RuleBasedNumberFormat.
     * The rule name is locale sensitive, but usually starts with "%spellout-ordinal".
     * (the reason for this check is that not all the locales have the same "%spellout-ordinal" rule)
     *
     * @param rbnf The RuleBasedNumberFormat from where we will try to extract the rule name.
     * @return The rule name for "ordinal spell out".
     */
    private static String getSpelloutRuleName(final RuleBasedNumberFormat rbnf) {
        List<String> l = Arrays.asList(rbnf.getRuleSetNames());
        if (l.contains("%spellout-ordinal")) {
            return "%spellout-ordinal";
        } else if (l.contains("%spellout-ordinal-masculine")) {
            return "%spellout-ordinal-masculine";
        } else {
            for (String string : l) {
                if (string.startsWith("%spellout-ordinal")) {
                    return string;
                }
            }
        }
        return null;
    }

    /**
     * Try to extract the rule name for "expand ordinal" from the given RuleBasedNumberFormat.
     * The rule name is locale sensitive, but usually starts with "%digits-ordinal".
     * (the reason for this check is that not all the locales have the same "%digits-ordinal" rule)
     *
     * @param rbnf The RuleBasedNumberFormat from where we will try to extract the rule name.
     * @return The rule name for "ordinal spell out".
     */
    private static String getOrdinalRuleName(final RuleBasedNumberFormat rbnf) {
        List<String> l = Arrays.asList(rbnf.getRuleSetNames());
        if (l.contains("%digits-ordinal")) {
            return "%digits-ordinal";
        }
        for (String string : l) {
            if (string.startsWith("%digits-ordinal")) {
                return string;
            }
        }
        return null;
    }
}
