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
/*
 * DONE:
 * setup rabbitmq using docker as per Etien's docker-compose fragment
 * added a user in rabbitmq UI, username = tkIQxFhK84x4QdgPXR, pwd = tkIQxFhK84x4QdgPXR (token provided by Matej)
 * created rabbitmq vhost /unifiedfeed/999 as 999 is the bookmakerid i'm returning from my wiremock fixtures
 */
public class SdkLogReplay {

    public static void main(String[] args) throws MalformedLogEntry, LogFileNotFound {

        String amqpHost = "localhost";
        int bookmakerId = 999;
        String amqpPassword = "guest";

        // paths are relative to the resources folder
        List<String> logFiles = Arrays.asList(
            "src/test/resources/mq-traffic.log");

        MessagePlayer player = new MessagePlayer(logFiles, amqpHost, bookmakerId, amqpPassword);

        // player.validateLogs(); // if you want to validate some logs before running
        player.publishLogs();
    }
}
