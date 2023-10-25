/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.sportradar.uf.datamodel.*;
import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.caching.NamedValuesProvider;
import com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptionProvider;
import com.sportradar.unifiedodds.sdk.entities.markets.MarketDescription;
import com.sportradar.unifiedodds.sdk.entities.markets.Specifier;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.oddsentities.UnmarshalledMessage;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines methods used to validate received messages
 */
@SuppressWarnings(
    {
        "ClassFanOutComplexity",
        "ConstantName",
        "CyclomaticComplexity",
        "LineLength",
        "MethodLength",
        "MultipleStringLiterals",
        "NPathComplexity",
        "ReturnCount",
    }
)
public class FeedMessageValidatorImpl implements FeedMessageValidator {

    private static final Logger logger = LoggerFactory.getLogger(FeedMessageValidatorImpl.class);

    private final MarketDescriptionProvider marketDescriptionProvider;
    private final List<Locale> defaultLocales;
    private final NamedValuesProvider namedValuesProvider;

    @Inject
    FeedMessageValidatorImpl(
        MarketDescriptionProvider marketDescriptionProvider,
        SdkInternalConfiguration configuration,
        NamedValuesProvider namedValuesProvider
    ) {
        Preconditions.checkNotNull(marketDescriptionProvider);
        Preconditions.checkNotNull(configuration);
        Preconditions.checkNotNull(namedValuesProvider);

        this.marketDescriptionProvider = marketDescriptionProvider;
        this.defaultLocales = Collections.singletonList(configuration.getDefaultLocale());
        this.namedValuesProvider = namedValuesProvider;
    }

    /**
     * Validates the provided {@link UnmarshalledMessage} instance
     *
     * @param message the message instance that should be validated
     * @param rkInfo the associated routing key information
     * @return a {@link ValidationResult} specifying the validation result
     */
    @Override
    public ValidationResult validate(UnmarshalledMessage message, RoutingKeyInfo rkInfo) {
        Preconditions.checkNotNull(message);
        Preconditions.checkNotNull(rkInfo);

        if (message instanceof UfOddsChange) {
            return validateOddsChange((UfOddsChange) message, rkInfo);
        } else if (message instanceof UfBetStop) {
            return validateBetStop((UfBetStop) message, rkInfo);
        } else if (message instanceof UfBetSettlement) {
            return validateBetSettlement((UfBetSettlement) message, rkInfo);
        } else if (message instanceof UfBetCancel) {
            return validateBetCancel((UfBetCancel) message, rkInfo);
        } else if (message instanceof UfSnapshotComplete) {
            return validateSnapshotComplete((UfSnapshotComplete) message);
        } else if (message instanceof UfAlive) {
            return validateAlive((UfAlive) message);
        } else if (message instanceof UfFixtureChange) {
            return validateFixtureChange((UfFixtureChange) message, rkInfo);
        } else if (message instanceof UfRollbackBetSettlement) {
            return validateRollbackBetSettlement((UfRollbackBetSettlement) message, rkInfo);
        } else if (message instanceof UfRollbackBetCancel) {
            return validateRollbackBetCancel((UfRollbackBetCancel) message, rkInfo);
        }

        throw new IllegalArgumentException(
            "Validation of " + message.getClass().getName() + " message is not supported."
        );
    }

