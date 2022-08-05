package com.sportradar.unifiedodds.sdk.logs;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.sportradar.unifiedodds.sdk.logs.config.ConfigModel;
import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class WriteToInflux {
  private final ConfigModel configModel;

  public static void main(String[] args) {
    String token = "myadmintoken";
    String url = "http://localhost:8086";
    String bucket = "mybucket";
    String org = "e0b0bca0af5dcd86";
    InfluxDBClient influxDBClient = InfluxDBClientFactory.create(url, token.toCharArray(), org, bucket);


  }

  public List<Point> write(Collection<LogEntry> les) throws ParseException {
    String token = "myadmintoken";
    String url = "http://localhost:8086";
    String bucket = "mybucket";
    //String bucket = configModel.getCustomer();
    String org = "e0b0bca0af5dcd86";
    InfluxDBClient influxDBClient = InfluxDBClientFactory.create(url, token.toCharArray(), org, bucket);
    WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();

    Instant nowUtc = Instant.now();
    ZoneId zone = ZoneId.systemDefault();//ZoneId.of(configModel.getTimezone());
    ZonedDateTime nowAsiaSingapore = ZonedDateTime.ofInstant(nowUtc, zone);
    int offset = nowAsiaSingapore.getOffset().getTotalSeconds();

    List<Point> points = new ArrayList<Point>();

    log.info("Found : " + les.size() + " points");

    for (LogEntry le : les) {
      if (le.getType() == LogEntryType.PRODUCER_DOWN) {
        Point point = Point.measurement("producer_down").addTag("customer", configModel.getCustomer()).addField("count", 1).addField("exception_count", le.getExceptionCount()).addTag("message_type", le.getType().name()).time(le.getTimestamp().getTime(), WritePrecision.MS);
        points.add(point);
      } else if (le.getType() == LogEntryType.CUSTOMER_MESSAGE_PROCESSING) {
        Point point = Point.measurement("customer_processing").addTag("customer", configModel.getCustomer()).addField("count", 1).addField("message_delay", le.getMessageDelayMillis()).addField("duration_millis", le.getDurationMillis()).addTag("message_type", le.getRabbitMessageType()).time(le.getTimestamp().getTime(), WritePrecision.MS);
        points.add(point);

        // Convert the time to be relative to the TZ of the jvm running this class
        long localTime = le.getMessageCreationTimestamp().getTime() + offset * 1000;
        Date newDate = new Date(localTime);

        //        point = Point.measurement("rabbit_message").addTag("customer", configModel.getCustomer()).addField("count", 1).addField("message_delay", le.getMessageDelayMillis()).addField("duration_millis", le.getDurationMillis()).addTag("message_type", le.getRabbitMessageType()).time(le.getMessageCreationTimestamp().getTime(), WritePrecision.MS);
        System.out.println("Message duration (ms) : " + le.getDurationMillis() + ", Delay : " + le.getMessageDelayMillis());
        point = Point.measurement("rabbit_message").addTag("customer", configModel.getCustomer()).addField("count", 1).addField("message_delay", le.getMessageDelayMillis()).addField("duration_millis", le.getDurationMillis()).addTag("message_type", le.getRabbitMessageType()).time(newDate.getTime(), WritePrecision.MS);
        points.add(point);
      } else if (le.getType() == LogEntryType.SAPI_REQUEST) {
        Point point = Point.measurement("sapi_request").addTag("customer", configModel.getCustomer()).addField("count", 1).addField("duration_millis", le.getDurationMillis()).addTag("message_type", le.getType().name()).addTag("sapi_type", le.getLogEntryModel().getRestApiType()).time(le.getTimestamp().getTime(), WritePrecision.MS);
        points.add(point);

        if (le.getLogEntryModel().isMqThread()) {
          if (le.getLogEntryModel().getRestApiType() == null || le.getLogEntryModel().getRestApiType().isEmpty()) {
            System.out.println(le.getLogEntryModel().getUrl());
          }
          point = Point.measurement("mq_thread_api_call").addTag("customer", configModel.getCustomer()).addField("count", 1).addField("duration_millis", le.getDurationMillis()).addTag("sapi_type", le.getLogEntryModel().getRestApiType()).time(le.getTimestamp().getTime(), WritePrecision.MS);
          points.add(point);
        }
      }
    }

    log.info("Wrote : " + points.size() + " points");

    writeApi.writePoints(points);

    return points;
  }
}
