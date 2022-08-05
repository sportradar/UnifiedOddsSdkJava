package com.sportradar.unifiedodds.sdk.logs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class LineFileModel {
  private final String line;
  private final String fileName;
}
