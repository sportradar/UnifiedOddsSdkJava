/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.utils;

import com.google.common.base.Preconditions;
import com.sportradar.uf.sportsapi.datamodel.SapiTeam;
import com.sportradar.uf.sportsapi.datamodel.SapiTeamCompetitor;
import com.sportradar.unifiedodds.sdk.caching.ci.ReferenceIdCi;
import com.sportradar.unifiedodds.sdk.entities.markets.Specifier;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import java.security.InvalidParameterException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.hc.client5.http.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An utility class that contains various methods which perform common language tasks
 */
@SuppressWarnings(
    {
        "ConstantName",
        "EmptyCatchBlock",
        "HideUtilityClassConstructor",
        "IllegalCatch",
        "LineLength",
        "MagicNumber",
        "NeedBraces",
        "NestedIfDepth",
        "OneStatementPerLine",
        "ParameterAssignment",
        "StaticVariableName",
        "VisibilityModifier",
    }
)
public final class SdkHelper {

    public static final Logger ExecutionLog = LoggerFactory.getLogger(SdkHelper.class);

    public static int MarketDescriptionMinFetchInterval = 30;
    public static String InVariantMarketListCache = "InVariantMarketListCache";
    public static String VariantMarketSingleCache = "VariantMarketSingleCache";
    public static String VariantMarketListCache = "VariantMarketListCache";

    public static final String ISO_8601_24H_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String ISO_8601_24H_FULL_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX";
    /**
     * The regex pattern to extract error message from failed API requests
     */
    public static final String ApiResponseErrorPattern = "<errors>([a-zA-Z0-9 -_:./'{}]*)</errors>";
    /**
     * The regex pattern to extract response message from failed API requests
     */
    public static final String ApiResponseMessagePattern = "<message>([a-zA-Z0-9 -_:.]*)</message>";

    /**
     * Calculates and returns the missing locales within the provided {@link List}
     *
     * @param have - a {@link List} that contains all the available locales
     * @param want - a {@link List} of locales that are required
     * @return - returns a {@link List} of missing locales
     */
    public static List<Locale> findMissingLocales(Collection<Locale> have, List<Locale> want) {
        Preconditions.checkNotNull(have);
        Preconditions.checkNotNull(want);

        return want.stream().filter(locale -> !have.contains(locale)).collect(Collectors.toList());
    }

