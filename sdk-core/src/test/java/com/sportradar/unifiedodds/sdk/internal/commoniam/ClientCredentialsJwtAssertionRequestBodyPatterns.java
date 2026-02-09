/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.commoniam;

import static java.util.regex.Pattern.DOTALL;

import java.util.regex.Pattern;

@SuppressWarnings("UnnecessaryParentheses")
public class ClientCredentialsJwtAssertionRequestBodyPatterns {

    private static final String JWT_REGEX = "[a-zA-Z0-9_-]+\\.[a-zA-Z0-9_-]+\\.[a-zA-Z0-9_-]+";

    public static boolean matchesRequestForRestApi(String body) {
        return Pattern.compile(jwtClientAssertionForApiPattern(), DOTALL).matcher(body).matches();
    }

    public static boolean matchesRequestForRabbit(String body) {
        return Pattern.compile(jwtClientAssertionForRabbitPattern(), DOTALL).matcher(body).matches();
    }

    public static String jwtClientAssertionForApiPattern() {
        return (
            "^" +
            "(?=.*grant_type=client_credentials)" +
            "(?=.*client_assertion_type=urn:ietf:params:oauth:client-assertion-type:jwt-bearer)" +
            "(?=.*client_assertion=" +
            JWT_REGEX +
            ")" +
            "(?=.*audience=UF-RestAPI)" +
            "[^&]*(&[^&]*){3}$"
        );
    }

    public static String jwtClientAssertionForRabbitPattern() {
        return (
            "^" +
            "(?=.*grant_type=client_credentials)" +
            "(?=.*client_assertion_type=urn:ietf:params:oauth:client-assertion-type:jwt-bearer)" +
            "(?=.*client_assertion=" +
            JWT_REGEX +
            ")" +
            "(?=.*audience=UF-RabbitMQ)" +
            "[^&]*(&[^&]*){3}$"
        );
    }
}
