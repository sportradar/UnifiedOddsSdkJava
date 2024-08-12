/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.markets;

import static com.sportradar.unifiedodds.sdk.conn.marketids.OddEvenMarketIds.EVEN_OUTCOME_ID;
import static com.sportradar.unifiedodds.sdk.conn.marketids.OddEvenMarketIds.ODD_OUTCOME_ID;
import static com.sportradar.unifiedodds.sdk.entities.SportEvents.any;
import static com.sportradar.utils.domain.markets.MarketIds.anyMarketId;
import static com.sportradar.utils.domain.specifiers.MarketSpecifiers.anySpecifiers;
import static com.sportradar.utils.generic.testing.RandomObjectPicker.pickOneRandomlyFrom;
import static java.util.Collections.singletonList;
import static java.util.Locale.ENGLISH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.impl.markets.NameProviderImpl.StatusMessage;
import com.sportradar.unifiedodds.sdk.testutil.generic.collections.Maps;
import com.sportradar.utils.domain.names.Languages;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.val;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

public class NameProviderStatusMessageTest {

    public static final int INVALID_MARKET_ID = 0;

    private final int mId = anyMarketId();
    private final Map<String, String> specif = anySpecifiers();
    private final String oId = anyOutcomeId();
    private final String oName = RandomStringUtils.randomAlphabetic(10);
    private final List<Locale> lang = Languages.anyLanguages();
    private final String anyMsg = RandomStringUtils.randomAlphabetic(10);

    @Test
    public void requiredParameters() {
        SportEvent event = any();
        assertThatThrownBy(() -> new StatusMessage(null, mId, specif, anyMsg, oId, oName, lang))
            .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new StatusMessage(event, INVALID_MARKET_ID, specif, anyMsg, oId, oName, lang)
            )
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new StatusMessage(event, mId, specif, null, oId, oName, lang))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new StatusMessage(event, mId, specif, "", oId, oName, lang))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new StatusMessage(event, mId, specif, anyMsg, oId, oName, null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void includesEvent() {
        val event = mock(SportEvent.class);
        val eventDesc = "eventDesc";
        when(event.toString()).thenReturn(eventDesc);
        val message = new StatusMessage(event, mId, specif, anyMsg, oId, oName, lang).toString();

        assertThat(message).contains("event=").contains(eventDesc);
    }

    @Test
    public void includesMarketId() {
        val message = new StatusMessage(any(), mId, specif, anyMsg, oId, oName, lang).toString();

        assertThat(message).contains("MarketId=" + mId);
    }

    @Test
    public void includesSpecifiers() {
        String name = "specifierName";
        String value = "specifierValue";
        val specifiers = Maps.of(name, value);
        val message = new StatusMessage(any(), mId, specifiers, anyMsg, oId, oName, lang).toString();

        assertThat(message).contains("Specifiers=").contains(name).contains(value);
    }

    @Test
    public void mentionsAbsenceOfSpecifiers() {
        val message = new StatusMessage(any(), mId, null, anyMsg, oId, oName, lang).toString();

        assertThat(message).contains("Specifiers=[null]");
    }

    @Test
    public void includesMessage() {
        String message = "anyMsg";
        val statusMessage = new StatusMessage(any(), mId, specif, message, oId, oName, lang).toString();

        assertThat(statusMessage).contains("Additional message:").contains(message);
    }

    @Test
    public void includesOutcomeId() {
        val message = new StatusMessage(any(), mId, specif, anyMsg, oId, oName, lang).toString();

        assertThat(message).contains("OutcomeId=" + oId);
    }

    @Test
    public void excludesOutcomeIdWhenUnavailable() {
        val message = new StatusMessage(any(), mId, specif, anyMsg, null, oName, lang).toString();

        assertThat(message).doesNotContain("OutcomeId=");
    }

    @Test
    public void includesOutcomeName() {
        val message = new StatusMessage(any(), mId, specif, anyMsg, oId, oName, lang).toString();

        assertThat(message).contains("nameDescriptor=").contains(oName);
    }

    @Test
    public void excludesOutcomeNameWhenUnavailable() {
        val message = new StatusMessage(any(), mId, specif, anyMsg, oId, null, lang).toString();

        assertThat(message).doesNotContain("nameDescriptor=");
    }

    @Test
    public void includesLanguages() {
        val oneLang = singletonList(ENGLISH);
        val message = new StatusMessage(any(), mId, specif, anyMsg, oId, oName, oneLang).toString();

        assertThat(message).contains("Locale=").contains(ENGLISH.toString());
    }

    public static String anyOutcomeId() {
        return pickOneRandomlyFrom(ODD_OUTCOME_ID, EVEN_OUTCOME_ID);
    }
}
