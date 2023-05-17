package com.sportradar.unifiedodds.sdk.di;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.OperationManager;
import com.sportradar.unifiedodds.sdk.SDKGlobalEventsListener;
import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import java.time.Duration;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings({ "MagicNumber" })
public class GeneralModuleTest {

    private GeneralModule module;

    @Mock
    private SDKGlobalEventsListener sdkListener;

    @Mock
    private SDKInternalConfiguration configuration;

    @Mock
    private HttpClientFactory httpClientFactory;

    @Mock
    private CloseableHttpClient httpClient;

    @Before
    public void setUp() throws Exception {
        module = new GeneralModule(sdkListener, configuration, httpClientFactory);

        when(httpClientFactory.create(anyInt(), anyInt(), anyInt())).thenReturn(httpClient);
    }

    @Test
    public void provideHttpClient() {
        when(configuration.getHttpClientTimeout()).thenReturn(3);
        when(configuration.getHttpClientMaxConnTotal()).thenReturn(10);
        when(configuration.getHttpClientMaxConnPerRoute()).thenReturn(20);

        CloseableHttpClient client = module.provideHttpClient();
        assertNotNull(client);

        verify(httpClientFactory).create(3000, 10, 20);
    }

    @Test
    public void provideCriticalHttpClient() {
        Duration timeout = Duration.ofSeconds(3);
        when(configuration.getHttpClientMaxConnTotal()).thenReturn(10);
        when(configuration.getHttpClientMaxConnPerRoute()).thenReturn(20);

        try (MockedStatic<OperationManager> mockedStatic = Mockito.mockStatic(OperationManager.class)) {
            mockedStatic.when(OperationManager::getFastHttpClientTimeout).thenReturn(timeout);

            CloseableHttpClient client = module.provideCriticalHttpClient();
            assertNotNull(client);
        }

        verify(httpClientFactory).create(3000, 10, 20);
    }

    @Test
    public void provideRecoveryHttpClient() {
        when(configuration.getRecoveryHttpClientTimeout()).thenReturn(3);
        when(configuration.getRecoveryHttpClientMaxConnTotal()).thenReturn(10);
        when(configuration.getRecoveryHttpClientMaxConnPerRoute()).thenReturn(20);

        CloseableHttpClient client = module.provideRecoveryHttpClient();
        assertNotNull(client);

        verify(httpClientFactory).create(3000, 10, 20);
    }
}
