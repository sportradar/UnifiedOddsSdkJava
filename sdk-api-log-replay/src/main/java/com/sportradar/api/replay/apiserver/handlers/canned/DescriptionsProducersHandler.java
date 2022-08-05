package com.sportradar.api.replay.apiserver.handlers.canned;

import static java.lang.String.format;

import com.sportradar.api.replay.apiserver.ApiHandlerConfig;
import com.sportradar.api.replay.apiserver.ApiServerConfig;

class DescriptionsProducersHandler extends AbstractCannedResponseApiHandler {

  private final int apiPort;

  public DescriptionsProducersHandler(ApiServerConfig serverConfig, ApiHandlerConfig config) {
    super(config);
    this.apiPort = serverConfig.getPort();
  }

  @Override
  String createXml() {
    StringBuilder sb = new StringBuilder();
    sb.append("<producers response_code=\"OK\">\n");
    for (int i = 0; i < config.getTotalProducers(); i++) {
      int producerID = i + 1;
      sb.append("    ").append(producerXmlFragment(producerID)).append("\n");
    }
    sb.append("</producers>");
    return sb.toString();
  }

  private String producerXmlFragment(int producerID) {
    String urlName = "producer" + producerID;
    String description = "Producer-" + producerID;
    String name = "PROD" + producerID;
    String apiUrl = format("http://localhost:%d/v1/%s/", apiPort, urlName);

    StringBuilder sb = new StringBuilder();
    sb.append("<producer active=\"true\"");
    sb.append(" api_url=\"").append(apiUrl).append("\"");
    sb.append(" description=\"").append(description).append("\"");
    sb.append(" name=\"").append(name).append("\"");
    sb.append(" id=\"").append(producerID).append("\"");
    sb.append(" stateful_recovery_window_in_minutes=\"").append(180).append("\"");
    sb.append("/>");

    return sb.toString();
  }
}