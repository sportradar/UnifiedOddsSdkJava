package com.sportradar.api.replay.apiserver.handlers.canned;

import com.sportradar.api.replay.apiserver.ApiHandlerConfig;
import com.sportradar.api.replay.apiserver.ApiServerRequestListener;
import com.sportradar.api.replay.apiserver.responses.ApiEndpoint;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ProductRecoveryInitiateHandler extends TemplatedResponseApiHandler {

  private static final Pattern REGEX_REQUEST_ID = Pattern.compile(".*request_id=(\\d+)");

  private final int bookmakerID;

  public ProductRecoveryInitiateHandler(
      TemplateFactory templateFactory,
      ApiHandlerConfig config,
      int bookmakerID,
      ApiServerRequestListener requestListener) {
    super(templateFactory, config, requestListener);
    this.bookmakerID = bookmakerID;
  }

  @Override
  void populateInput(
      Map<String, Object> input, String url, Map<String, Deque<String>> queryParameters) {
    int requestID = extractRequestID(url);
    input.put("requestID", requestID);
    input.put("bookmakerID", bookmakerID);

    if (requestListener != null) {
      requestListener.received(ApiEndpoint.RecoveryInitiateRequest, url, queryParameters);
    }
  }

  // FIXME need to extract requestID from Form data as this is a POST
  private int extractRequestID(String url) {
    Matcher matcher = REGEX_REQUEST_ID.matcher(url);
    if (matcher.find()) {
      String strRequestID = matcher.group(1);
      return Integer.parseInt(strRequestID);
    } else {
      return 0;
    }
  }

  @Override
  String renderXmlTemplate(Map<String, Object> input) {
    return templateFactory.productRecoveryInitiate(input);
  }

  public static void main(String[] args) {
    ProductRecoveryInitiateHandler handler =
        new ProductRecoveryInitiateHandler(
            new TemplateFactory(), new ApiHandlerConfig(), 123, null);

    Map<String, Object> input = new HashMap<>();
    // FIXME this port 8080 should be configurable and set from AbstractSdkIntegrationTest
    handler.populateInput(
        input, "localhost:8080/v1/pre/recovery/initiate_request?request_id=45678", null);

    System.out.println(input.get("requestID"));
    System.out.println(input.get("bookmakerID"));
  }
}
