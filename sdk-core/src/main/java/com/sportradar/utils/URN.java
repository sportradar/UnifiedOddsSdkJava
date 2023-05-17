/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.utils;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableMap;
import com.sportradar.unifiedodds.sdk.entities.ResourceTypeGroup;
import com.sportradar.unifiedodds.sdk.exceptions.UnsupportedUrnFormatException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a Uniform Resource Name
 */
@SuppressWarnings(
    {
        "AbbreviationAsWordInName",
        "ConstantName",
        "DeclarationOrder",
        "EqualsAvoidNull",
        "IllegalCatch",
        "LineLength",
        "MagicNumber",
        "NeedBraces",
    }
)
public class URN {

    /**
     * A {@link Logger} instance used to log data
     */
    private static final Logger logger = LoggerFactory.getLogger(URN.class);

    /**
     * The name of the regex group used to store the prefix
     */
    private static final String PREFIX_GROUP_NAME = "prefix";

    /**
     * The name of the regex group used to store the type
     */
    private static final String TYPE_GROUP_NAME = "type";

    /**
     * The name of the regex group used to store the id
     */
    private static final String ID_GROUP_NAME = "id";

    /**
     * A regex string pattern used for parsing of URN strings
     */
    private static final String REGEX_STRING =
        "\\A(?<" +
        PREFIX_GROUP_NAME +
        ">[a-zA-Z]+):(?<" +
        TYPE_GROUP_NAME +
        ">[a-zA-Z_2]+):(?<" +
        ID_GROUP_NAME +
        ">[-\\d]+)\\z";

    /**
     * A regex compiled pattern that is used to parse input string ids
     */
    private static final Pattern REGEX_PATTERN = Pattern.compile(REGEX_STRING);

    /**
     * toString value used to lower mem usage
     */
    private String toStringCache;

    /**
     * Defines supported resource types
     */
    private static final ImmutableMap<String, ResourceTypeGroup> TYPES = new ImmutableMap.Builder<String, ResourceTypeGroup>()
        .put("sport_event", ResourceTypeGroup.MATCH)
        .put("race_event", ResourceTypeGroup.RACE)
        .put("season", ResourceTypeGroup.TOURNAMENT)
        .put("tournament", ResourceTypeGroup.TOURNAMENT)
        .put("race_tournament", ResourceTypeGroup.TOURNAMENT)
        .put("simple_tournament", ResourceTypeGroup.TOURNAMENT)
        .put("h2h_tournament", ResourceTypeGroup.TOURNAMENT)
        .put("outright", ResourceTypeGroup.TOURNAMENT)
        .put("sport", ResourceTypeGroup.OTHER)
        .put("category", ResourceTypeGroup.OTHER)
        .put("match", ResourceTypeGroup.MATCH)
        .put("team", ResourceTypeGroup.OTHER)
        .put("competitor", ResourceTypeGroup.OTHER)
        .put("simpleteam", ResourceTypeGroup.OTHER)
        .put("simple_team", ResourceTypeGroup.OTHER)
        .put("venue", ResourceTypeGroup.OTHER)
        .put("player", ResourceTypeGroup.OTHER)
        .put("referee", ResourceTypeGroup.OTHER)
        .put("market", ResourceTypeGroup.OTHER)
        .put("group", ResourceTypeGroup.OTHER)
        .build();

    /**
     * Prefix of the current instance
     */
    private final String prefix;

    /**
     * Type of the current instance
     */
    private final String type;

    /**
     * Id of the current instance
     */
    private final long id;

    /**
     * The corresponding {@link ResourceTypeGroup} of the current instance
     */
    private ResourceTypeGroup group;

    /**
     * Initializes a new instance of the {@link URN} class
     *
     * @param prefix - prefix of the URN
     * @param type   - type of the resource associated with the URN
     * @param id     - numerical identifier of the resource associated with the URN
     */
    public URN(String prefix, String type, long id) {
        checkNotNull(prefix, "prefix can not be null");
        checkNotNull(type, "type can not be null");
        checkArgument(id != 0, "id must not be 0");

        this.prefix = prefix;
        this.type = type;
        this.id = id;
        this.group = TYPES.getOrDefault(type, ResourceTypeGroup.OTHER);
    }

    /**
     * Constructs a {@link URN} instance by parsing the provided {@link String}
     *
     * @param urnString - {@link String} representation of the URN
     * @return the {@link URN} constructed by parsing the provided string representation
     */
    public static URN parse(String urnString) {
        try {
            checkNotNull(urnString, "urnString can not be null");

            Matcher matcher = REGEX_PATTERN.matcher(urnString);

            checkArgument(
                !matcher.find() || matcher.groupCount() != 4,
                "Value " + urnString + " is not a valid string representation of the URN"
            );

            return new URN(
                matcher.group(PREFIX_GROUP_NAME),
                matcher.group(TYPE_GROUP_NAME),
                Long.valueOf(matcher.group(ID_GROUP_NAME))
            );
        } catch (Exception e) {
            throw new UnsupportedUrnFormatException("URN could not be parsed [" + urnString + "] ", e);
        }
    }

    /**
     * Returns the prefix of the current instance
     *
     * @return the prefix of the current instance
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Returns the type of the resource associated with the current instance
     *
     * @return the type of the resource associated with the current instance
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the numerical part of the identifier associated with the current instance
     *
     * @return the numerical part of the identifier associated with the current instance
     */
    public long getId() {
        return id;
    }

    /**
     * Returns the {@link ResourceTypeGroup} enum member describing the group of the current instance
     *
     * @return the {@link ResourceTypeGroup} enum member describing the group of the current instance
     */
    public ResourceTypeGroup getGroup() {
        return group;
    }

    /**
     * Determines whether the specified {@link Object} is equal to this instance
     *
     * @param obj - object to compare with the current object
     * @return - true if the specified {@link Object} is equal to this instance; otherwise false
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (obj instanceof String) {
            return obj.equals(toString());
        }

        if (obj instanceof URN) {
            URN other = (URN) obj;

            return prefix.equals(other.getPrefix()) && type.equals(other.getType()) && id == other.getId();
        }

        return false;
    }

    /**
     * Returns a {@link String} that represents this instance
     */
    @Override
    public String toString() {
        if (toStringCache == null) toStringCache = prefix + ":" + type + ":" + id;
        return toStringCache;
    }

    /**
     * Returns a hash code for this instance
     */
    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    public boolean isSimpleTeam() {
        if (type != null && (type.equalsIgnoreCase("simpleteam") || type.equalsIgnoreCase("simple_team"))) {
            return true;
        }
        return false;
    }

    public static boolean isSimpleTeam(String urn) {
        if (urn != null && (urn.contains("simpleteam") || urn.contains("simple_team"))) {
            return true;
        }
        return false;
    }
}
