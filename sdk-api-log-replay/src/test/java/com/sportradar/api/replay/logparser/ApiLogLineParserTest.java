package com.sportradar.api.replay.logparser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.sportradar.api.replay.logparser.listener.LogCollectorListener;
import java.util.Date;
import org.junit.Test;

public class ApiLogLineParserTest {

  private final RecordingApiLogProcessorListener listener = new RecordingApiLogProcessorListener();
  private final LogCollectorListener logCollector = new LogCollectorListener(listener);
  private final ApiLogLineParser parser = new ApiLogLineParser(logCollector);

  @Test
  public void should_parse_get_request() {
    String logGet = "2022-04-16 13:34:26,876 [INFO ] [] [main] Request[DataFetcher]: https://global.stgapi.betradar.com/v1/users/whoami.xml, response - OK(2.981 s): <?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><bookmaker_details response_code=\"OK\" expire_at=\"2023-02-28T13:16:44Z\" bookmaker_id=\"16281\" virtual_host=\"/unifiedfeed/16281\"/>";

    parser.accept(logGet);

    assertEquals(1, listener.getLogEntries().size());
    LogEntry logEntry = listener.getLogEntries().get(0);

    assertEquals("2022-04-16 13:34:26,876", toDateString(logEntry.getTimestamp()));
    assertEquals("https://global.stgapi.betradar.com/v1/users/whoami.xml", logEntry.getUrl());
    assertEquals("GET", logEntry.getHttpMethod());
    assertTrue(logEntry.isSuccess());
    assertTrue(logEntry.getXml().startsWith("<?xml"));
    assertTrue(logEntry.getXml().endsWith("virtual_host=\"/unifiedfeed/16281\"/>"));
  }

  @Test
  public void should_parse_summary_request() {
    String logSummary = "2022-04-16 13:38:13,498 [INFO ] [uf-sdk-16281-1] [uf-sdk-16281-1-amqp-t-2] Request[DataFetcher]: https://global.stgapi.betradar.com/v1/sports/en/sport_events/sr:stage:1009827/summary.xml, response - OK(202.9 ms): <tournament_info generated_at=\"2022-04-16T12:38:13.514+00:00\" xsi:schemaLocation=\"http://schemas.sportradar.com/sportsapi/v1/unified http://schemas.sportradar.com/bsa-staging/unified/v1/xml/endpoints/unified/tournament_info.xsd\" xmlns=\"http://schemas.sportradar.com/sportsapi/v1/unified\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">    <tournament id=\"sr:stage:1009827\" name=\"Overall\" scheduled=\"1970-01-01T00:00:00+00:00\" scheduled_end=\"1970-01-01T00:00:00+00:00\">        <sport id=\"sr:sport:48\" name=\"Ski Jumping\"/>        <category id=\"sr:category:140\" name=\"Ski Jumping\"/>    </tournament>    <coverage_info live_coverage=\"false\"/></tournament_info>\n";

    parser.accept(logSummary);

    LogEntry logEntry = listener.getLogEntries().get(0);
    assertEquals(
        "https://global.stgapi.betradar.com/v1/sports/en/sport_events/sr:stage:1009827/summary.xml",
        logEntry.getUrl());
    assertTrue(logEntry.isSuccess());
    assertTrue(logEntry.getXml().startsWith("<tournament_info"));
    assertTrue(logEntry.getXml().endsWith("</tournament_info>"));
  }

