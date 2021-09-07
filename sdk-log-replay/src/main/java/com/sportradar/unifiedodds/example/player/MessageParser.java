/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example.player;

import com.google.common.base.Preconditions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created on 05/01/2018.
 * // TODO @eti: Javadoc
 */
class MessageParser {
    private static final Pattern REGEX_PATTERN = composePattern();
    private final DateFormat logDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss,SSS", Locale.ENGLISH);

    public ParsedLine parseLine(String line) {
        Preconditions.checkNotNull(line);

        Matcher m = REGEX_PATTERN.matcher(line);
        if (!m.find()) {
            throw new IllegalArgumentException("Regex matcher could not match -> " + line);
        }

        String timestamp = m.group(1);
        String messageInterest = m.group(2);
        String routingKey = m.group(3);
        String messagePayload = m.group(4);

        Date logEntryTimestamp;
        try {
            logEntryTimestamp = logDateFormat.parse(timestamp);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Log entry timestamp is malformed -> " + timestamp);
        }

        return new ParsedLine(logEntryTimestamp, routingKey, messagePayload);
    }

    private static Pattern composePattern() {
        String re1="([21]\\d{3}[-/](?:(?:0[1-9])|(?:1[0-2]))[-/](?:(?:0[1-9])|(?:[1-2][0-9])|(?:3[0-1]))(?:T|\\s)(?:(?:[0-1][0-9])|(?:2[0-3])):(?:[0-5][0-9]):(?:[0-5][0-9]),(?:\\d+))";	// log timestamp
        String re2=".*?";               // space between timestamp and logger level
        String re3="(?:[a-z][a-z]+)";   // logger level
        String re4=".*?";               // anything
        String re5="(?:[a-z][a-z]+)";   // thread info
        String re6=".*?";               // anything
        String re7="(?:[a-z][a-z]+)";   // thread info
        String re8=".*?";               // anything
        String re9="(?:UFSession-)";    // UFSession
        String re11="((?:[a-z][a-z]+))";// session message interest
        String re12=".*?";              // anything
        String re13="(?:<~> )";         // separator
        String re14="(.*)";             // routing key
        String re15="(?: <~>)";         // separator
        String re16=".*?";              // anything
        String re17="(?: )";            // space
        String re18="(.*)";             // message xml payload

        return Pattern.compile(re1+re2+re3+re4+re5+re6+re7+re8+re9+re11+re12+re13+re14+re15+re16+re17+re18,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    }
}
