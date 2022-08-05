package com.sportradar.api.replay.apiserver;

import com.sportradar.api.replay.apiserver.responses.ApiEndpoint;
import java.util.Deque;
import java.util.Map;

public interface ApiServerRequestListener {
  void received(ApiEndpoint endpoint, String path, Map<String, Deque<String>> queryParameters);
}
