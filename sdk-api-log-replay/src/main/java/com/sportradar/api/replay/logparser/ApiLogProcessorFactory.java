package com.sportradar.api.replay.logparser;

import com.sportradar.api.replay.logparser.listener.ApiLogListener;
import com.sportradar.api.replay.logparser.listener.ApiLogListenerFactory;
import java.io.File;
import java.util.List;

public class ApiLogProcessorFactory {

  public ApiLogProcessor create(List<File> logfiles, ApiLogProcessorListener externalListener) {
    ApiLogListener listener = new ApiLogListenerFactory(externalListener).create();
    ApiLogLineParser parser = new ApiLogLineParser(listener);
    ApiLogLineReader reader = new ApiLogLineReader(logfiles, listener, parser);
    return new ApiLogProcessor(reader);
  }
}
