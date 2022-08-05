package com.sportradar.api.replay.logparser;

import static com.sportradar.api.replay.logparser.filesystem.TestFileResolver.testResourcesFolderPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.sportradar.api.replay.logparser.filesystem.ApiLogfiles;
import com.sportradar.api.replay.logparser.listener.ApiLogListener;
import java.io.File;
import java.util.List;
import java.util.function.Consumer;
import org.junit.Test;

public class ApiLogLineReaderTest {

  private final QueryableApiLogListener apiLogListener = new QueryableApiLogListener();
  private final LogConsumer logConsumer = new LogConsumer();

  @Test
  public void should_read_lines_from_one_logfile() {
    ApiLogLineReader reader = readLogsFrom("streamSingle");

    assertEquals("line 1", reader.nextLogLine());
    assertEquals("line 2", reader.nextLogLine());
    assertNull(reader.nextLogLine());
  }

  @Test
  public void should_notify_consumer() {
    ApiLogLineReader reader = readLogsFrom("streamSingle");

    reader.nextLogLine();
    reader.nextLogLine();

    assertEquals(2, logConsumer.invocationCount);
  }

  @Test
  public void should_read_lines_across_multiple_logfiles_in_order() {
    ApiLogLineReader reader = readLogsFrom("streamMultiple");

    assertEquals("line 1", reader.nextLogLine());
    assertEquals("line 2", reader.nextLogLine());
    assertEquals("line 3", reader.nextLogLine());
    assertEquals("line 4", reader.nextLogLine());
    assertNull(reader.nextLogLine());
  }

  @Test
  public void should_notify_listener() {
    ApiLogLineReader reader = readLogsFrom("streamMultiple");

    while (reader.nextLogLine() != null) {
    }

    assertEquals(2, apiLogListener.logFileCount);
    assertTrue(apiLogListener.complete);
  }

  private ApiLogLineReader readLogsFrom(String testFolderName) {

    return new ApiLogLineReader(logsFrom(testFolderName), apiLogListener, logConsumer);
  }

  private List<File> logsFrom(String testFolderName) {
    return ApiLogfiles.logsFromFolder(
        testResourcesFolderPath(testFolderName));
  }

  private class QueryableApiLogListener implements ApiLogListener {

    private int logFileCount;
    private boolean complete;

    @Override
    public void onNewLogfile(File logfile) {
      logFileCount++;
    }

    @Override
    public void onComplete() {
      complete = true;
    }
  }

  private class LogConsumer implements Consumer<String> {

    private int invocationCount;

    @Override
    public void accept(String s) {
      invocationCount++;
    }
  }
}