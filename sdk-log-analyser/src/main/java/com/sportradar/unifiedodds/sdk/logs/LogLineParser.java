package com.sportradar.unifiedodds.sdk.logs;

import com.sportradar.unifiedodds.sdk.logs.config.ConfigModel;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
class LogLineParser implements Consumer<LineFileModel> {

  static final Pattern REGEX_PATTERN_XML = Pattern.compile(".*response.*:\\s+(<.*)");

  static final DateFormat LOG_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS",
          Locale.ENGLISH);
  private final ApiLogListener listener;

  private final ConfigModel configModel;

  private boolean parsingException;

  //  private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
  private SimpleDateFormat dateTimeFormat = null;
  private TreeMap<String, LogEntry> eventMap = new TreeMap<>();

  private int count = 0;

  private static String PRODUCER_DOWN = "ProducerDown";
  private static String REQUEST = "Request";
  private static String FEED_EXCEPTION = "FeedException";
  private static String FEED_MESSAGE = "FeedMessage";

  private Date lastExceptionTime = null;
  private LogEntry lastEntry = null;

  private int exceptionCount = 0;
  private int channelRecoveryExceptionCount = 0;
  private int connectionRecoveryExceptionCount = 0;
  private int topologyRecoveryExceptionCount = 0;
  private int connectionDriverExceptionCount = 0;

  @Getter
  @Setter
  private Set<String> uniqueUrls = new TreeSet<>();

  public SimpleDateFormat getDateTimeFormat() {
    return dateTimeFormat == null ? dateTimeFormat = new SimpleDateFormat(configModel.getDateTimeFormat()) : dateTimeFormat;
  }

  @Override
  public void accept(LineFileModel lineFileModel) {
    parse(lineFileModel.getLine(), lineFileModel.getFileName());
  }

  private void parse(String logLine, String fileName) {
    count++;

//    if (count > 10000) {
//      throw new RuntimeException("Ekkk");
//    }


    String[] bits = logLine.split(" ");
    if (bits.length > 3) {
      String dateTimeStr = bits[0] + " " + bits[1];
      try {
        Date logTimestamp = getDateTimeFormat().parse(dateTimeStr);

        // Collect actual message
//        String message = logLine.substring(logLine.indexOf(":", 50), logLine.length() - 1);
        String message = logLine;

        // if (logLine.contains("amqp:")) {
        if (logLine.contains("SDKExceptionHandler")) {
          exceptionCount++;

          if (logLine.contains("Channel recovery")) {
//            if (lastEntry != null) {
//              lastEntry.incrementChannelRecoveryExceptionCount();
//            }
            channelRecoveryExceptionCount++;
          } else if (logLine.contains("Unexpected connection driver exception")) {
//            if (lastEntry != null) {
//              lastEntry.incrementConnectionDriverExceptionCount();
//            }
            connectionDriverExceptionCount++;
          } else if (logLine.contains("Topology recovery exception")) {
//            if (lastEntry != null) {
//              lastEntry.incrementTopologyRecoveryExceptionCount();
//            }
            topologyRecoveryExceptionCount++;
          } else if (logLine.contains("Connection Recovery")) {
//            if (lastEntry != null) {
//              lastEntry.incrementConnectionRecoveryExceptionCount();
//            }
            connectionRecoveryExceptionCount++;
          }

          System.out.println(logLine);

        }


        LogEntryModel logEntryModel = buildLogEntryModel(logLine, fileName);
        if (logEntryModel != null && logEntryModel.isApiCall()) {

          if (uniqueUrls.contains(logEntryModel.getUrl())) {
            // System.out.println(logEntryModel.getTimestamp() + " Duplicate URL : " + logEntryModel.getUrl());
          } else {
            uniqueUrls.add(logEntryModel.getUrl());
          }

          if (logEntryModel.isMqThread()) {
            System.out.println(logEntryModel.getTimestamp() + " MQ THREAD : " + logEntryModel.getUrl());
          }

          LogEntry logEntry = new LogEntry();
          logEntry.setLogEntryModel(logEntryModel);
          logEntry.setTimestamp(logTimestamp);
          logEntry.setType(LogEntryType.SAPI_REQUEST);

          int index = logLine.indexOf("response");
          if (index > 0) {
            int startIndex = logLine.indexOf("(", index);
            int endIndex = logLine.indexOf(")", index);
            try {
              String durationMillisStr = logLine.substring(startIndex + 1, endIndex);
              boolean isMillis = durationMillisStr.endsWith("ms");
              durationMillisStr = durationMillisStr.replaceAll("[^0-9.]", "");

              NumberFormat nf = NumberFormat.getNumberInstance(Locale.UK);
              Number n = nf.parse(durationMillisStr);
              if (isMillis) {
                int value = n.intValue();
                logEntry.setDurationMillis(n.intValue());
              } else {
                int value = (int) (n.doubleValue() * 1000);
                logEntry.setDurationMillis(value);
              }
            } catch (StringIndexOutOfBoundsException | NumberFormatException nfe) {
              log.error(logLine);
              nfe.printStackTrace();
            }
            eventMap.put(dateTimeStr, logEntry);
          } else {
            System.err.println(logLine);
          }
        } else if (logEntryModel != null && logEntryModel.isCustomerMessageListenerCall()) {
          LogEntry logEntry = new LogEntry();
          logEntry.setTimestamp(logTimestamp);
          logEntry.setType(LogEntryType.CUSTOMER_MESSAGE_PROCESSING);
          logEntry.setRabbitMessageType(logEntryModel.getRabbitMessageType());
          logEntry.setLogEntryModel(logEntryModel);

          if (logEntryModel.getRabbitMessageType() == null) {
            System.out.println(logEntryModel.getPayload());
          }

          logEntry.setDurationMillis(logEntryModel.getDurationMillis());
          logEntry.setMessageDelayMillis(logEntryModel.getMessageDelayMillis());
          logEntry.setMessageCreationTimestamp(logEntryModel.getMessageCreationTimestamp());
          eventMap.put(dateTimeStr, logEntry);
        } else if (logLine.contains("ProducerDown")) {
          if (logLine.contains("ProducerDown:AliveIntervalViolation -> Changing producer down reason")) {
            // Skip as it is spurious
            return;
          }
          // Within a second??
          if (lastExceptionTime != null && (logTimestamp.getTime() - lastExceptionTime.getTime()) < 1000) {
            System.out.println("Within ONE second");
            if (lastEntry != null) {
              //lastEntry.setExceptionCount(lastEntry.getExceptionCount() + 1);
            }

          } else {
            LogEntry logEntry = new LogEntry();
            logEntry.setLogEntryModel(logEntryModel);
            logEntry.setTimestamp(logTimestamp);
            logEntry.setType(LogEntryType.PRODUCER_DOWN);
            logEntry.setExceptionCount(exceptionCount);
            logEntry.setConnectionDriverExceptionCount(connectionDriverExceptionCount);
            logEntry.setConnectionRecoveryExceptionCount(connectionRecoveryExceptionCount);
            logEntry.setChannelRecoveryExceptionCount(channelRecoveryExceptionCount);
            logEntry.setTopologyRecoveryExceptionCount(topologyRecoveryExceptionCount);
            exceptionCount = 0;
            connectionDriverExceptionCount = 0;
            channelRecoveryExceptionCount = 0;
            connectionRecoveryExceptionCount = 0;
            topologyRecoveryExceptionCount = 0;

            eventMap.put(dateTimeStr, logEntry);
            lastEntry = logEntry;
          }

          lastExceptionTime = logTimestamp;

          if (message.contains("Producer[4 BetPal]")) {
            lastEntry.getProducers().add("4 BetPal");
          } else if (message.contains("Producer[1 LO]")) {
            lastEntry.getProducers().add("1 LO");
          } else if (message.contains("Producer[3 Ctrl]")) {
            lastEntry.getProducers().add("3 Ctrl");
          }

          if (logLine.contains("AliveIntervalViolation")) {
            lastEntry.getReasons().add("AliveIntervalViolation");
          } else if (logLine.contains("ProcessingQueueDelayViolation")) {
            lastEntry.getReasons().add("ProcessingQueueDelayViolation");
          }
        }
      } catch (ParseException e) {
        // System.err.println("Unparseable line");
        System.out.println(logLine);
      }
    }

    //if (logLine.contains("AliveIntervalViolation") || logLine.contains("ProcessingQueueDelayViolation")) {
    if (logLine.contains("ProducerDown")) {
      System.out.println(logLine);
    }
  }

  private String extractXml(String logLine) {
    String xml;

    Matcher matcher = REGEX_PATTERN_XML.matcher(logLine);
    if (matcher.find()) {
      xml = matcher.group(1);
    } else {
      throw new LogParserException("XML payload not found:" + logLine);
    }
    return xml;
  }

  private Date toDateTime(String timestamp) {
    try {
      return LOG_DATE_FORMAT.parse(timestamp);
    } catch (ParseException e) {
      throw new LogParserException("Error parsing timestamp: " + timestamp, e);
    }
  }

  public void dumpExceptionDataAsConfluenceTable() {
    System.out.println("||Time||Producers||Exceptions||||Reasons||");

//    eventMap.forEach((key, value) -> System.out.println("|" + key + "|" + value.getProducers() + "|" + value.getExceptionCount() + " " + value.getExceptionDetails() + "|" + value.getReasons() + "|"));
    for (LogEntry value : eventMap.values()) {
      if (value.getType() == LogEntryType.PRODUCER_DOWN) {
        System.out.println("|" + value.getTimestamp() + "|" + value.getProducers() + "|" + value.getExceptionCount() + " " + value.getExceptionDetails() + "|" + value.getReasons() + "|");
      }
    }

    System.out.println("Count : " + eventMap.size());
  }

  public void writeToInfux() {

  }

  public TreeMap<String, LogEntry> getEventMap() {
    return eventMap;
  }

  public LogEntryModel buildLogEntryModel(String line, String fileName) {
    LogEntryModel logEntryModel = new LogEntryModel();
    logEntryModel.setLine(line);
    try {


      int index = line.indexOf(":", 50);

      if (index < 0) {
        log.warn(line);
        return null;
      }

      String firstBit = line.substring(0, index);

      // Sort out [INFO ]
      firstBit = firstBit.replaceAll("\\[INFO \\]", "INFO");

      String[] bits = cleanBits(firstBit.split(" "));
      String lastBit = line.substring(index + 1, line.length());
      lastBit = lastBit.trim();

      String dateTimeStr = bits[0] + " " + bits[1];
      try {
        logEntryModel.setTimestamp(getDateTimeFormat().parse(dateTimeStr));
      } catch (ParseException e) {
        log.error("Unable to parse timestamp : " + dateTimeStr);
      }

      // Trim brackets of thread name
      if (configModel.getLogOffsetThreadName() != null) {
        String threadName = bits[configModel.getLogOffsetThreadName()];
        if (threadName.startsWith("[")) threadName = threadName.substring(1, threadName.length());
        if (threadName.endsWith("]")) threadName = threadName.substring(0, threadName.length() - 1);
        logEntryModel.setThreadName(threadName);
      }

      // Logger name
      if (configModel.getLogOffsetLoggerName() != null) {
        logEntryModel.setLoggerName(bits[configModel.getLogOffsetLoggerName()]);
      }

      logEntryModel.setPayload(lastBit);

      if (logEntryModel.isApiCall() || isApiCall(fileName)) {
        int urlIndex = line.indexOf("https://");
        if (urlIndex < 0) {
          urlIndex = line.indexOf("http://");
        }

        // Sometimes the line has no further info
        int endIndex = line.indexOf(",", urlIndex);
        if (endIndex <= 0) {
          endIndex = line.length();
        }

        String url = line.substring(urlIndex, endIndex);
        url = url.trim();
        logEntryModel.setUrl(url);
      } else if (logEntryModel.isCustomerMessageListenerCall()) {
        if (lastBit.contains("|UFOddsChange|")) {
          logEntryModel.setRabbitMessageType("odds_change");
        } else if (lastBit.contains("|UFFixtureChange|")) {
          logEntryModel.setRabbitMessageType("fixture_change");
        } else if (lastBit.contains("|UFAlive|")) {
          logEntryModel.setRabbitMessageType("alive");
        } else if (lastBit.contains("|UFBetStop|")) {
          logEntryModel.setRabbitMessageType("bet_stop");
        } else if (lastBit.contains("|UFBetCancel|")) {
          logEntryModel.setRabbitMessageType("bet_cancel");
        } else if (lastBit.contains("|UFRollbackBetCancel|")) {
          logEntryModel.setRabbitMessageType("rollback_bet_cancel");
        } else if (lastBit.contains("|UFBetSettlement|")) {
          logEntryModel.setRabbitMessageType("bet_settlement");
        } else if (lastBit.contains("|UFRollbackBetSettlement|")) {
          logEntryModel.setRabbitMessageType("rollback_bet_settlement");
        } else if (lastBit.contains("|UFSnapshotComplete|")) {
          logEntryModel.setRabbitMessageType("snapshot_complete");
        }

        index = lastBit.lastIndexOf("|");
        if (index > 0) {
          try {
            String messageCreationTimestamp = lastBit.substring(index + 1, lastBit.lastIndexOf(")"));
            logEntryModel.setMessageCreationTimestamp(new Date(Long.parseLong(messageCreationTimestamp)));
          } catch (Exception e) {
            log.error(line, e);
          }
        } else {
          log.error(line);
          return null;
        }

        index = lastBit.indexOf("duration");
        if (index > 0) {
          String durationMillisStr = lastBit.substring(index, lastBit.length());
          durationMillisStr = durationMillisStr.replaceAll("[^0-9]", "");
          try {
            logEntryModel.setDurationMillis(Integer.parseInt(durationMillisStr));
          } catch (NumberFormatException nfe) {
            log.error(line, nfe);
          }
        }

      }

      return logEntryModel;
    } catch (Exception e) {
      log.warn(line);
      e.printStackTrace();
      throw e;
    }
  }

  private boolean isApiCall(String fileName) {
    if (fileName != null && fileName.contains("rest-traffic")) {
      return true;
    }

    return false;
  }

  private String[] cleanBits(String[] bits) {
    final List<String> tmpList = new LinkedList<>();

    for (String bit : bits) {
      if (bit != null && bit.length() > 0) {
        tmpList.add(bit);
      }
    }

    return tmpList.toArray(new String[]{});
  }


  public static void main(String[] args) {
  }
}
