package com.sportradar.unifiedodds.sdk.conn;

import static com.sportradar.unifiedodds.sdk.impl.Constants.RABBIT_BASE_URL;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqProducer.connectDeclaringExchange;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.http.client.Client;
import com.rabbitmq.http.client.ClientParameters;
import com.rabbitmq.http.client.domain.ConnectionInfo;
import com.sportradar.uf.datamodel.*;
import com.sportradar.unifiedodds.sdk.ProducerScope;
import com.sportradar.unifiedodds.sdk.impl.Constants;
import com.sportradar.unifiedodds.sdk.impl.ProducerData;
import com.sportradar.unifiedodds.sdk.impl.TimeUtils;
import com.sportradar.unifiedodds.sdk.oddsentities.UnmarshalledMessage;
import com.sportradar.unifiedodds.sdk.shared.FeedMessageBuilder;
import com.sportradar.unifiedodds.sdk.shared.Helper;
import com.sportradar.unifiedodds.sdk.shared.TestProducersProvider;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.*;
import com.sportradar.utils.Urn;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import lombok.val;

/**
 *  Management of rabbit server and sending messages
 *  On rabbit server there should be additional user:testuser/testpass and virtual host: /virtualhost with read/write permission
 */
@SuppressWarnings(
    {
        "ClassDataAbstractionCoupling",
        "ClassFanOutComplexity",
        "DeclarationOrder",
        "IllegalCatch",
        "MagicNumber",
        "MemberName",
        "MethodLength",
        "ParameterAssignment",
        "ReturnCount",
        "VisibilityModifier",
    }
)
public class RabbitProducer {

    private RabbitMqProducer producer;
    private TimeUtils time = mock(TimeUtils.class);
    private boolean isRunning;
    private final FeedMessageBuilder feedMessageBuilder;
    private final TestProducersProvider testProducersProvider;
    private ScheduledFuture<?> timerFuture;
    private ScheduledExecutorService timerTaskScheduler;

    /**
     * Gets the management client for getting and managing connections and channels
     */
    public Client ManagementClient;

    /**
     * Gets the messages to be sent
     */
    public List<RabbitMessage> Messages;

    /**
     * Gets the list of producers for which alive messages should be periodically sent
     */
    public Map<Integer, Date> ProducersAlive;

    public RabbitProducer(TestProducersProvider producersProvider, final RabbitMqUserSetup rabbitMqUserSetup)
        throws Exception {
        testProducersProvider = producersProvider;
        Messages = Collections.synchronizedList(new ArrayList());
        ProducersAlive = new HashMap<>();
        GlobalVariables variables = new GlobalVariables();
        variables.setProducer(ProducerId.LIVE_ODDS);
        variables.setSportEventUrn(SportEvent.MATCH);
        feedMessageBuilder = new FeedMessageBuilder(variables);
        isRunning = false;

        try {
            ClientParameters parameters = new ClientParameters()
                .url("http://" + Constants.RABBIT_IP + ":15672/api/")
                .username(Constants.ADMIN_USERNAME)
                .password(Constants.ADMIN_PASSWORD);
            ManagementClient = new Client(parameters);
        } catch (Exception ex) {
            Helper.writeToOutput(ex.getMessage());
        }

        rabbitMqUserSetup.setupUser(Credentials.with(Constants.SDK_USERNAME, Constants.SDK_PASSWORD));
    }

    // start the connection for sending messages
    public void start() {
        List<ConnectionInfo> activeConnections = ManagementClient.getConnections();
        for (ConnectionInfo connectionInfo : activeConnections) {
            ManagementClient.closeConnection(connectionInfo.getName(), "cleanup");
        }

        val vhostLocation = VhostLocation.at(RABBIT_BASE_URL, Constants.UF_VIRTUALHOST);
        val exchangeLocation = ExchangeLocation.at(vhostLocation, Constants.UF_EXCHANGE);
        val adminCredentials = Credentials.with(Constants.ADMIN_USERNAME, Constants.ADMIN_PASSWORD);
        val factory = new ConnectionFactory();

        try {
            producer = connectDeclaringExchange(exchangeLocation, adminCredentials, factory, time);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }

        timerTaskScheduler = Executors.newScheduledThreadPool(1);
        timerFuture =
            timerTaskScheduler.scheduleAtFixedRate(() -> timerCheckAndSend(), 1, 1, TimeUnit.SECONDS);
        isRunning = true;
    }

