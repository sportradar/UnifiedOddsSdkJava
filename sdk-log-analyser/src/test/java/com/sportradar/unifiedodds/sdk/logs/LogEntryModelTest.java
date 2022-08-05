package com.sportradar.unifiedodds.sdk.logs;

import com.sportradar.unifiedodds.sdk.logs.config.ConfigModel;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LogEntryModelTest {
  private LogLineParser p = null;
  private LogEntryModel logEntryModel = null;
  private ConfigModel configModel;

  @Before
  public void startup() {
    configModel = new ConfigModel();
    configModel.setCustomer("eurobet");
    p = new LogLineParser(null, configModel);
  }

  @After
  public void teardown() {
    p = null;
  }


  private void configureEurobet() {
    configModel.setLogOffsetLoggerName(6);
    configModel.setLogOffsetThreadName(5);
    configModel.setDateTimeFormat("yyyy-MM-dd HH:mm:ss.SSS");
  }

  private void configureKladonica() {
    configModel.setLogOffsetThreadName(4);
    configModel.setDateTimeFormat("yyyy-MM-dd HH:mm:ss,SSS");
  }

  private void configureDefault() {
    configModel.setLogOffsetThreadName(4);
    configModel.setDateTimeFormat("yyyy-MM-dd HH:mm:ss,SSS");
  }

  @Test
  // Eurobet
  public void testRestCall0() {
    configureDefault();
    String line = "2022-05-24 11:51:08,846 [INFO ] [uf-sdk-999-1] [uf-sdk-999-1-t-0] Request[DataFetcher]: http://localhost:8080/v1/sports/en/schedules/2022-05-24/schedule.xml, response - OK(4.071 s): <?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>  <schedule xmlns=\"http://schemas.sportradar.com/sportsapi/v1/unified\" ";
    LogEntryModel logEntryModel = p.buildLogEntryModel(line, null);
    System.out.println(logEntryModel);

    Assert.assertEquals("http://localhost:8080/v1/sports/en/schedules/2022-05-24/schedule.xml", logEntryModel.getUrl());
    Assert.assertEquals(true, logEntryModel.isApiCall());
    Assert.assertEquals(false, logEntryModel.isMqThread());
    Assert.assertEquals("uf-sdk-999-1-t-0", logEntryModel.getThreadName());


    line = "2022-05-27 09:59:38,704 [INFO ] [] [Worker-10] [c.s.u.s.impl.LogHttpDataFetcher] - Fetching data from: http://localhost:8080/v1/sports/en/sport_events/sr:match:1653641978629/summary.xml";
    logEntryModel = p.buildLogEntryModel(line, null);
    System.out.println(logEntryModel);

    Assert.assertEquals("http://localhost:8080/v1/sports/en/sport_events/sr:match:1653641978629/summary.xml", logEntryModel.getUrl());
    Assert.assertEquals(true, logEntryModel.isApiCall());
    Assert.assertEquals(false, logEntryModel.isMqThread());
    Assert.assertEquals("Worker-10", logEntryModel.getThreadName());

    line = "2022-05-30 09:31:17,511 [INFO ] [] [Worker-04] [c.s.u.s.LoggerDefinitions$UFSdkRestTrafficLog] - Request[DataFetcher]: http://localhost:8080/v1/sports/en/sport_events/sr:match:1653899477147/fixture_change_fixture.xml, response - OK(187.5 ms): <?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
    logEntryModel = p.buildLogEntryModel(line, null);
    System.out.println(logEntryModel);

    Assert.assertEquals("http://localhost:8080/v1/sports/en/sport_events/sr:match:1653899477147/fixture_change_fixture.xml", logEntryModel.getUrl());
    Assert.assertEquals(true, logEntryModel.isApiCall());
    Assert.assertEquals(false, logEntryModel.isMqThread());
    Assert.assertEquals("Worker-04", logEntryModel.getThreadName());
  }

  @Test
  // Eurobet
  public void testRestCall1() {
    configureEurobet();
    LogEntryModel logEntryModel = p.buildLogEntryModel("2022-05-02 00:00:01.778  INFO 17652 --- [eventId=32284641] .s.LoggerDefinitions$UFSdkRestTrafficLog : Request[DataFetcher]: https://global.api.betradar.com/v1/sports/it/sport_events/sr:match:32284641/timeline.xml, response - OK(134.3 ms): <?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>", null);
    System.out.println(logEntryModel);

    Assert.assertEquals("https://global.api.betradar.com/v1/sports/it/sport_events/sr:match:32284641/timeline.xml", logEntryModel.getUrl());
    Assert.assertEquals(true, logEntryModel.isApiCall());
    Assert.assertEquals(false, logEntryModel.isMqThread());
    Assert.assertEquals("eventId=32284641", logEntryModel.getThreadName());
  }

  @Test
  // Kladonica
  public void testRestCall2() {
    configureKladonica();
    String s = "2022-05-14 04:19:20,745 [INFO ] [uf-sdk-9592-434] [uf-sdk-9592-434-t-0] Request[DataFetcher]: https://global.api.betradar.com/v1/sports/en/tournaments.xml, response - OK(667 ms): <?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><tournaments xmlns=\"http://schemas.sportradar.com/sportsapi/v1/unified\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" generated_at=\"2022-05-14T02:19:20+00:00\" xsi:schemaLocation=\"http://schemas.sportradar.com/sportsapi/v1/unified http://schemas.sportradar.com/ufsportsapi/v1/endpoints/unified/ufsportsapi.xsd\"><tournament id=\"sr:tournament:3\" name=\"World Championship\"></tournament></tournaments>\n";
    LogEntryModel logEntryModel = p.buildLogEntryModel(s, null);
    System.out.println(logEntryModel);

    Assert.assertEquals("https://global.api.betradar.com/v1/sports/en/tournaments.xml", logEntryModel.getUrl());
    Assert.assertEquals(true, logEntryModel.isApiCall());
    Assert.assertEquals(false, logEntryModel.isMqThread());
    Assert.assertEquals("uf-sdk-9592-434-t-0", logEntryModel.getThreadName());

  }

  @Test
  public void testCustomerListenerCall0() {
    configureDefault();
    String s = "2022-05-24 16:07:54,627 [INFO ] [uf-sdk-999-1] [uf-sdk-999-1-amqp-t-0] Message -> (1|sr:match:33275469|UFFixtureChange|1653404874239) processing finished on UFSession-AllMessages, duration: 384 ms\n";
    logEntryModel = p.buildLogEntryModel(s, null);
    System.out.println(logEntryModel);

    Assert.assertEquals(384, logEntryModel.getDurationMillis());
    Assert.assertEquals("uf-sdk-999-1-amqp-t-0", logEntryModel.getThreadName());
    Assert.assertEquals(false, logEntryModel.isApiCall());
    Assert.assertEquals(true, logEntryModel.isMqThread());
  }

  @Test
  public void testCustomerListenerCall1() {
    configureEurobet();
    logEntryModel = p.buildLogEntryModel("2022-04-30 15:40:36.628  INFO 17652 --- [uf-sdk-2-2-amqp-t-0] gerDefinitions$UFSdkClientInteractionLog : Message -> (1|sr:match:33420193|UFOddsChange|1651326036596) processing finished on UFSession-LiveMessagesOnly, duration: 0 ms", null);
    System.out.println(logEntryModel);

    Assert.assertEquals(0, logEntryModel.getDurationMillis());
    Assert.assertEquals("uf-sdk-2-2-amqp-t-0", logEntryModel.getThreadName());
    Assert.assertEquals(false, logEntryModel.isApiCall());
    Assert.assertEquals(true, logEntryModel.isMqThread());
  }

  @Test
  // Kladonica soccer format
  public void testCustomerListenerCall2() {
    configureKladonica();
    logEntryModel = p.buildLogEntryModel("2022-05-14 00:00:04,330 [INFO ] [uf-sdk-9592-434] [uf-sdk-9592-434-amqp-t-0] Message -> (1|sr:match:33603871|UFFixtureChange|1652479204313) processing finished on UFSession-LiveMessagesOnly, duration: 0 ms", null);
    System.out.println(logEntryModel);

    Assert.assertEquals(0, logEntryModel.getDurationMillis());
    Assert.assertEquals(false, logEntryModel.isApiCall());
    Assert.assertEquals(true, logEntryModel.isMqThread());
    Assert.assertEquals(true, logEntryModel.isCustomerMessageListenerCall());
  }
}
