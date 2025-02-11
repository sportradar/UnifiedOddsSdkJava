package com.sportradar.unifiedodds.sdk.impl;

import com.sportradar.uf.datamodel.UfAlive;
import com.sportradar.uf.sportsapi.datamodel.SapiFixturesEndpoint;
import com.sportradar.unifiedodds.sdk.internal.exceptions.DataProviderException;
import com.sportradar.unifiedodds.sdk.internal.exceptions.DeserializationException;
import com.sportradar.unifiedodds.sdk.internal.impl.TestingDataProvider;
import org.junit.Assert;
import org.junit.Test;

public class DataReaderTests {

    @Test
    public void mockFeedMessageReader() throws DeserializationException {
        UfAlive alive = XmlMessageReader.readMessageFromResource("test/feed_xml/alive.xml");
        Assert.assertNotNull(alive);
    }

    @Test
    public void mockSportsApiMessageReader() throws DeserializationException {
        SapiFixturesEndpoint fixturesEndpoint = XmlMessageReader.readMessageFromResource(
            "test/rest/fixtures.de.xml"
        );
        Assert.assertNotNull(fixturesEndpoint);
    }

    @Test
    public void mockDataProvider() throws DataProviderException {
        TestingDataProvider<SapiFixturesEndpoint> dataProvider = new TestingDataProvider<>(
            "test/rest/fixtures.de.xml"
        );
        SapiFixturesEndpoint data = dataProvider.getData();
        Assert.assertNotNull(data);
    }
}
