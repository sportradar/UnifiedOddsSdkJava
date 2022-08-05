package com.sportradar.unifiedodds.sdk.logs;

import com.sportradar.unifiedodds.sdk.logs.config.ConfigModel;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class GeneralLogProcessor {

  private final List<Path> logfiles;
  // private final ApiLogProcessorListener apiLogProcessorListener;

  private final ConfigModel configModel;

  private LogLineParser parser;

  public void run() {
//    ApiLogListenerFactory listenerFactory = new ApiLogListenerFactory(apiLogProcessorListener);
    ApiLogListener listener = new ApiLogListener() {
      @Override
      public void onNewLogfile(Path logfile) {
        ApiLogListener.super.onNewLogfile(logfile);
      }

      @Override
      public void onNewLogEntry(LogEntry logEntry) {
        ApiLogListener.super.onNewLogEntry(logEntry);
      }

      @Override
      public void onComplete() {
        ApiLogListener.super.onComplete();
      }
    };

    parser = new LogLineParser(listener, configModel);
    ApiLogLineReader logLineReader = new ApiLogLineReader(logfiles, listener, parser);
    logLineReader.setCharset(Charset.forName(configModel.getContentEncoding()));

    int totalLogs = 0;
    String line = logLineReader.nextLogLine();
    while (line != null) {
      totalLogs++;
      LineFileModel lineFileModel = new LineFileModel(line, logLineReader.getFileName());
      parser.accept(lineFileModel);
      line = logLineReader.nextLogLine();
    }

    log.info("Processed {} log entries", totalLogs);
    return;
  }

  public void dumpExceptionDataAsConfluenceTable() {
    parser.dumpExceptionDataAsConfluenceTable();
  }

  public TreeMap<String, LogEntry> getEventMap() {
    return parser.getEventMap();
  }

  public Set<String> getUniqueUrls() {
    return parser.getUniqueUrls();
  }

}