    /**
     * Validates the provided {@link UfOddsChange} message
     *
     * @param message the message object to validate
     * @param rkInfo the associated routing key information
     * @return the validation result
     */
    private ValidationResult validateOddsChange(UfOddsChange message, RoutingKeyInfo rkInfo) {
        Preconditions.checkNotNull(message);
        Preconditions.checkNotNull(rkInfo);

        if (!validateEventRoutingKeyInfo(rkInfo)) {
            logFailure(message, "routingKey", rkInfo);
            return ValidationResult.Failure;
        }

        ValidationResult result = ValidationResult.Success;

        if (message.getOdds() == null) {
            return result;
        }

        if (
            message.getOdds().getBetstopReason() != null &&
            !namedValuesProvider.getBetStopReasons().isValueDefined(message.getOdds().getBetstopReason())
        ) {
            logWarning(message, "betstop_reason", message.getOdds().getBetstopReason());
            result = ValidationResult.ProblemsDetected;
        }

        if (
            message.getOdds().getBettingStatus() != null &&
            !namedValuesProvider.getBettingStatuses().isValueDefined(message.getOdds().getBettingStatus())
        ) {
            logWarning(message, "betting_status", message.getOdds().getBettingStatus());
            result = ValidationResult.ProblemsDetected;
        }

        if (message.getOdds().getMarket() == null || message.getOdds().getMarket().isEmpty()) {
            return result;
        }

        for (UfOddsChangeMarket market : message.getOdds().getMarket()) {
            if (!checkSpecifiers(market.getId(), message.getProduct(), market.getSpecifiers())) {
                result = ValidationResult.ProblemsDetected;
            }

            if (market.getOutcome() == null || market.getOutcome().isEmpty()) {
                continue;
            }

            for (UfOddsChangeMarket.UfOutcome outcome : market.getOutcome()) {
                if (
                    outcome.getActive() != null &&
                    outcome.getActive().value() != 1 &&
                    outcome.getActive().value() != 0
                ) {
                    logWarning(
                        message,
                        String.format("Markets[%s].outcomes[%s].active", market.getId(), outcome.getId()),
                        outcome.getActive()
                    );
                    result = ValidationResult.ProblemsDetected;
                }
            }
        }
        return result;
    }

    /**
     * Validates the provided {@link UfBetStop} message
     *
     * @param message the message object to validate
     * @param rkInfo the associated routing key information
     * @return the validation result
     */
    private ValidationResult validateBetStop(UfBetStop message, RoutingKeyInfo rkInfo) {
        Preconditions.checkNotNull(message);
        Preconditions.checkNotNull(rkInfo);

        if (!validateEventRoutingKeyInfo(rkInfo)) {
            logFailure(message, "routingKey", rkInfo);
            return ValidationResult.Failure;
        }

        if (Strings.isNullOrEmpty(message.getGroups())) {
            logFailure(message, "groups", message.getGroups());
            return ValidationResult.Failure;
        }

        return ValidationResult.Success;
    }

    /**
     * Validates the provided {@link UfBetSettlement} message
     *
     * @param message the message object to validate
     * @param rkInfo the associated routing key information
     * @return the validation result
     */
    private ValidationResult validateBetSettlement(UfBetSettlement message, RoutingKeyInfo rkInfo) {
        Preconditions.checkNotNull(message);
        Preconditions.checkNotNull(rkInfo);

        if (!validateEventRoutingKeyInfo(rkInfo)) {
            logFailure(message, "routingKey", rkInfo);
            return ValidationResult.Failure;
        }

        if (
            message.getOutcomes() == null ||
            message.getOutcomes().getMarket() == null ||
            message.getOutcomes().getMarket().isEmpty()
        ) {
            return ValidationResult.Success;
        }

        ValidationResult result = ValidationResult.Success;
        for (UfBetSettlementMarket ufBetSettlementMarket : message.getOutcomes().getMarket()) {
            if (
                !checkSpecifiers(
                    ufBetSettlementMarket.getId(),
                    message.getProduct(),
                    ufBetSettlementMarket.getSpecifiers()
                )
            ) {
                result = ValidationResult.ProblemsDetected;
            }
        }

        return result;
    }

    /**
     * Validates the provided {@link UfBetCancel} message
     *
     * @param message the message object to validate
     * @param rkInfo the associated routing key information
     * @return the validation result
     */
    private ValidationResult validateBetCancel(UfBetCancel message, RoutingKeyInfo rkInfo) {
        Preconditions.checkNotNull(message);
        Preconditions.checkNotNull(rkInfo);

        if (!validateEventRoutingKeyInfo(rkInfo)) {
            logFailure(message, "routingKey", rkInfo);
            return ValidationResult.Failure;
        }

        return validateBasicMarkets(message.getMarket(), message.getProduct());
    }