  @Test
  public void should_parse_match_summary() {
    String logSummary = "2022-04-16 13:34:38,136 [INFO ] [uf-sdk-16281-1] [uf-sdk-16281-1-amqp-t-4] Request[DataFetcher]: https://global.stgapi.betradar.com/v1/sports/en/sport_events/sr:match:28119244/summary.xml, response - OK(166.3 ms): <?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><match_summary xmlns=\"http://schemas.sportradar.com/sportsapi/v1/unified\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" generated_at=\"2022-04-16T12:34:38+00:00\" xsi:schemaLocation=\"http://schemas.sportradar.com/sportsapi/v1/unified http://schemas.sportradar.com/ufsportsapi/v1/endpoints/unified/ufsportsapi.xsd\"><sport_event id=\"sr:match:28119244\" scheduled=\"2022-04-16T12:00:00+00:00\" start_time_tbd=\"false\"><tournament_round type=\"group\" number=\"34\" group_long_name=\"Regionalliga West\" betradar_id=\"8364\" betradar_name=\"Regionalliga West\" phase=\"regular season\"/><season start_date=\"2021-08-12\" end_date=\"2022-05-14\" year=\"21/22\" tournament_id=\"sr:tournament:493\" id=\"sr:season:85172\" name=\"Regionalliga West 21/22\"/><tournament id=\"sr:tournament:493\" name=\"Regionalliga West\"><sport id=\"sr:sport:1\" name=\"Soccer\"/><category id=\"sr:category:122\" name=\"Germany Amateur\" country_code=\"DEU\"/></tournament><competitors><competitor qualifier=\"home\" id=\"sr:competitor:350336\" name=\"FC Schalke 04 II\" abbreviation=\"S04\" country=\"Germany\" country_code=\"DEU\" age_group=\"U23\" gender=\"male\"><reference_ids><reference_id name=\"betradar\" value=\"228366\"/></reference_ids></competitor><competitor qualifier=\"away\" id=\"sr:competitor:6188\" name=\"Bonner SC\" abbreviation=\"BSC\" country=\"Germany\" country_code=\"DEU\" gender=\"male\"><reference_ids><reference_id name=\"betradar\" value=\"1674463\"/></reference_ids></competitor></competitors><venue id=\"sr:venue:57584\" name=\"Parkstadion (New)\" capacity=\"5000\" city_name=\"Gelsenkirchen\" country_name=\"Germany\" country_code=\"DEU\" map_coordinates=\"51.5594769,7.067421\"/></sport_event><sport_event_conditions><venue id=\"sr:venue:57584\" name=\"Parkstadion (New)\" capacity=\"5000\" city_name=\"Gelsenkirchen\" country_name=\"Germany\" country_code=\"DEU\" map_coordinates=\"51.5594769,7.067421\"/></sport_event_conditions><sport_event_status home_score=\"1\" away_score=\"0\" status_code=\"1\" match_status_code=\"6\" status=\"live\" match_status=\"1st_half\" period=\"1\"/><coverage_info level=\"silver\" live_coverage=\"true\" covered_from=\"venue\"><coverage includes=\"basic_score\"/><coverage includes=\"key_events\"/></coverage_info><statistics><totals><teams><team id=\"sr:competitor:350336\" name=\"FC Schalke 04 II\"><statistics cards=\"0\" corner_kicks=\"2\"/></team><team id=\"sr:competitor:6188\" name=\"Bonner SC\"><statistics cards=\"0\" corner_kicks=\"3\"/></team></teams></totals><periods><period name=\"1st half\"><teams><team id=\"sr:competitor:350336\" name=\"FC Schalke 04 II\"><statistics cards=\"0\" corner_kicks=\"2\"/></team><team id=\"sr:competitor:6188\" name=\"Bonner SC\"><statistics cards=\"0\" corner_kicks=\"3\"/></team></teams></period></periods></statistics></match_summary>";

    parser.accept(logSummary);

    LogEntry logEntry = listener.getLogEntries().get(0);
    assertEquals(
        "https://global.stgapi.betradar.com/v1/sports/en/sport_events/sr:match:28119244/summary.xml",
        logEntry.getUrl());
    assertTrue(logEntry.isSuccess());
    assertTrue(logEntry.getXml().startsWith("<?xml"));
    assertTrue(logEntry.getXml().contains("<match_summary"));
    assertTrue(logEntry.getXml().endsWith("</match_summary>"));
  }

  @Test
  public void should_parse_post_request() {
    String logPost = "2022-04-16 13:35:09,002 [INFO ] [uf-sdk-16281-1] [uf-sdk-16281-1-t-0] Request[POST]: https://stgapi.betradar.com/v1/pre/recovery/initiate_request?node_id=1&request_id=5581, response code - OK[202](99.45 ms): <?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><response response_code=\"ACCEPTED\"><action>Request id: 5581 for PRE odds from bookmaker: 16281 received</action></response>";

    parser.accept(logPost);

    assertEquals(1, listener.getLogEntries().size());
    LogEntry logEntry = listener.getLogEntries().get(0);

    assertEquals("2022-04-16 13:35:09,002", toDateString(logEntry.getTimestamp()));
    assertEquals(
        "https://stgapi.betradar.com/v1/pre/recovery/initiate_request?node_id=1&request_id=5581",
        logEntry.getUrl());
    assertEquals("POST", logEntry.getHttpMethod());
    assertTrue(logEntry.isSuccess());
    assertTrue(logEntry.getXml().startsWith("<?xml"));
    assertTrue(logEntry.getXml().endsWith("bookmaker: 16281 received</action></response>"));
  }

