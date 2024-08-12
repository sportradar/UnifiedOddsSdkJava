package com.sportradar.unifiedodds.sdk.impl.oddsentities;

import static com.sportradar.unifiedodds.sdk.impl.Constants.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.inject.Injector;
import com.sportradar.uf.datamodel.*;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.SportEntityFactory;
import com.sportradar.unifiedodds.sdk.di.TestInjectorFactory;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.impl.FeedMessageFactory;
import com.sportradar.unifiedodds.sdk.impl.XmlMessageReader;
import com.sportradar.unifiedodds.sdk.oddsentities.*;
import com.sportradar.unifiedodds.sdk.shared.StubUofConfiguration;
import com.sportradar.utils.Urn;
import java.util.Arrays;
import java.util.Locale;
import lombok.val;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings({ "ClassFanOutComplexity", "MagicNumber", "VisibilityModifier" })
public class FeedMessageFactoryTest {

    SportEntityFactory entityFactory;
    FeedMessageFactory factory;
    SportEvent sportEvent;

    byte[] rawMsg = new byte[] {};
    MessageTimestamp timestamp = new MessageTimestampImpl(1);

    @BeforeEach
    public void setup() throws Exception {
        val configuration = configurationWithAnyDefaultLanguageAndThrowingOnErrors();
        Injector injector = new TestInjectorFactory(configuration, new StubUofConfiguration()).create();
        factory = injector.getInstance(FeedMessageFactory.class);
        entityFactory = injector.getInstance(SportEntityFactory.class);

        sportEvent =
            entityFactory.buildSportEvent(
                Urn.parse("sr:match:9578495"),
                Urn.parse("sr:sport:1"),
                Arrays.asList(Locale.ENGLISH),
                false
            );
    }

    @Test
    public void buildsBetStop() throws Exception {
        UfBetStop msg = XmlMessageReader.readMessageFromResource(BET_STOP_MSG_URI);

        BetStop<SportEvent> result = factory.buildBetStop(sportEvent, msg, rawMsg, timestamp);

        Assert.assertNotNull(result);
    }

    @Test
    public void buildsFixtureChange() throws Exception {
        UfFixtureChange msg = XmlMessageReader.readMessageFromResource(FIXTURE_CHANGE_MSG_URI);

        FixtureChange<SportEvent> result = factory.buildFixtureChange(sportEvent, msg, rawMsg, timestamp);

        Assert.assertNotNull(result);
    }

    @Test
    public void buildsBetSettlement() throws Exception {
        UfBetSettlement msg = XmlMessageReader.readMessageFromResource(BET_SETTLEMENT_MSG_URI);

        BetSettlement<SportEvent> result = factory.buildBetSettlement(sportEvent, msg, rawMsg, timestamp);

        Assert.assertNotNull(result);
    }

    @Test
    public void buildsRollbackBetSettlement() throws Exception {
        UfRollbackBetSettlement msg = XmlMessageReader.readMessageFromResource(
            ROLLBACK_BET_SETTLEMENT_MSG_URI
        );

        RollbackBetSettlement<SportEvent> result = factory.buildRollbackBetSettlement(
            sportEvent,
            msg,
            rawMsg,
            timestamp
        );

        Assert.assertNotNull(result);
    }

    @Test
    public void buildsOddsChange() throws Exception {
        UfOddsChange msg = XmlMessageReader.readMessageFromResource(ODDS_CHANGE_MSG_URI);

        OddsChange<SportEvent> result = factory.buildOddsChange(sportEvent, msg, rawMsg, timestamp);

        Assert.assertNotNull(result);
    }

    @Test
    public void buildsRollbackBetCancel() throws Exception {
        UfRollbackBetCancel msg = XmlMessageReader.readMessageFromResource(ROLLBACK_BET_CANCEL_MSG_URI);

        RollbackBetCancel<SportEvent> result = factory.buildRollbackBetCancel(
            sportEvent,
            msg,
            rawMsg,
            timestamp
        );

        Assert.assertNotNull(result);
    }

    @Test
    public void buildsBetCancel() throws Exception {
        UfBetCancel msg = XmlMessageReader.readMessageFromResource(BET_CANCEL_MSG_URI);

        BetCancel<SportEvent> result = factory.buildBetCancel(sportEvent, msg, rawMsg, timestamp);

        Assert.assertNotNull(result);
    }

    @Test
    public void buildsCashoutProbabilities() throws Exception {
        UfCashout msg = XmlMessageReader.readMessageFromResource("test/feed_xml/probabilities.xml");

        CashOutProbabilities<SportEvent> result = factory.buildCashOutProbabilities(
            sportEvent,
            msg,
            timestamp
        );

        Assert.assertNotNull(result);
    }

    @Test
    public void buildsNoOddsCashoutProbabilities() throws Exception {
        UfCashout msg = XmlMessageReader.readMessageFromResource("test/feed_xml/probabilities_no_odds.xml");

        CashOutProbabilities<SportEvent> result = factory.buildCashOutProbabilities(
            sportEvent,
            msg,
            timestamp
        );

        Assert.assertNotNull(result);
        Assert.assertTrue(result.getMarkets().isEmpty());
        Assert.assertNull(result.getBetstopReason());
        Assert.assertNull(result.getBettingStatus());
    }

    @Test
    public void buildsOptionalFieldsCashoutProbabilities() throws Exception {
        UfCashout msg = XmlMessageReader.readMessageFromResource("test/feed_xml/probabilities.xml");

        msg.getOdds().setBetstopReason(2);
        msg.getOdds().setBettingStatus(3);

        CashOutProbabilities<SportEvent> result = factory.buildCashOutProbabilities(
            sportEvent,
            msg,
            timestamp
        );

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getMarkets());
        Assert.assertNotNull(result.getBetstopReasonValue());
        Assert.assertNotNull(result.getBetstopReasonValue());

        Assert.assertEquals(2, result.getBetstopReasonValue().getId());
        Assert.assertEquals(3, result.getBettingStatusValue().getId());
    }

    private static SdkInternalConfiguration configurationWithAnyDefaultLanguageAndThrowingOnErrors() {
        SdkInternalConfiguration mock = mock(SdkInternalConfiguration.class);
        when(mock.getDefaultLocale()).thenReturn(Locale.UK);
        when(mock.getExceptionHandlingStrategy()).thenReturn(ExceptionHandlingStrategy.Throw);
        return mock;
    }
}