    /**
     * Validates the provided {@link UfRollbackBetSettlement} message
     *
     * @param message the message object to validate
     * @param rkInfo the associated routing key information
     * @return the validation result
     */
    private ValidationResult validateRollbackBetSettlement(
        UfRollbackBetSettlement message,
        RoutingKeyInfo rkInfo
    ) {
        Preconditions.checkNotNull(message);
        Preconditions.checkNotNull(rkInfo);

        if (!validateEventRoutingKeyInfo(rkInfo)) {
            logFailure(message, "routingKey", rkInfo);
            return ValidationResult.Failure;
        }

        return validateBasicMarkets(message.getMarket(), message.getProduct());
    }

    /**
     * Validates the provided {@link UfRollbackBetCancel} message
     *
     * @param message the message object to validate
     * @param rkInfo the associated routing key information
     * @return the validation result
     */
    private ValidationResult validateRollbackBetCancel(UfRollbackBetCancel message, RoutingKeyInfo rkInfo) {
        Preconditions.checkNotNull(message);
        Preconditions.checkNotNull(rkInfo);

        if (!validateEventRoutingKeyInfo(rkInfo)) {
            logFailure(message, "routingKey", rkInfo);
            return ValidationResult.Failure;
        }

        return validateBasicMarkets(message.getMarket(), message.getProduct());
    }

    /**
     * Validates the provided {@link UfFixtureChange} message
     *
     * @param message the message object to validate
     * @param rkInfo the associated routing key information
     * @return the validation result
     */
    private ValidationResult validateFixtureChange(UfFixtureChange message, RoutingKeyInfo rkInfo) {
        Preconditions.checkNotNull(message);
        Preconditions.checkNotNull(rkInfo);

        if (!validateEventRoutingKeyInfo(rkInfo)) {
            logFailure(message, "routingKey", rkInfo);
            return ValidationResult.Failure;
        }

        return ValidationResult.Success;
    }

    /**
     * Validates the provided {@link UfSnapshotComplete} message
     *
     * @param message the message object to validate
     * @return the validation result
     */
    private ValidationResult validateSnapshotComplete(UfSnapshotComplete message) {
        Preconditions.checkNotNull(message);

        return ValidationResult.Success;
    }

    /**
     * Validates the provided {@link UfAlive} message
     *
     * @param message the message object to validate
     * @return the validation result
     */
    private ValidationResult validateAlive(UfAlive message) {
        Preconditions.checkNotNull(message);

        if (message.getSubscribed() < 0) {
            return ValidationResult.ProblemsDetected;
        }

        return ValidationResult.Success;
    }

    /**
     * Validates a list of basic {@link UfMarket}s
     *
     * @param market the list of markets that needs to be validated
     * @param producerId the associated producer id
     * @return the validation result
     */
    private ValidationResult validateBasicMarkets(List<UfMarket> market, int producerId) {
        if (market == null) {
            return ValidationResult.Failure;
        }

        if (market.isEmpty()) {
            return ValidationResult.Success;
        }

        ValidationResult result = ValidationResult.Success;
        for (UfMarket ufMarket : market) {
            if (!checkSpecifiers(ufMarket.getId(), producerId, ufMarket.getSpecifiers())) {
                result = ValidationResult.ProblemsDetected;
            }
        }

        return result;
    }

    /**
     * Validates the given routing key info so it matches the event messages requirements
     *
     * @param rkInfo the {@link RoutingKeyInfo} which needs to be validated
     * @return <code>true</code> if the validation passed, otherwise <code>false</code>
     */
    private boolean validateEventRoutingKeyInfo(RoutingKeyInfo rkInfo) {
        return rkInfo != null && rkInfo.getSportId() != null && rkInfo.getEventId() != null;
    }