    // stop the connection for sending messages
    public void stop() {
        timerTaskScheduler.shutdownNow();
        timerFuture = null;

        try {
            producer.close();
        } catch (Exception ex) {
            Helper.writeToOutput(ex.getMessage());
        }
    }

    public int getConnectionCount() {
        return ManagementClient.getConnections().size();
    }

    /**
     * Send the message
     * @param message the message to be sent
     */
    public void send(UnmarshalledMessage message) {
        send(message, "", 0);
    }

    /**
     * Send the message
     * @param message the message to be sent
     * @param routingKey the routing key to be applied, or it will be generated based on message type
     * @param timestamp the timestamp to be applied or Now
     */
    public void send(UnmarshalledMessage message, String routingKey, long timestamp) {
        if (timestamp == 0) {
            timestamp = new Date().getTime();
        }

        String msgBody = buildMessageBody(message);

        if (routingKey == null || routingKey.isEmpty()) {
            routingKey = buildRoutingKey(message);
        }

        when(time.now()).thenReturn(timestamp);
        producer.send(msgBody, routingKey);
        String result = String.format(
            "Generated:%s, Routing: %s, Msg: %s",
            new Date(timestamp),
            routingKey,
            message
        );
        Helper.writeToOutput(result);
    }

    /**
     * Adds the producerId for periodically send alive messages. And start immediately.
     * @param producerId the producer id
     * @param periodInMs the period in ms before next is sent
     */
    public void addProducersAlive(int producerId, int periodInMs) {
        if (periodInMs == 0) {
            periodInMs = 5000;
        }
        if (!ProducersAlive.containsKey(producerId)) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.DATE, -30);
            Date dateBefore30Days = cal.getTime();
            ProducersAlive.put(producerId, dateBefore30Days);
            ExecutorService executor = Executors.newFixedThreadPool(1);
            int finalPeriodInMs = periodInMs;
            executor.submit(() -> {
                sendPeriodicAliveForProducer(producerId, finalPeriodInMs);
            });
        }
    }

    /**
     * Removes the producerId for periodically send alive messages. On next iteration will stop.
     * @param producerId the producer id
     */
    public void stopProducersAlive(int producerId) {
        if (ProducersAlive.containsKey(producerId)) {
            ProducersAlive.remove(producerId);
        }
    }

    /**
     * Periodically check if there are messages to be sent (in Messages queue)
     */
    private void timerCheckAndSend() {
        if (!isRunning) {
            return;
        }
        //        Helper.WriteToOutput("timerCheckAndSend");
        for (RabbitMessage rabbitMessage : Messages) {
            Helper.writeToOutput("TimerCheckAndSend message");
            if (rabbitMessage.LastSent.before(new Date())) {
                send(rabbitMessage.Message, null, 0);
            }
        }
    }

    /**
     * Send the periodic alive for producer (if producerId is listed in ProducersAlive)
     * @param producerId the producer id
     * @param periodInMs the period in ms before next is sent
     */
    private void sendPeriodicAliveForProducer(int producerId, int periodInMs) {
        try {
            Thread.sleep(periodInMs == 0 ? 5000 : periodInMs);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        while (ProducersAlive.containsKey(producerId)) {
            try {
                int sleep = periodInMs > 0 ? periodInMs : new Random().nextInt(10000);
                Thread.sleep(sleep);
                UfAlive msgAlive = feedMessageBuilder.buildAlive(producerId, new Date(), true);
                send(msgAlive, "-.-.-.alive.-.-.-.-", msgAlive.getTimestamp());
            } catch (Exception ex) {
                Helper.writeToOutput("Error: " + ex.getMessage());
            }
        }
    }

    /**
     * Builds the xml message body from the raw feed message (instance)
     * @param message the message to be serialized
     * @return xml message body
     */
    private String buildMessageBody(UnmarshalledMessage message) {
        if (message instanceof UfAlive) {
            return Helper.serializeToJaxbXml(message);
        }
        if (message instanceof UfOddsChange) {
            return Helper.serializeToJaxbXml(message);
        }
        if (message instanceof UfBetStop) {
            return Helper.serializeToJaxbXml(message);
        }
        if (message instanceof UfFixtureChange) {
            return Helper.serializeToJaxbXml(message);
        }
        if (message instanceof UfSnapshotComplete) {
            return Helper.serializeToJaxbXml(message);
        }
        if (message instanceof UfBetSettlement) {
            return Helper.serializeToJaxbXml(message);
        }

        return Helper.serializeToJaxbXml(message);
    }

    /**
     * Builds the routing key from the message type
     * @param message the message
     * @return the appropriate routing key
     */
    private String buildRoutingKey(UnmarshalledMessage message) {
        if (message instanceof UfAlive) {
            return "-.-.-.alive.-.-.-.-";
        } else if (message instanceof UfOddsChange) {
            UfOddsChange oddsChange = (UfOddsChange) message;
            Urn urn = Urn.parse(oddsChange.getEventId());
            int sportId = 1;
            return String.format(
                "hi.%s.odds_change.%s.%s:%s.%s.-",
                buildSessionPartOfRoutingKey(oddsChange.getProduct()),
                sportId,
                urn.getPrefix(),
                urn.getType(),
                urn.getId()
            );
        } else if (message instanceof UfBetStop) {
            UfBetStop betStop = (UfBetStop) message;
            Urn urn = Urn.parse(betStop.getEventId());
            int sportId = 1;
            return String.format(
                "hi.%s.bet_stop.%s.%s:%s.%s.-",
                buildSessionPartOfRoutingKey(betStop.getProduct()),
                sportId,
                urn.getPrefix(),
                urn.getType(),
                urn.getId()
            );
        } else if (message instanceof UfFixtureChange) {
            UfFixtureChange fixtureChange = (UfFixtureChange) message;
            Urn urn = Urn.parse(fixtureChange.getEventId());
            int sportId = 1;
            return String.format(
                "hi.pre.live.bet_stop.%s.%s:%s.%s.-",
                sportId,
                urn.getPrefix(),
                urn.getType(),
                urn.getId()
            );
        } else if (message instanceof UfSnapshotComplete) {
            return "-.-.-.snapshot_complete.-.-.-.0";
        } else if (message instanceof UfBetSettlement) {
            UfBetSettlement betSettlement = (UfBetSettlement) message;
            Urn urn = Urn.parse(betSettlement.getEventId());
            int sportId = 1;
            return String.format(
                "lo.%s.bet_stop.%s.%s:%s.%s.-",
                buildSessionPartOfRoutingKey(betSettlement.getProduct()),
                sportId,
                urn.getPrefix(),
                urn.getType(),
                urn.getId()
            );
        }

        return "";
    }

    private String buildSessionPartOfRoutingKey(int producerId) {
        ProducerData producerData = testProducersProvider.Producers
            .stream()
            .filter(f -> f.getId() == producerId)
            .findFirst()
            .get();
        if (producerData == null) {
            return "missing.missing";
        }

        if (
            producerData.getProducerScopes().contains("live") ||
            producerData.getProducerScopes().contains(ProducerScope.Live)
        ) {
            return "-.live";
        }
        if (
            producerData.getProducerScopes().contains("prematch") ||
            producerData.getProducerScopes().contains(ProducerScope.Prematch)
        ) {
            return "pre.-";
        }
        if (
            producerData.getProducerScopes().contains("virtual") ||
            producerData.getProducerScopes().contains(ProducerScope.Virtuals)
        ) {
            return "virt.-";
        }

        return "na.na";
    }
}
