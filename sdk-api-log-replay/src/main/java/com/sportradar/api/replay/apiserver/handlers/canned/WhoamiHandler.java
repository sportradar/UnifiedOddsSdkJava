package com.sportradar.api.replay.apiserver.handlers.canned;

import com.sportradar.api.replay.apiserver.ApiHandlerConfig;

class WhoamiHandler extends AbstractCannedResponseApiHandler {

  private final int bookmakerID;

  public WhoamiHandler(ApiHandlerConfig config, int bookmakerID) {
    super(config);
    this.bookmakerID = bookmakerID;
  }

  @Override
  String createXml() {
    String expiryDateStr = "2099-12-01T00:00:00Z";

    StringBuilder sb = new StringBuilder();
    sb.append(
            "<bookmaker_details");
    sb.append(
            " response_code=\"OK\"");
    sb.append(
            " expire_at=\"").append(expiryDateStr).append("\"");
    sb.append(
            " bookmaker_id=\"").append(bookmakerID).append("\"");
    sb.append(
            " virtual_host=\"/unifiedfeed/").append(bookmakerID).append("\"");
    sb.append("/>");

    return sb.toString();
  }
}