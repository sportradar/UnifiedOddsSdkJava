package com.sportradar.api.replay.apiserver.handlers.canned;

import com.sportradar.api.replay.apiserver.ApiHandlerConfig;


class FileBackedHandler extends AbstractCannedResponseApiHandler {

  private final String filepath;

  public FileBackedHandler(ApiHandlerConfig config, String filepath) {
    super(config);
    this.filepath = filepath;
  }

  @Override
  String createXml() {
    return new FileLoader().load(filepath);
  }
}