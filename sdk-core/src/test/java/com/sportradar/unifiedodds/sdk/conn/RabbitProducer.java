package com.sportradar.unifiedodds.sdk.conn;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.http.client.Client;
import com.rabbitmq.http.client.ClientParameters;
import com.rabbitmq.http.client.domain.ConnectionInfo;
import com.rabbitmq.http.client.domain.UserInfo;
import com.rabbitmq.http.client.domain.UserPermissions;
import com.rabbitmq.http.client.domain.VhostInfo;
import com.sportradar.uf.datamodel.*;
import com.sportradar.unifiedodds.sdk.ProducerScope;
import com.sportradar.unifiedodds.sdk.impl.Constants;
import com.sportradar.unifiedodds.sdk.impl.ProducerData;
import com.sportradar.unifiedodds.sdk.oddsentities.UnmarshalledMessage;
import com.sportradar.unifiedodds.sdk.shared.FeedMessageBuilder;
import com.sportradar.unifiedodds.sdk.shared.Helper;
import com.sportradar.unifiedodds.sdk.shared.TestProducersProvider;
import com.sportradar.utils.URN;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;

/**
 *  Management of rabbit server and sending messages
 *  On rabbit server there should be additional user:testuser/testpass and virtual host: /virtualhost with read/write permission
 */
public class RabbitProducer {
    private Connection connection;
    private Channel channel;
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

    public RabbitProducer(TestProducersProvider producersProvider)
    {
        testProducersProvider = producersProvider;
        Messages = Collections.synchronizedList(new ArrayList());
        ProducersAlive = new HashMap<>();
        feedMessageBuilder = new FeedMessageBuilder(1);
        isRunning = false;
        try {
            ClientParameters parameters = new ClientParameters()
                    .url("http://" + Constants.RABBIT_IP + ":15672/api/")
                    .username("guest")
                    .password("guest");
            ManagementClient = new Client(parameters);
        }
        catch(Exception ex) {
            Helper.writeToOutput(ex.getMessage());
        }

        List<UserInfo> rabbitUsers = ManagementClient.getUsers();
        if (rabbitUsers.stream().noneMatch(a->a.getName().equals(Constants.SDK_USERNAME)))
        {
            ManagementClient.createUser(Constants.SDK_USERNAME, Constants.SDK_PASSWORD.toCharArray(), Collections.singletonList("administrator"));
        } else{
            // reset sdk rabbit connection user
            ManagementClient.updateUser(Constants.SDK_USERNAME, Constants.SDK_PASSWORD.toCharArray(), Collections.singletonList("administrator"));
        }

        List<VhostInfo> virtualHosts = ManagementClient.getVhosts();
        if (virtualHosts.stream().noneMatch(a->a.getName().equals(Constants.UF_VIRTUALHOST)))
        {
            UserPermissions userPermissions = new UserPermissions();
            userPermissions.setUser(Constants.SDK_USERNAME);
            userPermissions.setVhost(Constants.UF_VIRTUALHOST);
            userPermissions.setConfigure(".*");
            userPermissions.setRead(".*");
            userPermissions.setWrite(".*");
            ManagementClient.createVhost(Constants.UF_VIRTUALHOST);
            ManagementClient.updatePermissions(Constants.UF_VIRTUALHOST, Constants.SDK_USERNAME, userPermissions);
        }
    }

    // start the connection for sending messages
    public void start()
    {
        List<ConnectionInfo> activeConnections = ManagementClient.getConnections();
        for (ConnectionInfo connectionInfo : activeConnections){
            ManagementClient.closeConnection(connectionInfo.getName(), "cleanup");
        }

        // factory uses default user: guest/guest
        ConnectionFactory factory = new ConnectionFactory();
        factory.setVirtualHost(Constants.UF_VIRTUALHOST);
        factory.setHost(Constants.RABBIT_IP);
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare(Constants.UF_EXCHANGE, "topic", true);
        }
        catch (Exception ex){
            Helper.writeToOutput(ex.getMessage());
        }

