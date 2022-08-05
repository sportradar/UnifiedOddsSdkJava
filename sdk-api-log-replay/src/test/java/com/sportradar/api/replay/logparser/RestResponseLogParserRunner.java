package com.sportradar.api.replay.logparser;

import java.util.regex.Matcher;

public class RestResponseLogParserRunner {

  public static void main(String[] args) {
    String logGet = "2022-04-16 13:34:26,876 [INFO ] [] [main] Request[DataFetcher]: https://global.stgapi.betradar.com/v1/users/whoami.xml, response - OK(2.981 s): <?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><bookmaker_details response_code=\"OK\" expire_at=\"2023-02-28T13:16:44Z\" bookmaker_id=\"16281\" virtual_host=\"/unifiedfeed/16281\"/>";
    String logPost = "2022-04-16 13:35:09,002 [INFO ] [uf-sdk-16281-1] [uf-sdk-16281-1-t-0] Request[POST]: https://stgapi.betradar.com/v1/pre/recovery/initiate_request?node_id=1&request_id=5581, response code - OK[202](99.45 ms): <?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><response response_code=\"ACCEPTED\"><action>Request id: 5581 for PRE odds from bookmaker: 16281 received</action></response>";
    String logGetError =
        "2022-04-16 13:35:08,902 [INFO ] [uf-sdk-16281-1] [uf-sdk-16281-1-t-0] Request[DataFetcher]: https://global.stgapi.betradar.com/v1/sports/en/schedules/2022-04-16/schedule.xml, response - FAILED(32.03 s), ex:\n"
            + "com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException: There was a problem retrieving the requested data\n"
            + "\tat com.sportradar.unifiedodds.sdk.impl.HttpDataFetcher.send(HttpDataFetcher.java:119)\n";

    System.out.println("GET example:");
    dump(logGet);
    System.out.println();
    System.out.println("POST example:");
    dump(logPost);
    System.out.println();
    System.out.println("GET example (error):");
    dump(logGetError);
  }

  private static void dump(String xml) {
    Matcher matcher = ApiLogLineParser.REGEX_PATTERN_API_CALL.matcher(xml);
    if (matcher.find()) {
      System.out.println("1: '" + matcher.group(1) + "'"); // timestamp
      System.out.println("2: '" + matcher.group(2) + "'"); // log level
      System.out.println("3: '" + matcher.group(3) + "'"); // SDK thread
      System.out.println("4: '" + matcher.group(4) + "'"); // AMQP thread
      System.out.println("5: '" + matcher.group(5) + "'"); // Requester (or POST)
      System.out.println("6: '" + matcher.group(6) + "'"); // URL
      System.out.println(
          "7: '" + matcher.group(7) + "'"); // response, timing (and response code if POST)
    }
  }
}
