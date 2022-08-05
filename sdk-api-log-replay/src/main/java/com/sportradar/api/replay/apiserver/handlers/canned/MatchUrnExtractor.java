package com.sportradar.api.replay.apiserver.handlers.canned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class MatchUrnExtractor {

  private static final Pattern REGEX_MATCH_URN = Pattern.compile(".*(sr:match:\\d+)");

  public String extract(String url) {
    Matcher matcher = REGEX_MATCH_URN.matcher(url);
    if (matcher.find()) {
      return matcher.group(1);
    } else {
      return "sr:match:UNKNOWN";
    }
  }
}
