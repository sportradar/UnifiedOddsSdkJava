/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.apireaders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.sportradar.unifiedodds.sdk.impl.Deserializer;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class ResponseDataTest {

    private static final int HTTP_OK = 200;
    private static final int HTTP_ACCEPTED = 202;

    private Object[] successfulHttpCodes() {
        return new Object[][] { { HTTP_OK }, { HTTP_ACCEPTED } };
    }

    @Test
    @Parameters(method = "successfulHttpCodes")
    public void treatsOkAndAcceptedResponseCodesAsSuccessful(int successfulHttpCode) {
        HttpHelper.ResponseData response = new HttpHelper.ResponseData(
            successfulHttpCode,
            null,
            mock(MessageAndActionExtractor.class)
        );

        assertThat(response.isSuccessful()).isTrue();
    }
}
