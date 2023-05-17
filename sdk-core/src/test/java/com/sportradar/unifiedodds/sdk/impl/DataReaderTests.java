package com.sportradar.unifiedodds.sdk.impl;

import com.sportradar.uf.datamodel.UFAlive;
import com.sportradar.uf.sportsapi.datamodel.SAPIFixturesEndpoint;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DataProviderException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DeserializationException;
import org.junit.Assert;
import org.junit.Test;

public class DataReaderTests {

    @Test
    public void mockFeedMessageReader() throws DeserializationException {
        UFAlive alive = XmlMessageReader.readMessageFromResource("test/feed_xml/alive.xml");
        Assert.assertNotNull(alive);
    }

    @Test
    public void mockSportsApiMessageReader() throws DeserializationException {
        SAPIFixturesEndpoint fixturesEndpoint = XmlMessageReader.readMessageFromResource(
            "test/rest/fixtures.de.xml"
        );
        Assert.assertNotNull(fixturesEndpoint);
    }

    @Test
    public void mockDataProvider() throws DataProviderException {
        TestingDataProvider<SAPIFixturesEndpoint> dataProvider = new TestingDataProvider<>(
            "test/rest/fixtures.de.xml"
        );
        SAPIFixturesEndpoint data = dataProvider.getData();
        Assert.assertNotNull(data);
    }
}
