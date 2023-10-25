/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.shared;

public class SportsApiXmlResponseProvider {

    private final String xmlDeclarationString =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n";
    private final String sportResponse =
        xmlDeclarationString +
        "  <sports>\n" +
        "    <sport id=\"sr:sport:143\" name=\"7BallRun\"/>\n" +
        "  </sports>";

    private final String matchNotFoundResponse =
        xmlDeclarationString +
        "  <response response_code=\"NOT_FOUND\" generated_at=\"2023-05-02T08:37:37Z\">\n" +
        "    <message>Content not found: Match ID 1 not found. Bookmaker ID 1</message>" +
        "  </response>";

    private final String errorResponseWithEmptyMessage =
        xmlDeclarationString +
        "  <response response_code=\"NOT_FOUND\" generated_at=\"2023-05-02T08:37:37Z\">\n" +
        "    <message></message>\n" +
        "  </response>";

    private final String contentNotFoundResponse =
        xmlDeclarationString +
        "<response>\n" +
        "    <message>Content not found: null</message>\n" +
        "</response>";

    private final String responseContainingSuccessMessage =
        xmlDeclarationString + "<response>\n" + "    <message>success</message>\n" + "</response>";

    public String createSportsApiSportResponse() {
        return sportResponse;
    }

    public String createMatchNotFoundResponse() {
        return matchNotFoundResponse;
    }

    public String createErrorResponseWithEmptyMessage() {
        return errorResponseWithEmptyMessage;
    }

    public String createNotFoundResponse() {
        return contentNotFoundResponse;
    }

    public String createResponseContainingSuccessMessage() {
        return responseContainingSuccessMessage;
    }

    public String any() {
        return "<xml />";
    }
}
