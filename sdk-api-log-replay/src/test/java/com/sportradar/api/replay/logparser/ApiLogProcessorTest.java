package com.sportradar.api.replay.logparser;

import static org.junit.Assert.assertEquals;

import com.sportradar.api.replay.logparser.filesystem.TestFileResolver;
import java.io.File;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;

public class ApiLogProcessorTest {

  private ApiLogProcessor logProcessor;
  private final RecordingApiLogProcessorListener listener = new RecordingApiLogProcessorListener();

  @Ignore("slow - takes a few minutes to run")
  @Test
  public void should_process_multiple_logs_from_same_sdk_run() {
    logProcessor = create("restTraffic/uf-sdk-rest-traffic-2022-04-16_0.log",
        "restTraffic/uf-sdk-rest-traffic-2022-04-16_1.log",
        "restTraffic/uf-sdk-rest-traffic-2022-04-16_2.log");
    logProcessor.processAll();

    assertEquals(16281, listener.getBookmakerID());
    assertEquals(1872, listener.getTotalLogEntries());
  }

  @Test
  public void should_process_multiple_logs() {
    logProcessor = create("basic/basic-1.log", "basic/basic-2.log");
    logProcessor.processAll();

    assertEquals(2, listener.getTotalLogEntries());
  }

  @Test
  public void should_parse_log_with_exception() {
    logProcessor = create("apiLogWithException.log");
    logProcessor.processAll();

    assertEquals(16281, listener.getBookmakerID());
    assertEquals(4, listener.getTotalLogEntries());
  }

  @Test(expected = LogParserException.class)
  public void should_fail_when_timestamp_difference_between_logfiles_is_too_large() {
    logProcessor = create("mismatch/mismatchingLogs1.log", "mismatch/mismatchingLogs2.log");
    logProcessor.processAll();
  }

  private ApiLogProcessor create(String... logfilenames) {
    List<File> logFilenames = TestFileResolver.testResourcesFiles(logfilenames);
    return new ApiLogProcessorFactory().create(logFilenames, listener);
  }
}