    /**
     * Filters out the translated data not needed
     *
     * @param data the data to be filtered
     * @param filterLocales the requested locales
     * @return the filtered map data set
     */
    public static Map<Locale, String> filterLocales(Map<Locale, String> data, List<Locale> filterLocales) {
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(filterLocales);

        return data
            .entrySet()
            .stream()
            .filter(entry -> filterLocales.contains(entry.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Get the abbreviation from the input string
     * @param input input text to be abbreviated
     * @param length of the abbreviation
     * @return the abbreviated input string in upper case (it does not return null)
     */
    public static String getAbbreviationFromName(String input, int length) {
        if (length < 1) {
            length = Integer.MAX_VALUE;
        }
        if (input == null || input.isEmpty()) {
            return "";
        }
        return input.length() > length ? input.substring(0, length).toUpperCase() : input.toUpperCase();
    }

    /**
     * Get competitor reference from a list of competitors
     * Note: reference must be checked and updated, since it is not sure that references on summary are the same as on fixture
     * @param competitors competitor id with which is associated reference
     * @param currentCompetitorsReferences competitor references
     * @return map of references per competitor id
     */
    public static Map<Urn, ReferenceIdCi> parseCompetitorsReferences(
        List<SapiTeam> competitors,
        Map<Urn, ReferenceIdCi> currentCompetitorsReferences
    ) {
        if (competitors == null) {
            return currentCompetitorsReferences;
        }

        Map<Urn, ReferenceIdCi> competitorsReferences = currentCompetitorsReferences == null
            ? new HashMap<>()
            : new HashMap<>(currentCompetitorsReferences);
        for (SapiTeam competitor : competitors) {
            if (competitor.getReferenceIds() != null) {
                ReferenceIdCi newReferenceId = new ReferenceIdCi(
                    competitor
                        .getReferenceIds()
                        .getReferenceId()
                        .stream()
                        .filter(r -> r.getName() != null && r.getValue() != null)
                        .collect(
                            HashMap::new,
                            (map, i) -> map.put(i.getName(), i.getValue()),
                            HashMap::putAll
                        )
                );

                Urn competitorId = Urn.parse(competitor.getId());
                if (competitorsReferences.containsKey(competitorId)) {
                    ReferenceIdCi oldReference = competitorsReferences.get(competitorId);
                    oldReference.merge(newReferenceId.getReferenceIds());
                    competitorsReferences.put(competitorId, newReferenceId);
                } else {
                    competitorsReferences.put(competitorId, newReferenceId);
                }
            }
        }

        return competitorsReferences;
    }

    /**
     * Get competitor reference from a list of competitors
     * Note: reference must be checked and updated, since it is not sure that references on summary are the same as on fixture
     * @param competitors competitor id with which is associated reference
     * @param currentCompetitorsReferences competitor references
     * @return map of references per competitor id
     */
    public static Map<Urn, ReferenceIdCi> parseTeamCompetitorsReferences(
        List<SapiTeamCompetitor> competitors,
        Map<Urn, ReferenceIdCi> currentCompetitorsReferences
    ) {
        if (competitors == null) {
            return currentCompetitorsReferences;
        }

        Map<Urn, ReferenceIdCi> competitorsReferences = currentCompetitorsReferences == null
            ? new HashMap<>()
            : currentCompetitorsReferences;
        for (SapiTeam competitor : competitors) {
            if (competitor.getReferenceIds() != null) {
                ReferenceIdCi newReferenceId = new ReferenceIdCi(
                    competitor
                        .getReferenceIds()
                        .getReferenceId()
                        .stream()
                        .filter(r -> r.getName() != null && r.getValue() != null)
                        .collect(
                            HashMap::new,
                            (map, i) -> map.put(i.getName(), i.getValue()),
                            HashMap::putAll
                        )
                );

                Urn competitorId = Urn.parse(competitor.getId());
                if (competitorsReferences.containsKey(competitorId)) {
                    ReferenceIdCi oldReference = competitorsReferences.get(competitorId);
                    oldReference.merge(newReferenceId.getReferenceIds());
                    competitorsReferences.put(competitorId, newReferenceId);
                } else {
                    competitorsReferences.put(competitorId, newReferenceId);
                }
            }
        }

        return competitorsReferences;
    }

    public static Locale checkConfigurationLocales(Locale defaultLocale, Set<Locale> supportedLocales) {
        if (defaultLocale == null && !supportedLocales.isEmpty()) {
            defaultLocale = supportedLocales.iterator().next();
        }
        if (
            !supportedLocales.contains(defaultLocale) || supportedLocales.iterator().next() != defaultLocale
        ) {
            List<Locale> locales = new ArrayList<>();
            locales.add(defaultLocale);
            locales.addAll(supportedLocales);
            supportedLocales.clear();
            supportedLocales.addAll(locales);
        }

        if (defaultLocale == null) {
            throw new InvalidParameterException("Missing default locale");
        }
        if (supportedLocales.isEmpty()) {
            throw new InvalidParameterException("Missing supported locales");
        }
        return defaultLocale;
    }

    public static Date combineDateAndTime(Date date, Date time) {
        if (date == null) {
            return time;
        }
        if (time == null) {
            return date;
        }
        return new Date(date.getTime() + time.getTime());
    }

    public static long getTimeDifferenceInSeconds(Date d1, Date d2) {
        return Math.abs(d1.getTime() - d2.getTime()) / 1000;
    }

    public static Date toDate(XMLGregorianCalendar gregorianCalendar) {
        if (
            gregorianCalendar.getTimezone() == DatatypeConstants.FIELD_UNDEFINED
        ) gregorianCalendar.setTimezone(0);
        return gregorianCalendar.toGregorianCalendar().getTime();
    }

    public static Date toDate(String dateString) throws ParseException {
        if (dateString.isEmpty()) {
            return null;
        }

        try {
            Instant instant = DateUtils.parseStandardDate(dateString);
            if (instant != null) {
                return Date.from(instant);
            }
        } catch (Exception e) {}

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(ISO_8601_24H_FULL_FORMAT);
            return sdf.parse(dateString);
        } catch (Exception e) {}

        SimpleDateFormat sdf = new SimpleDateFormat(ISO_8601_24H_FORMAT);
        return sdf.parse(dateString);
    }

    public static String dateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(ISO_8601_24H_FULL_FORMAT);
        return sdf.format(date);
    }

    public static String doubleToStringWithSign(double value) {
        if (value > 0) return "+" + value; else if (value < 0) return String.valueOf(value); else return "0";
    }

    public static boolean isDataNotFound(Throwable e) {
        return isDataNotFound(e, 0);
    }

    public static boolean isDataNotFound(Throwable e, int counter) {
        if (e != null) {
            if (e instanceof CommunicationException) {
                if (e.getMessage().contains("404")) {
                    return true;
                }
            }
            if (e.getCause() != null) {
                if (counter < 10) {
                    return isDataNotFound(e.getCause(), counter + 1);
                }
            }
        }
        return false;
    }

    public static String stringSetToString(Set<String> set) {
        if (set == null || set.isEmpty()) {
            return null;
        }
        String result = "";
        for (String key : set) {
            result += "," + key;
        }
        if (result.length() > 1) {
            result = result.substring(1);
        }
        return result;
    }

    public static String integerSetToString(Set<Integer> set) {
        if (set == null || set.isEmpty()) {
            return null;
        }
        String result = "";
        for (int key : set) {
            result += "," + key;
        }
        if (result.length() > 1) {
            result = result.substring(1);
        }
        return result;
    }

    public static String dictionaryToString(Map<String, String> dict) {
        if (dict == null || dict.isEmpty()) {
            return null;
        }
        String result = null;
        for (String key : dict.keySet()) {
            result += "," + key + "=" + dict.get(key);
        }
        return result;
    }

    public static String specifierListToString(List<Specifier> specifiers) {
        if (specifiers == null || specifiers.isEmpty()) {
            return null;
        }
        String result = specifiers
            .stream()
            .map(n -> String.valueOf(n))
            .collect(Collectors.joining("|", "{", "}"));
        return result;
    }

    public static String specifierKeyListToString(List<Specifier> specifiers) {
        if (specifiers == null || specifiers.isEmpty()) {
            return null;
        }
        String result = specifiers.stream().map(n -> n.getName()).collect(Collectors.joining("|", "{", "}"));
        return result;
    }

    public static String localeListToString(List<Locale> locales) {
        if (locales == null || locales.isEmpty()) {
            return null;
        }
        String result = locales.stream().map(Locale::getLanguage).collect(Collectors.joining(", "));
        return result;
    }

    public static boolean checkCauseReason(Throwable cause, String message) {
        if (cause == null || message == null) {
            return false;
        }

        int i = 10;
        while (i > 0) {
            i--;
            if (cause.getMessage().contains(message)) {
                return true;
            }
            if (cause.getCause() != null) {
                cause = cause.getCause();
                continue;
            }
            break;
        }
        return false;
    }

    /**
     * Check is string is null or empty
     * @param input to be checked
     * @return true if null or empty, otherwise false
     */
    public static boolean stringIsNullOrEmpty(String input) {
        return input == null || input.isEmpty();
    }

    /**
     * Obfuscate the input string
     * @param input text to be obfuscated
     * @return obfuscated string with only first and last 3 letters
     */
    public static String obfuscate(String input) {
        if (!stringIsNullOrEmpty(input) && input.length() > 3) {
            return String.format("%s***%s", input.substring(0, 3), input.substring(input.length() - 3));
        }
        return input;
    }

    /**
     * Get the specified size of UUID
     * @param size the substring size
     * @return the substring of uuid
     */
    public static String getUuid(int size) {
        if (size < 3) {
            size = 3;
        } else if (size > 20) {
            size = 20;
        }
        String strUuid = UUID.randomUUID().toString();
        strUuid = strUuid.replace("-", "");
        return strUuid.substring(0, size);
    }

    public static String extractHttpResponseMessage(String responseContent) {
        if (responseContent == null || responseContent.isEmpty()) {
            return "";
        }
        Matcher messageMatcher = Pattern.compile(ApiResponseMessagePattern).matcher(responseContent);
        Matcher errorMatcher = Pattern.compile(ApiResponseErrorPattern).matcher(responseContent);
        if (errorMatcher.find()) {
            return messageMatcher.find()
                ? String.format("%s (detail: %s)", errorMatcher.group(), messageMatcher.group())
                : errorMatcher.group();
        }
        return responseContent;
    }
}
