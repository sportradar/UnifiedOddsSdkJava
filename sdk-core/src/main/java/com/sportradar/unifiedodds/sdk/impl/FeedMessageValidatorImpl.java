/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.sportradar.uf.datamodel.UFAlive;
import com.sportradar.uf.datamodel.UFBetCancel;
import com.sportradar.uf.datamodel.UFBetSettlement;
import com.sportradar.uf.datamodel.UFBetSettlementMarket;
import com.sportradar.uf.datamodel.UFBetStop;
import com.sportradar.uf.datamodel.UFFixtureChange;
import com.sportradar.uf.datamodel.UFMarket;
import com.sportradar.uf.datamodel.UFOddsChange;
import com.sportradar.uf.datamodel.UFOddsChangeMarket;
import com.sportradar.uf.datamodel.UFRollbackBetCancel;
import com.sportradar.uf.datamodel.UFRollbackBetSettlement;
import com.sportradar.uf.datamodel.UFSnapshotComplete;
import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import com.sportradar.unifiedodds.sdk.caching.NamedValuesProvider;
import com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptionProvider;
import com.sportradar.unifiedodds.sdk.entities.markets.MarketDescription;
import com.sportradar.unifiedodds.sdk.entities.markets.Specifier;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.oddsentities.UnmarshalledMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Defines methods used to validate received messages
 */
public class FeedMessageValidatorImpl implements FeedMessageValidator {
    private static final Logger logger = LoggerFactory.getLogger(FeedMessageValidatorImpl.class);

    private final MarketDescriptionProvider marketDescriptionProvider;
    private final List<Locale> defaultLocales;
    private final NamedValuesProvider namedValuesProvider;

    @Inject
    FeedMessageValidatorImpl(MarketDescriptionProvider marketDescriptionProvider, SDKInternalConfiguration configuration, NamedValuesProvider namedValuesProvider) {
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

        if (message instanceof UFOddsChange) {
            return validateOddsChange((UFOddsChange) message, rkInfo);
        } else if (message instanceof UFBetStop) {
            return validateBetStop((UFBetStop) message, rkInfo);
        } else if (message instanceof UFBetSettlement) {
            return validateBetSettlement((UFBetSettlement) message, rkInfo);
        } else if (message instanceof UFBetCancel) {
            return validateBetCancel((UFBetCancel) message, rkInfo);
        } else if (message instanceof UFSnapshotComplete) {
            return validateSnapshotComplete((UFSnapshotComplete) message);
        } else if (message instanceof UFAlive) {
            return validateAlive((UFAlive) message);
        } else if (message instanceof UFFixtureChange) {
            return validateFixtureChange((UFFixtureChange) message, rkInfo);
        } else if (message instanceof UFRollbackBetSettlement) {
            return validateRollbackBetSettlement((UFRollbackBetSettlement) message, rkInfo);
        } else if (message instanceof UFRollbackBetCancel) {
            return validateRollbackBetCancel((UFRollbackBetCancel) message, rkInfo);
        }

        throw new IllegalArgumentException("Validation of " + message.getClass().getName() + " message is not supported.");
    }

    /**
     * Validates the provided {@link UFOddsChange} message
     *
     * @param message the message object to validate
     * @param rkInfo the associated routing key information
     * @return the validation result
     */
    private ValidationResult validateOddsChange(UFOddsChange message, RoutingKeyInfo rkInfo) {
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

        if (message.getOdds().getBetstopReason() != null && !namedValuesProvider.getBetStopReasons().isValueDefined(message.getOdds().getBetstopReason())) {
            logWarning(message, "betstop_reason", message.getOdds().getBetstopReason());
            result = ValidationResult.ProblemsDetected;
        }

        if (message.getOdds().getBettingStatus() != null && !namedValuesProvider.getBettingStatuses().isValueDefined(message.getOdds().getBettingStatus())) {
            logWarning(message, "betting_status", message.getOdds().getBettingStatus());
            result = ValidationResult.ProblemsDetected;
        }

        if (message.getOdds().getMarket() == null || message.getOdds().getMarket().isEmpty()) {
            return result;
        }

        for (UFOddsChangeMarket market : message.getOdds().getMarket()) {
            if(!checkSpecifiers(market.getId(), message.getProduct(), market.getSpecifiers())) {
                result = ValidationResult.ProblemsDetected;
            }

            if (market.getOutcome() == null || market.getOutcome().isEmpty()) {
                continue;
            }

            for (UFOddsChangeMarket.UFOutcome outcome : market.getOutcome()) {
                if (outcome.getActive() != null && outcome.getActive().value() != 1 && outcome.getActive().value() != 0) {
                    logWarning(message, String.format("Markets[%s].outcomes[%s].active", market.getId(), outcome.getId()), outcome.getActive());
                    result = ValidationResult.ProblemsDetected;
                }
            }
        }
        return result;
    }