        timerTaskScheduler = Executors.newScheduledThreadPool(1);
        timerFuture = timerTaskScheduler.scheduleAtFixedRate(() -> timerCheckAndSend(), 1, 1, TimeUnit.SECONDS);
        isRunning = true;
    }

    // stop the connection for sending messages
    public void stop()
    {
        timerTaskScheduler.shutdownNow();
        timerFuture = null;

        try {
            channel.close();
            connection.close();
        }
        catch (Exception ex){
            Helper.writeToOutput(ex.getMessage());
        }
    }

    public int getConnectionCount(){
        return ManagementClient.getConnections().size();
    }

    /**
     * Sends the message to the rabbit server
     * @param message the message should be valid xml
     * @param routingKey the routing key
     * @param timestamp the timestamp applied to the message or Now
     */
    public void send(String message, String routingKey, long timestamp)
    {
        if (timestamp == 0)
        {
            timestamp = new Date().getTime();
        }

        byte[] body = message.getBytes(StandardCharsets.UTF_8);

        Map<String, Object> headers = new HashMap<>();
        headers.put("timestamp_in_ms", timestamp);
        AMQP.BasicProperties.Builder propsBuilder = new AMQP.BasicProperties.Builder();
        propsBuilder.headers(headers);
        AMQP.BasicProperties basicProperties = propsBuilder.build();

        try {
            channel.basicPublish(Constants.UF_EXCHANGE, routingKey, basicProperties, body);
            String result = String.format("Generated:%s, Routing: %s, Msg: %s",
                                          new Date(timestamp),
                                          routingKey,
                                          message);
            Helper.writeToOutput(result);
        }
        catch(Exception ex){
            Helper.writeToOutput("Error sending message: " + ex.getMessage());
        }
    }

    /**
     * Send the message
     * @param message the message to be sent
     */
    public void send(UnmarshalledMessage message)
    {
        send(message, "", 0);
    }

    /**
     * Send the message
     * @param message the message to be sent
     * @param routingKey the routing key to be applied, or it will be generated based on message type
     * @param timestamp the timestamp to be applied or Now
     */
    public void send(UnmarshalledMessage message, String routingKey, long timestamp)
    {
        if (timestamp == 0)
        {
            timestamp = new Date().getTime();
        }

        String msgBody = buildMessageBody(message);

        if (routingKey == null || routingKey.isEmpty())
        {
            routingKey = buildRoutingKey(message);
        }

        send(msgBody, routingKey, timestamp);
    }

    /**
     * Adds the producerId for periodically send alive messages. And start immediately.
     * @param producerId the producer id
     * @param periodInMs the period in ms before next is sent
     */
    public void addProducersAlive(int producerId, int periodInMs)
    {
        if(periodInMs == 0) {
            periodInMs = 5000;
        }
        if (!ProducersAlive.containsKey(producerId))
        {
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
    public void stopProducersAlive(int producerId)
    {
        if (ProducersAlive.containsKey(producerId))
        {
            ProducersAlive.remove(producerId);
        }
    }

    /**
     * Periodically check if there are messages to be sent (in Messages queue)
     */
    private void timerCheckAndSend()
    {
        if (!isRunning)
        {
            return;
        }
//        Helper.WriteToOutput("timerCheckAndSend");
        for (RabbitMessage rabbitMessage : Messages)
        {
            Helper.writeToOutput("TimerCheckAndSend message");
            if (rabbitMessage.LastSent.before(new Date()))
            {
                send(rabbitMessage.Message, null, 0);
            }
        }
    }

    /**
     * Send the periodic alive for producer (if producerId is listed in ProducersAlive)
     * @param producerId the producer id
     * @param periodInMs the period in ms before next is sent
     */
    private void sendPeriodicAliveForProducer(int producerId, int periodInMs)
    {
        try {
            Thread.sleep( periodInMs == 0 ? 5000 : periodInMs);
            while (ProducersAlive.containsKey(producerId))
            {
                UFAlive msgAlive = feedMessageBuilder.buildAlive(producerId, new Date(), true);
                send(msgAlive, "-.-.-.alive.-.-.-.-", msgAlive.getTimestamp());
                int sleep = periodInMs > 0 ? periodInMs : new Random().nextInt(10000);
                Thread.sleep(sleep);
            }
        }
        catch (Exception ex){
            Helper.writeToOutput("Error: " + ex.getMessage());
        }
    }

    /**
     * Builds the xml message body from the raw feed message (instance)
     * @param message the message to be serialized
     * @return xml message body
     */
    private String buildMessageBody(UnmarshalledMessage message)
    {
        if (message instanceof UFAlive)
        {
            return Helper.serializeToJaxbXml(message);
        }
        if (message instanceof UFOddsChange)
        {
            return Helper.serializeToJaxbXml(message);
        }
        if (message instanceof UFBetStop)
        {
            return Helper.serializeToJaxbXml(message);
        }
        if (message instanceof UFFixtureChange)
        {
            return Helper.serializeToJaxbXml(message);
        }
        if (message instanceof UFSnapshotComplete)
        {
            return Helper.serializeToJaxbXml(message);
        }
        if (message instanceof UFBetSettlement)
        {
            return Helper.serializeToJaxbXml(message);
        }

        return Helper.serializeToJaxbXml(message);
    }

    /**
     * Builds the routing key from the message type
     * @param message the message
     * @return the appropriate routing key
     */
    private String buildRoutingKey(UnmarshalledMessage message)
    {
        if (message instanceof UFAlive)
        {
            return "-.-.-.alive.-.-.-.-";
        }
        else if (message instanceof UFOddsChange)
        {
            UFOddsChange oddsChange = (UFOddsChange)message;
            URN urn = URN.parse(oddsChange.getEventId());
            int sportId = 1;
            return String.format("hi.%s.odds_change.%s.%s:%s.%s.-",
                                 buildSessionPartOfRoutingKey(oddsChange.getProduct()),
                                 sportId,
                                 urn.getPrefix(),
                                 urn.getType(),
                                 urn.getId());
        }
        else if (message instanceof UFBetStop)
        {
            UFBetStop betStop = (UFBetStop)message;
            URN urn = URN.parse(betStop.getEventId());
            int sportId = 1;
            return String.format("hi.%s.bet_stop.%s.%s:%s.%s.-",
                                 buildSessionPartOfRoutingKey(betStop.getProduct()),
                                 sportId,
                                 urn.getPrefix(),
                                 urn.getType(),
                                 urn.getId());
        }
        else if (message instanceof UFFixtureChange)
        {
            UFFixtureChange fixtureChange = (UFFixtureChange)message;
            URN urn = URN.parse(fixtureChange.getEventId());
            int sportId = 1;
            return String.format("hi.pre.live.bet_stop.%s.%s:%s.%s.-",
                                 sportId,
                                 urn.getPrefix(),
                                 urn.getType(),
                                 urn.getId());
        }
        else if(message instanceof UFSnapshotComplete)
        {
            return "-.-.-.snapshot_complete.-.-.-.-";
        }
        else if (message instanceof UFBetSettlement)
        {
            UFBetSettlement betSettlement = (UFBetSettlement)message;
            URN urn = URN.parse(betSettlement.getEventId());
            int sportId = 1;
            return String.format("lo.%s.bet_stop.%s.%s:%s.%s.-",
                                 buildSessionPartOfRoutingKey(betSettlement.getProduct()),
                                 sportId,
                                 urn.getPrefix(),
                                 urn.getType(),
                                 urn.getId());
        }

        return "";
    }

    private String buildSessionPartOfRoutingKey(int producerId)
    {
        ProducerData producer = testProducersProvider.Producers.stream().filter(f -> f.getId() == producerId).findFirst().get();
        if (producer == null)
        {
            return "missing.missing";
        }

        if (producer.getProducerScopes().contains("live") || producer.getProducerScopes().contains(ProducerScope.Live))
        {
            return "-.live";
        }
        if (producer.getProducerScopes().contains("prematch") || producer.getProducerScopes().contains(ProducerScope.Prematch))
        {
            return "pre.-";
        }
        if (producer.getProducerScopes().contains("virtual") || producer.getProducerScopes().contains(ProducerScope.Virtuals))
        {
            return "virt.-";
        }

        return "na.na";
    }
}
