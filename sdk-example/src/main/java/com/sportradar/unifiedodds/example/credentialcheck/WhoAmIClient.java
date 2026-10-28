/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example.credentialcheck;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

final class WhoAmIClient {

    enum AuthMode {
        ACCESS_TOKEN,
        BEARER,
    }

    private static final Pattern BOOKMAKER_ID_PATTERN = Pattern.compile("bookmaker_id=\"(\\d+)\"");
    private static final Pattern VIRTUAL_HOST_PATTERN = Pattern.compile("virtual_host=\"([^\"]*)\"");
    private static final Pattern RESPONSE_CODE_PATTERN = Pattern.compile("response_code=\"([^\"]*)\"");

    private WhoAmIClient() {}

    static WhoAmIResult check(String token, CredentialAuthEnvironment apiEnvironment, AuthMode authMode) {
        String apiHost = apiEnvironment == CredentialAuthEnvironment.Integration
            ? CredentialCheckEndpoints.INTEGRATION_API_HOST
            : CredentialCheckEndpoints.PRODUCTION_API_HOST;
        String whoAmIUrl =
            CredentialCheckEndpoints.getApiBaseUrl(apiEnvironment) + CredentialCheckEndpoints.WHO_AM_I_PATH;

        try {
            Map<String, String> headers = new HashMap<>();
            if (authMode == AuthMode.BEARER) {
                headers.put("Authorization", "Bearer " + token);
            } else {
                headers.put("x-access-token", token);
            }

            HttpSupport.HttpResponse response = HttpSupport.get(whoAmIUrl, headers);
            int statusCode = response.getStatusCode();
            String responseBody = response.getBody();
            ParsedWhoAmI parsed = parseWhoAmI(responseBody);

            if (statusCode >= 200 && statusCode < 300 && parsed != null && parsed.bookmakerId != null) {
                return new WhoAmIResult(
                    apiEnvironment,
                    apiHost,
                    true,
                    statusCode,
                    parsed.bookmakerId,
                    parsed.virtualHost,
                    parsed.responseCode,
                    null
                );
            }

            return new WhoAmIResult(
                apiEnvironment,
                apiHost,
                false,
                statusCode,
                parsed == null ? null : parsed.bookmakerId,
                parsed == null ? null : parsed.virtualHost,
                parsed == null ? null : parsed.responseCode,
                buildFailureDetail(statusCode, parsed, responseBody)
            );
        } catch (Exception ex) {
            return new WhoAmIResult(
                apiEnvironment,
                apiHost,
                false,
                null,
                null,
                null,
                null,
                "Request to " + whoAmIUrl + " failed: " + ex.getMessage()
            );
        }
    }

    private static String buildFailureDetail(int statusCode, ParsedWhoAmI parsed, String responseBody) {
        if (parsed != null && parsed.responseCode != null && !"OK".equalsIgnoreCase(parsed.responseCode)) {
            return "HTTP " + statusCode + ", response_code=" + parsed.responseCode;
        }

        if (parsed != null && parsed.message != null && !parsed.message.isEmpty()) {
            return "HTTP " + statusCode + ": " + parsed.message;
        }

        if (responseBody != null && !responseBody.trim().isEmpty()) {
            String trimmed = responseBody.trim();
            return trimmed.length() > 300
                ? "HTTP " + statusCode + ": " + trimmed.substring(0, 300) + "..."
                : "HTTP " + statusCode + ": " + trimmed;
        }

        return "HTTP " + statusCode + " with empty response body.";
    }

    private static ParsedWhoAmI parseWhoAmI(String responseBody) {
        if (responseBody == null || responseBody.trim().isEmpty()) {
            return null;
        }

        try {
            Document document = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .parse(
                    new java.io.ByteArrayInputStream(
                        responseBody.getBytes(java.nio.charset.StandardCharsets.UTF_8)
                    )
                );
            Element root = document.getDocumentElement();
            if (root == null) {
                return parseWhoAmIWithRegex(responseBody);
            }

            ParsedWhoAmI parsed = new ParsedWhoAmI();
            parsed.bookmakerId = parseInteger(root.getAttribute("bookmaker_id"));
            parsed.virtualHost = emptyToNull(root.getAttribute("virtual_host"));
            parsed.responseCode = emptyToNull(root.getAttribute("response_code"));
            parsed.message = emptyToNull(root.getTextContent());
            return parsed;
        } catch (Exception ex) {
            return parseWhoAmIWithRegex(responseBody);
        }
    }

    private static ParsedWhoAmI parseWhoAmIWithRegex(String responseBody) {
        ParsedWhoAmI parsed = new ParsedWhoAmI();
        parsed.bookmakerId = parseRegexGroup(BOOKMAKER_ID_PATTERN, responseBody);
        parsed.virtualHost = parseRegexString(VIRTUAL_HOST_PATTERN, responseBody);
        parsed.responseCode = parseRegexString(RESPONSE_CODE_PATTERN, responseBody);
        return parsed;
    }

    private static Integer parseRegexGroup(Pattern pattern, String value) {
        Matcher matcher = pattern.matcher(value);
        if (!matcher.find()) {
            return null;
        }
        return Integer.valueOf(matcher.group(1));
    }

    private static String parseRegexString(Pattern pattern, String value) {
        Matcher matcher = pattern.matcher(value);
        if (!matcher.find()) {
            return null;
        }
        return matcher.group(1);
    }

    private static Integer parseInteger(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return Integer.valueOf(value.trim());
    }

    private static String emptyToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    private static final class ParsedWhoAmI {

        private Integer bookmakerId;
        private String virtualHost;
        private String responseCode;
        private String message;
    }
}