  @Test
  public void should_parse_get_exception() {
    String logGetError =
        "2022-04-16 13:35:08,902 [INFO ] [uf-sdk-16281-1] [uf-sdk-16281-1-t-0] Request[DataFetcher]: https://global.stgapi.betradar.com/v1/sports/en/schedules/2022-04-16/schedule.xml, response - FAILED(32.03 s), ex:\n"
            + "com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException: There was a problem retrieving the requested data\n"
            + "\tat com.sportradar.unifiedodds.sdk.impl.HttpDataFetcher.send(HttpDataFetcher.java:119)\n";

    parser.accept(logGetError);

    assertEquals(1, listener.getLogEntries().size());
    LogEntry logEntry = listener.getLogEntries().get(0);

    assertEquals("2022-04-16 13:35:08,902", toDateString(logEntry.getTimestamp()));
    assertEquals(
        "https://global.stgapi.betradar.com/v1/sports/en/schedules/2022-04-16/schedule.xml",
        logEntry.getUrl());
    assertEquals("GET", logEntry.getHttpMethod());
    assertFalse(logEntry.isSuccess());
    assertNull(logEntry.getXml());
  }

  @Test
  public void should_parse_post_exception() {
    String logPostError =
        "2022-04-16 14:20:57,705 [INFO ] [uf-sdk-16281-1] [uf-sdk-16281-1-t-0] Request[POST]: https://stgapi.betradar.com/v1/liveodds/recovery/initiate_request?after=1650112806814&node_id=1&request_id=5604, FAILED(218.6 ms), ex:\n"
            + "java.net.SocketException: Connection reset";

    parser.accept(logPostError);

    assertEquals(1, listener.getLogEntries().size());
    LogEntry logEntry = listener.getLogEntries().get(0);

    assertEquals("2022-04-16 14:20:57,705", toDateString(logEntry.getTimestamp()));
    assertEquals(
        "https://stgapi.betradar.com/v1/liveodds/recovery/initiate_request?after=1650112806814&node_id=1&request_id=5604",
        logEntry.getUrl());
    assertEquals("POST", logEntry.getHttpMethod());
    assertFalse(logEntry.isSuccess());
    assertNull(logEntry.getXml());
  }