    /**
     * Checks the provided specifiers with the associated market descriptor
     *
     * @param marketId the associated market id
     * @param producerId the associated producer id
     * @param receivedSpecifiers the specifiers received as a part of a message
     * @return <code>true</code> if the specifiers validation passed, otherwise <code>false</code>
     */
    private boolean checkSpecifiers(int marketId, int producerId, String receivedSpecifiers) {
        if (Strings.isNullOrEmpty(receivedSpecifiers)) {
            return true;
        }

        MarketDescription descriptor;
        try {
            descriptor =
                marketDescriptionProvider.getMarketDescription(marketId, null, defaultLocales, false);
        } catch (CacheItemNotFoundException e) {
            logger.info(
                "Market validation failed. Failed to retrieve market descriptor market[id={}]",
                marketId,
                e
            );
            return false;
        }

        if (descriptor.getId() != marketId) {
            logger.info(
                "Market validation failed. Retrieved market descriptor has different market id. RequestedId={}, RetrievedId={}",
                marketId,
                descriptor.getId()
            );
            return false;
        }

        if (descriptor.getSpecifiers() != null && !descriptor.getSpecifiers().isEmpty()) {
            List<String> specifiers;
            try {
                specifiers = extractSpecifierKeys(receivedSpecifiers);
            } catch (ParseException e) {
                logger.info(
                    "Specifiers validation failed. Could not extract specifier keys from: {}, marketId:{}, producerId:{}. Ex:",
                    receivedSpecifiers,
                    marketId,
                    producerId,
                    e
                );
                return false;
            }

            if (descriptor.getSpecifiers().size() != specifiers.size()) {
                String requiredSpecifiers = descriptor
                    .getSpecifiers()
                    .stream()
                    .map(Specifier::getName)
                    .collect(Collectors.joining(","));
                logger.info(
                    "Specifiers validation failed. ProducerId={}, MarketId={}, Required={}, Actual={}",
                    producerId,
                    marketId,
                    requiredSpecifiers,
                    receivedSpecifiers
                );
                return false;
            }

            long matchCounter = specifiers
                .stream()
                .filter(s -> descriptor.getSpecifiers().stream().anyMatch(spec -> spec.getName().equals(s)))
                .count();

            if (matchCounter != descriptor.getSpecifiers().size()) {
                String requiredSpecifiers = descriptor
                    .getSpecifiers()
                    .stream()
                    .map(Specifier::getName)
                    .collect(Collectors.joining(","));
                logger.info(
                    "Specifiers validation for market[id={}] failed. Required={}, Actual={}",
                    marketId,
                    requiredSpecifiers,
                    receivedSpecifiers
                );
                return false;
            }
        }

        return true;
    }

    /**
     * Extracts the specifier keys from the specifiers string received as a part of a message
     *
     * @param receivedSpecifiers the specifiers string received as a part of a message
     * @return a {@link List} of specifiers keys extracted from the provided string
     * @throws ParseException if the specifiers string was malformed
     */
    private List<String> extractSpecifierKeys(String receivedSpecifiers) throws ParseException {
        Preconditions.checkNotNull(receivedSpecifiers);

        String[] split = receivedSpecifiers.split(UnifiedFeedConstants.SPECIFIERS_DELIMITER);

        List<String> result = new ArrayList<>(split.length);
        for (String s : split) {
            String[] exp = s.split("=");
            if (exp.length != 2) {
                throw new ParseException(
                    "The specifiers message String is invalid, expected format: 'k=v|k1=v1|,...', input: " +
                    receivedSpecifiers,
                    receivedSpecifiers.indexOf(s)
                );
            }

            result.add(exp[0]);
        }

        return result;
    }

    /**
     * Logs the validation warning
     *
     * @param msg the message of which the validation failed
     * @param propertyName the property on which the validation failed
     * @param propertyValue the property value of which the validation failed
     */
    private void logWarning(UnmarshalledMessage msg, String propertyName, Object propertyValue) {
        Preconditions.checkNotNull(msg);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(propertyName));

        logger.warn(
            "AMQP Message validation failure: message={}, property={}, propertyValue={} is not expected",
            msg.getClass().getName(),
            propertyName,
            propertyValue
        );
    }

    /**
     * Logs the validation error
     *
     * @param msg the message of which the validation failed
     * @param propertyName the property on which the validation failed
     * @param propertyValue the property value of which the validation failed
     */
    private void logFailure(UnmarshalledMessage msg, String propertyName, Object propertyValue) {
        Preconditions.checkNotNull(msg);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(propertyName));

        logger.error(
            "AMQP Message validation error: message={}, property={}, propertyValue={} is not supported",
            msg.getClass().getName(),
            propertyName,
            propertyValue
        );
    }
}
