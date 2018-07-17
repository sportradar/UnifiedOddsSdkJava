/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example.player;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.sportradar.unifiedodds.example.player.exceptions.LogFileNotFound;
import com.sportradar.unifiedodds.example.player.exceptions.MalformedLogEntry;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Created on 05/01/2018.
 * // TODO @eti: Javadoc
 */
public class MessagePlayer {
    private static final boolean DO_MESSAGE_DELAYS = false;
    private static final Set<String> IGNORED_ROUTING_KEYS = ImmutableSet.<String>builder()
            .add("-.-.-.snapshot_complete.-.-.-.-")
            .add("-.-.-.alive.-.-.-.-")
            .build();

    private final List<String> logFiles;
    private final MessageParser messageParser;
    private final MessagePublisher messagePublisher;

    private Date previousMessageDate;
    private int messageCounter = 0;

    /**
     * Initializes a new message player instance
     *
     * @param logFiles listed relative to the resources folder
     */
    public MessagePlayer(List<String> logFiles, String amqpHost, int bookmakerId, String password) {
        Preconditions.checkNotNull(logFiles);
        Preconditions.checkNotNull(amqpHost);

        this.logFiles = logFiles;
        this.messageParser = new MessageParser();
        this.messagePublisher = new MessagePublisher(amqpHost, password, bookmakerId);
    }

    public void validateLogs() throws LogFileNotFound, MalformedLogEntry {
        processLogFiles(messageParser::parseLine);
    }

    public void publishLogs() throws LogFileNotFound, MalformedLogEntry {
        System.out.println("========= Message publishing started =========");
        messagePublisher.init();

        processLogFiles(this::publishLine);

        messagePublisher.destroy();
        System.out.println("========= Message publishing finished =========");
        System.out.println("Published messages count: " + messageCounter);
    }

    private void processLogFiles(Consumer<String> lineConsumer) throws LogFileNotFound, MalformedLogEntry {
        for (String filePath : logFiles) {
            Path path = provideValidPath(filePath);

            try (Stream<String> stream = Files.lines(path)) {
                stream.forEach(lineConsumer);
            } catch (IOException e) {
                throw new LogFileNotFound("Error opening log file ~> " + filePath, e);
            } catch (IllegalArgumentException exc) {
                throw new MalformedLogEntry("Log file entry invalid ~> " + filePath, exc);
            }
        }
    }

    private Path provideValidPath(String filePath) throws LogFileNotFound {
        try {
            URI fullUriPath = Optional
                    .ofNullable(getClass().getClassLoader().getResource(filePath))
                    .orElseThrow(() -> new IllegalArgumentException("Log file not found ~> " + filePath))
                    .toURI();

            return Paths.get(fullUriPath);
        } catch (URISyntaxException e) {
            throw new LogFileNotFound("Invalid log URI path ~> " + filePath, e);
        }
    }

    private void publishLine(String line) {
        ParsedLine parsedLine = messageParser.parseLine(line);

        if (IGNORED_ROUTING_KEYS.contains(parsedLine.getRoutingKey())) {
            return;
        }

        if (DO_MESSAGE_DELAYS) {
            handleMessageDelay(parsedLine, previousMessageDate);
        }

        System.out.println("Publishing line: " + parsedLine);
        messagePublisher.publishMessage(parsedLine.getRoutingKey(), parsedLine.getMessagePayload().getBytes());

        messageCounter++;
    }

    private void handleMessageDelay(ParsedLine parsedLine, Date previousMessageDate) {
        Preconditions.checkNotNull(parsedLine);

        if (previousMessageDate == null) {
            return;
        }

        long diff = previousMessageDate.getTime() - parsedLine.getTimestamp().getTime();

        try {
            Thread.sleep(diff);
        } catch (InterruptedException e) {
            System.err.println("Message delay failed");
            e.printStackTrace();
        }

        this.previousMessageDate = parsedLine.getTimestamp();
    }
}