  @Test
  public void should_ignore_exception_stack_trace() {
    String logException =
        "2022-04-16 13:35:08,902 [INFO ] [uf-sdk-16281-1] [uf-sdk-16281-1-t-0] Request[DataFetcher]: https://global.stgapi.betradar.com/v1/sports/en/schedules/2022-04-16/schedule.xml, response - FAILED(32.03 s), ex:\n"
            + "com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException: There was a problem retrieving the requested data\n"
            + "\tat com.sportradar.unifiedodds.sdk.impl.HttpDataFetcher.send(HttpDataFetcher.java:119)\n"
            + "\tat com.sportradar.unifiedodds.sdk.impl.LogHttpDataFetcher.send(LogHttpDataFetcher.java:38)\n"
            + "\tat com.sportradar.unifiedodds.sdk.impl.HttpDataFetcher.get(HttpDataFetcher.java:56)\n"
            + "\tat com.sportradar.unifiedodds.sdk.impl.LogHttpDataFetcher.get(LogHttpDataFetcher.java:21)\n"
            + "\tat com.sportradar.unifiedodds.sdk.impl.DataProvider.fetchData(DataProvider.java:152)\n"
            + "\tat com.sportradar.unifiedodds.sdk.impl.DataProvider.getData(DataProvider.java:100)\n"
            + "\tat com.sportradar.unifiedodds.sdk.caching.impl.DataRouterManagerImpl.requestEventsFor(DataRouterManagerImpl.java:596)\n"
            + "\tat com.sportradar.unifiedodds.sdk.caching.impl.DataRouterManagerImpl.lambda$null$11(DataRouterManagerImpl.java:961)\n"
            + "\tat java.util.ArrayList.forEach(ArrayList.java:1257)\n"
            + "\tat com.sportradar.unifiedodds.sdk.caching.impl.DataRouterManagerImpl.lambda$onDateScheduleTimerElapsed$12(DataRouterManagerImpl.java:959)\n"
            + "\tat java.util.ArrayList.forEach(ArrayList.java:1257)\n"
            + "\tat com.sportradar.unifiedodds.sdk.caching.impl.DataRouterManagerImpl.onDateScheduleTimerElapsed(DataRouterManagerImpl.java:959)\n"
            + "\tat com.sportradar.unifiedodds.sdk.impl.util.MdcScheduledExecutorService.lambda$setMDCContext$1(MdcScheduledExecutorService.java:470)\n"
            + "\tat java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511)\n"
            + "\tat java.util.concurrent.FutureTask.runAndReset(FutureTask.java:308)\n"
            + "\tat java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.access$301(ScheduledThreadPoolExecutor.java:180)\n"
            + "\tat java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.run(ScheduledThreadPoolExecutor.java:294)\n"
            + "\tat java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)\n"
            + "\tat java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)\n"
            + "\tat java.lang.Thread.run(Thread.java:748)\n"
            + "Caused by: java.net.SocketTimeoutException: Read timed out\n"
            + "\tat java.net.SocketInputStream.socketRead0(Native Method)\n"
            + "\tat java.net.SocketInputStream.socketRead(SocketInputStream.java:116)\n"
            + "\tat java.net.SocketInputStream.read(SocketInputStream.java:171)\n"
            + "\tat java.net.SocketInputStream.read(SocketInputStream.java:141)\n"
            + "\tat sun.security.ssl.InputRecord.readFully(InputRecord.java:465)\n"
            + "\tat sun.security.ssl.InputRecord.read(InputRecord.java:503)\n"
            + "\tat sun.security.ssl.SSLSocketImpl.readRecord(SSLSocketImpl.java:975)\n"
            + "\tat sun.security.ssl.SSLSocketImpl.readDataRecord(SSLSocketImpl.java:933)\n"
            + "\tat sun.security.ssl.AppInputStream.read(AppInputStream.java:105)\n"
            + "\tat org.apache.http.impl.io.SessionInputBufferImpl.streamRead(SessionInputBufferImpl.java:139)\n"
            + "\tat org.apache.http.impl.io.SessionInputBufferImpl.fillBuffer(SessionInputBufferImpl.java:155)\n"
            + "\tat org.apache.http.impl.io.SessionInputBufferImpl.readLine(SessionInputBufferImpl.java:284)\n"
            + "\tat org.apache.http.impl.io.ChunkedInputStream.getChunkSize(ChunkedInputStream.java:266)\n"
            + "\tat org.apache.http.impl.io.ChunkedInputStream.nextChunk(ChunkedInputStream.java:227)\n"
            + "\tat org.apache.http.impl.io.ChunkedInputStream.read(ChunkedInputStream.java:186)\n"
            + "\tat org.apache.http.conn.EofSensorInputStream.read(EofSensorInputStream.java:137)\n"
            + "\tat java.util.zip.InflaterInputStream.fill(InflaterInputStream.java:238)\n"
            + "\tat java.util.zip.InflaterInputStream.read(InflaterInputStream.java:158)\n"
            + "\tat java.util.zip.GZIPInputStream.read(GZIPInputStream.java:117)\n"
            + "\tat org.apache.http.client.entity.LazyDecompressingInputStream.read(LazyDecompressingInputStream.java:73)\n"
            + "\tat sun.nio.cs.StreamDecoder.readBytes(StreamDecoder.java:284)\n"
            + "\tat sun.nio.cs.StreamDecoder.implRead(StreamDecoder.java:326)\n"
            + "\tat sun.nio.cs.StreamDecoder.read(StreamDecoder.java:178)\n"
            + "\tat java.io.InputStreamReader.read(InputStreamReader.java:184)\n"
            + "\tat java.io.Reader.read(Reader.java:140)\n"
            + "\tat org.apache.http.util.EntityUtils.toString(EntityUtils.java:247)\n"
            + "\tat com.sportradar.unifiedodds.sdk.impl.HttpDataFetcher.send(HttpDataFetcher.java:99)\n"
            + "\t... 19 common frames omitted\n";
    String log = "2022-04-16 13:35:09,002 [INFO ] [uf-sdk-16281-1] [uf-sdk-16281-1-t-0] Request[POST]: https://stgapi.betradar.com/v1/pre/recovery/initiate_request?node_id=1&request_id=5581, response code - OK[202](99.45 ms): <?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><response response_code=\"ACCEPTED\"><action>Request id: 5581 for PRE odds from bookmaker: 16281 received</action></response>\n";

    parser.accept(logException);
    parser.accept(log);

    assertEquals(2, listener.getLogEntries().size());
    LogEntry logEntry = listener.getLogEntries().get(0);
    assertEquals("2022-04-16 13:35:08,902", toDateString(logEntry.getTimestamp()));
    assertNull(logEntry.getXml());

    logEntry = listener.getLogEntries().get(1);
    assertEquals("2022-04-16 13:35:09,002", toDateString(logEntry.getTimestamp()));
  }