    /**
     * Validates the provided {@link UFBetStop} message
     *
     * @param message the message object to validate
     * @param rkInfo the associated routing key information
     * @return the validation result
     */
    private ValidationResult validateBetStop(UFBetStop message, RoutingKeyInfo rkInfo) {
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
     * Validates the provided {@link UFBetSettlement} message
     *
     * @param message the message object to validate
     * @param rkInfo the associated routing key information
     * @return the validation result
     */
    private ValidationResult validateBetSettlement(UFBetSettlement message, RoutingKeyInfo rkInfo) {
        Preconditions.checkNotNull(message);
        Preconditions.checkNotNull(rkInfo);

        if (!validateEventRoutingKeyInfo(rkInfo)) {
            logFailure(message, "routingKey", rkInfo);
            return ValidationResult.Failure;
        }

        if (message.getOutcomes() == null || message.getOutcomes().getMarket() == null || message.getOutcomes().getMarket().isEmpty()) {
            return ValidationResult.Success;
        }

        ValidationResult result = ValidationResult.Success;
        for (UFBetSettlementMarket ufBetSettlementMarket : message.getOutcomes().getMarket()) {
            if (!checkSpecifiers(ufBetSettlementMarket.getId(), message.getProduct(), ufBetSettlementMarket.getSpecifiers())) {
                result = ValidationResult.ProblemsDetected;
            }
        }

        return result;
    }

    /**
     * Validates the provided {@link UFBetCancel} message
     *
     * @param message the message object to validate
     * @param rkInfo the associated routing key information
     * @return the validation result
     */
    private ValidationResult validateBetCancel(UFBetCancel message, RoutingKeyInfo rkInfo) {
        Preconditions.checkNotNull(message);
        Preconditions.checkNotNull(rkInfo);

        if (!validateEventRoutingKeyInfo(rkInfo)) {
            logFailure(message, "routingKey", rkInfo);
            return ValidationResult.Failure;
        }

        return validateBasicMarkets(message.getMarket(), message.getProduct());
    }

    /**
     * Validates the provided {@link UFRollbackBetSettlement} message
     *
     * @param message the message object to validate
     * @param rkInfo the associated routing key information
     * @return the validation result
     */
    private ValidationResult validateRollbackBetSettlement(UFRollbackBetSettlement message, RoutingKeyInfo rkInfo) {
        Preconditions.checkNotNull(message);
        Preconditions.checkNotNull(rkInfo);

        if (!validateEventRoutingKeyInfo(rkInfo)) {
            logFailure(message, "routingKey", rkInfo);
            return ValidationResult.Failure;
        }

        return validateBasicMarkets(message.getMarket(), message.getProduct());
    }

    /**
     * Validates the provided {@link UFRollbackBetCancel} message
     *
     * @param message the message object to validate
     * @param rkInfo the associated routing key information
     * @return the validation result
     */
    private ValidationResult validateRollbackBetCancel(UFRollbackBetCancel message, RoutingKeyInfo rkInfo) {
        Preconditions.checkNotNull(message);
        Preconditions.checkNotNull(rkInfo);

        if (!validateEventRoutingKeyInfo(rkInfo)) {
            logFailure(message, "routingKey", rkInfo);
            return ValidationResult.Failure;
        }

        return validateBasicMarkets(message.getMarket(), message.getProduct());
    }

    /**
     * Validates the provided {@link UFFixtureChange} message
     *
     * @param message the message object to validate
     * @param rkInfo the associated routing key information
     * @return the validation result
     */
    private ValidationResult validateFixtureChange(UFFixtureChange message, RoutingKeyInfo rkInfo) {
        Preconditions.checkNotNull(message);
        Preconditions.checkNotNull(rkInfo);

        if (!validateEventRoutingKeyInfo(rkInfo)) {
            logFailure(message, "routingKey", rkInfo);
            return ValidationResult.Failure;
        }

        return ValidationResult.Success;
    }

    /**
     * Validates the provided {@link UFSnapshotComplete} message
     *
     * @param message the message object to validate
     * @return the validation result
     */
    private ValidationResult validateSnapshotComplete(UFSnapshotComplete message) {
        Preconditions.checkNotNull(message);

        return ValidationResult.Success;
    }

    /**
     * Validates the provided {@link UFAlive} message
     *
     * @param message the message object to validate
     * @return the validation result
     */
    private ValidationResult validateAlive(UFAlive message) {
        Preconditions.checkNotNull(message);

        return ValidationResult.Success;
    }

    /**
     * Validates a list of basic {@link UFMarket}s
     *
     * @param market the list of markets that needs to be validated
     * @param producerId the associated producer id
     * @return the validation result
     */
    private ValidationResult validateBasicMarkets(List<UFMarket> market, int producerId) {
        if (market == null || market.isEmpty()) {
            return ValidationResult.Success;
        }

        ValidationResult result = ValidationResult.Success;
        for (UFMarket ufMarket : market) {
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
            descriptor = marketDescriptionProvider.getMarketDescription(marketId, null, defaultLocales, false);
        } catch (CacheItemNotFoundException e) {
            logger.info("Market validation failed. Failed to retrieve market descriptor market[id={}]", marketId, e);
            return false;
        }

        if (descriptor.getId() != marketId) {
            logger.info("Market validation failed. Retrieved market descriptor has different market id. RequestedId={}, RetrievedId={}", marketId, descriptor.getId());
            return false;
        }

        if (descriptor.getSpecifiers() != null && !descriptor.getSpecifiers().isEmpty()) {
            List<String> specifiers;
            try {
                specifiers = extractSpecifierKeys(receivedSpecifiers);
            } catch (ParseException e) {
                logger.info("Specifiers validation failed. Could not extract specifier keys from: {}, marketId:{}, producerId:{}. Ex:", receivedSpecifiers, marketId, producerId, e);
                return false;
            }

            if (descriptor.getSpecifiers().size() != specifiers.size()) {
                String requiredSpecifiers = String.join(",",
                        descriptor.getSpecifiers().stream()
                                .map(Specifier::getName)
                                .collect(Collectors.toList()));
                logger.info("Specifiers validation failed. ProducerId={}, MarketId={}, Required={}, Actual={}", producerId, marketId, requiredSpecifiers, receivedSpecifiers);
                return false;
            }

            long matchCounter = specifiers.stream().filter(s -> descriptor.getSpecifiers().stream().anyMatch(spec -> spec.getName().equals(s))).count();

            if (matchCounter != descriptor.getSpecifiers().size()) {
                String requiredSpecifiers = String.join(",",
                        descriptor.getSpecifiers().stream()
                                .map(Specifier::getName)
                                .collect(Collectors.toList()));
                logger.info("Specifiers validation for market[id={}] failed. Required={}, Actual={}", requiredSpecifiers, receivedSpecifiers);
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
                throw new ParseException("The specifiers message String is invalid, expected format: 'k=v|k1=v1|,...', input: " + receivedSpecifiers, receivedSpecifiers.indexOf(s));
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

        logger.warn("AMQP Message validation failure: message={}, property={}, propertyValue={} is not expected", msg.getClass().getName(), propertyName, propertyValue);
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

        logger.error("AMQP Message validation error: message={}, property={}, propertyValue={} is not supported", msg.getClass().getName(), propertyName, propertyValue);
    }
}
