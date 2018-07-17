/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example;


import com.sportradar.unifiedodds.example.player.MessagePlayer;
import com.sportradar.unifiedodds.example.player.exceptions.LogFileNotFound;
import com.sportradar.unifiedodds.example.player.exceptions.MalformedLogEntry;

import java.util.Arrays;
import java.util.List;

/**
 * HOW-TO:
 * - setup a RabbitMQ server instance
 * - add a user to the RabbitMQ instance. The username should be the access token provided by Sportradar, password as you wish
 * - create a new vhost on the RabbitMQ & ensure the new user has permissions on it. The vhost should be as follows: /unifiedfeed/{bookmakerId}
 *          (the bookmaker id can be obtained from the api.betradar.com/v1/users/whoami.xml endpoint or from the SDK logs)
 * - start the SDK(wait for it to establish the connection to the server)
 * - start the replay service
 */
public class SdkLogReplay {

    public static void main(String[] args) throws MalformedLogEntry, LogFileNotFound {

        String amqpHost = "localhost";
        int bookmakerId = 22017;
        String amqpPassword = "guest";

        // paths are relative to the resources folder
        List<String> logFiles = Arrays.asList(
                "logs/uf-sdk-traffic.log",
                "logs/uf-sdk-traffic-2018-01-03_0.log");

        MessagePlayer player = new MessagePlayer(logFiles, amqpHost, bookmakerId, amqpPassword);

        // player.validateLogs(); // if you want to validate some logs before running
        player.publishLogs();
    }
}
