package com.sportradar.unifiedodds.sdk.shared;

import com.sportradar.unifiedodds.sdk.internal.impl.ProducerData;
import com.sportradar.unifiedodds.sdk.internal.impl.ProducerDataProvider;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Class TestProducersProvider for setting default producers list
 */
@SuppressWarnings({ "MagicNumber", "MemberName", "MethodLength", "VisibilityModifier" })
public class TestProducersProvider implements ProducerDataProvider {

    /**
     * Gets the producers
     */
    public List<ProducerData> Producers;

    /**
     * Initializes a new instance of the TestProducersProvider class. Loads default list of producers.
     */
    public TestProducersProvider() {
        Producers = getProducers();
    }

    @Override
    public List<ProducerData> getAvailableProducers() {
        return Producers;
    }

    /**
     * Gets the available producers from api (default setup used in most tests)
     * @return the available producers from api (default setup used in most tests)
     */
    public List<ProducerData> getProducers() {
        int maxInactivitySeconds = 20;
        int maxRecoveryTime = 3600;

        List<ProducerData> producers = Arrays.asList(
            new ProducerData(
                1,
                "LO",
                "Live Odds",
                true,
                "https://stgapi.betradar.com/v1/liveodds/",
                "live",
                600
            ),
            new ProducerData(
                3,
                "Ctrl",
                "Betradar Ctrl",
                true,
                "https://stgapi.betradar.com/v1/pre/",
                "prematch",
                4320
            ),
            new ProducerData(
                4,
                "BetPal",
                "BetPal",
                true,
                "https://stgapi.betradar.com/v1/betpal/",
                "live",
                4320
            ),
            new ProducerData(
                5,
                "PremiumCricket",
                "Premium Cricket",
                true,
                "https://stgapi.betradar.com/v1/premium_cricket/",
                "live|prematch",
                4320
            ),
            new ProducerData(
                6,
                "VF",
                "Virtual football",
                true,
                "https://stgapi.betradar.com/v1/vf/",
                "virtual",
                180
            ),
            new ProducerData(
                7,
                "WNS",
                "Numbers Betting",
                true,
                "https://stgapi.betradar.com/v1/wns/",
                "prematch",
                4320
            ),
            new ProducerData(
                8,
                "VBL",
                "Virtual Basketball League",
                false,
                "https://stgapi.betradar.com/v1/vbl/",
                "virtual",
                180
            )
        );

        return producers;
    }

    public ProducerData getProducer(int i, boolean createIfMissing) {
        ProducerData defaultProducerData = new ProducerData(
            i,
            "Producer " + i,
            "Descirption for producer " + i,
            true,
            "some url",
            "prematch",
            100
        );
        if (!Producers.isEmpty()) {
            Optional<ProducerData> producerData = Producers.stream().filter(f -> f.getId() == i).findFirst();
            if (producerData.isPresent()) {
                return producerData.get();
            }
            return createIfMissing ? defaultProducerData : null;
        } else {
            return createIfMissing ? defaultProducerData : null;
        }
    }
}
