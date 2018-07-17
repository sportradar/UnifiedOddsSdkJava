/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.localreplay;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.impl.ChannelMessageConsumer;
import com.sportradar.unifiedodds.sdk.impl.RabbitMqChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by urosbregar on 17/07/2017.
 */
public class LogFileChannel implements RabbitMqChannel {

    private static final Logger logger = LoggerFactory.getLogger(LogFileChannel.class);

    private final File fileHandle;

    private final AtomicBoolean isOpened = new AtomicBoolean(false);

    private Thread worker;


    public LogFileChannel(File fileHandle){
        Preconditions.checkArgument(fileHandle != null, "fileHandle cannot be a null referencee");

        this.fileHandle = fileHandle;
    }

    private void readFile(){

        InputStream inputStream;

        try {
            inputStream = new FileInputStream(fileHandle);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(String.format("The file %s could not be opened", fileHandle.getAbsolutePath()));
        }

        try (Scanner scanner = new Scanner(inputStream, "UTF-8")){

            while(scanner.hasNextLine()){
                String line = scanner.nextLine();
                line = line + "a";
            }
        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                logger.warn("An error occurred while closing the input stream. Exception:", e);
            }
        }
    }


    /**
     * Opens the current channel and binds the created queue to the provided routing keys
     *
     * @param routingKeys            - a {@link List} of routing keys which should be binded
     * @param channelMessageConsumer - a {@link ChannelMessageConsumer} which consumes the received payloads
     */
    @Override
    public void open(List<String> routingKeys, ChannelMessageConsumer channelMessageConsumer) {
        if(isOpened()){
            throw new IllegalStateException("The channel is already opened");
        }

        readFile();
        isOpened.set(true);
    }

    /**
     * Terminates the current channel
     */
    @Override
    public void close() {
        if(!isOpened()){
            throw new IllegalStateException("The channel is already closed");
        }

        isOpened.set(false);
    }

    /**
     * Indicates if the associated channel instance is opened
     *
     * @return - <code>true</code> if the channel is opened; <code>false</code> otherwise
     */
    @Override
    public boolean isOpened() {
        return isOpened.get();
    }
}