  @Test
  public void should_parse_duration_from_get() {
    String logSummary = "2022-04-16 13:38:13,498 [INFO ] [uf-sdk-16281-1] [uApiServef-sdk-16281-1-amqp-t-2] Request[DataFetcher]: https://global.stgapi.betradar.com/v1/sports/en/sport_events/sr:stage:1009827/summary.xml, response - OK(202.9 ms): <tournament_info generated_at=\"2022-04-16T12:38:13.514+00:00\" xsi:schemaLocation=\"http://schemas.sportradar.com/sportsapi/v1/unified http://schemas.sportradar.com/bsa-staging/unified/v1/xml/endpoints/unified/tournament_info.xsd\" xmlns=\"http://schemas.sportradar.com/sportsapi/v1/unified\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">    <tournament id=\"sr:stage:1009827\" name=\"Overall\" scheduled=\"1970-01-01T00:00:00+00:00\" scheduled_end=\"1970-01-01T00:00:00+00:00\">        <sport id=\"sr:sport:48\" name=\"Ski Jumping\"/>        <category id=\"sr:category:140\" name=\"Ski Jumping\"/>    </tournament>    <coverage_info live_coverage=\"false\"/></tournament_info>\n";

    parser.accept(logSummary);

    LogEntry logEntry = listener.getLogEntries().get(0);
    assertEquals(202, logEntry.getDurationInMillis());
  }

  @Test
  public void should_parse_duration_from_failed_get() {
    String logGetError =
        "2022-04-16 13:35:08,902 [INFO ] [uf-sdk-16281-1] [uf-sdk-16281-1-t-0] Request[DataFetcher]: https://global.stgapi.betradar.com/v1/sports/en/schedules/2022-04-16/schedule.xml, response - FAILED(32.03 s), ex:\n"
            + "com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException: There was a problem retrieving the requested data\n"
            + "\tat com.sportradar.unifiedodds.sdk.impl.HttpDataFetcher.send(HttpDataFetcher.java:119)\n";

    parser.accept(logGetError);

    LogEntry logEntry = listener.getLogEntries().get(0);
    assertEquals(32000, logEntry.getDurationInMillis());
  }

  @Test
  public void should_parse_duration_from_post() {
    String logPost = "2022-04-16 13:35:09,002 [INFO ] [uf-sdk-16281-1] [uf-sdk-16281-1-t-0] Request[POST]: https://stgapi.betradar.com/v1/pre/recovery/initiate_request?node_id=1&request_id=5581, response code - OK[202](99.45 ms): <?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><response response_code=\"ACCEPTED\"><action>Request id: 5581 for PRE odds from bookmaker: 16281 received</action></response>";

    parser.accept(logPost);

    LogEntry logEntry = listener.getLogEntries().get(0);
    assertEquals(99, logEntry.getDurationInMillis());
  }

  @Test
  public void should_parse_duration_from_failed_post() {
    String logPostError =
        "2022-04-16 14:20:57,705 [INFO ] [uf-sdk-16281-1] [uf-sdk-16281-1-t-0] Request[POST]: https://stgapi.betradar.com/v1/liveodds/recovery/initiate_request?after=1650112806814&node_id=1&request_id=5604, FAILED(218.6 ms), ex:\n"
            + "java.net.SocketException: Connection reset";

    parser.accept(logPostError);

    LogEntry logEntry = listener.getLogEntries().get(0);
    assertEquals(218, logEntry.getDurationInMillis());
  }

  private String toDateString(Date date) {
    return ApiLogLineParser.LOG_DATE_FORMAT.format(date);
  }
}
