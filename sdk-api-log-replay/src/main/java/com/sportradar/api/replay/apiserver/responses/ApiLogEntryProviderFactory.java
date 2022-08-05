package com.sportradar.api.replay.apiserver.responses;

import com.sportradar.api.replay.logparser.ApiLogProcessor;
import com.sportradar.api.replay.logparser.ApiLogProcessorFactory;
import com.sportradar.api.replay.logparser.ApiLogProcessorListener;
import com.sportradar.api.replay.logparser.CompositeApiLogProcessorListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ApiLogEntryProviderFactory {

  private final ApiLogEntryCacheFactory apiLogEntryCacheFactory = new ApiLogEntryCacheFactory();

  public ApiLogEntryProvider create(List<File> logfiles, ApiLogProcessorListener externalListener) {
    ApiLogEntryCache cache = apiLogEntryCacheFactory.create();
    ApiLogProcessorListener listener = createListener(cache, externalListener);
    ApiLogProcessor processor = new ApiLogProcessorFactory().create(logfiles, listener);
    return new ApiLogEntryProvider(cache, processor);
  }

  private ApiLogProcessorListener createListener(ApiLogEntryCache cache,
      ApiLogProcessorListener externalListener) {
    List<ApiLogProcessorListener> listeners = new ArrayList<>();
    listeners.add(new CachePopulatingApiLogProcessorListener(cache));
    listeners.add(externalListener);
    return new CompositeApiLogProcessorListener(listeners);
  }
}
