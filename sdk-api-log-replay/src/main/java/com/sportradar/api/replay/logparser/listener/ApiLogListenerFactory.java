package com.sportradar.api.replay.logparser.listener;

import com.sportradar.api.replay.logparser.ApiLogProcessorListener;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ApiLogListenerFactory {

  private final ApiLogProcessorListener apiLogProcessorListener;

  public ApiLogListener create() {
    List<ApiLogListener> apiLogListeners = new ArrayList<>();
    apiLogListeners.add(new BookmakerExtractorListener(apiLogProcessorListener));
    apiLogListeners.add(new ProducerUrlRewriteListener());
    apiLogListeners.add(new LogCollectorListener(apiLogProcessorListener));
    apiLogListeners.add(new LogProcessingTimeListener());
    apiLogListeners.add(new LogSequenceListener());
    apiLogListeners.add(new TotalProcessingTimeListener());
    apiLogListeners.add(new LogRecordedLengthListener());
    return new CompositeApiLogListener(apiLogListeners);
  }
}
