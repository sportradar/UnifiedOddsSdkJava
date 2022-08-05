package com.sportradar.api.replay.apiserver.handlers.canned;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
class TemplateFactory {

  private final Configuration config = new FreemarkerConfigurationFactory().create();

  public String descriptionsBetStopReasons(Map<String, Object> input) {
    return render("descriptions/betstop_reasons.ftl", input);
  }

  public String descriptionsBettingStatus(Map<String, Object> input) {
    return render("descriptions/betting_status.ftl", input);
  }

  public String descriptionsMarkets(Map<String, Object> input) {
    return render("descriptions/markets.ftl", input);
  }

  public String descriptionsVariants(Map<String, Object> input) {
    return render("descriptions/variants.ftl", input);
  }

  public String descriptionsMarketsVariants(Map<String, Object> input) {
    return render("descriptions/markets/variants.ftl", input);
  }

  public String sportsEventsSummary(Map<String, Object> input) {
    return render("sports/sport_events/summary.ftl", input);
  }

  public String sportsEventsFixtureChangeFixture(Map<String, Object> input) {
    return render("sports/sport_events/fixture_change_fixture.ftl", input);
  }

  public String sportsSchedulesSchedule(Map<String, Object> input) {
    return render("sports/schedules/schedule.ftl", input);
  }

  public String sportsPlayersProfile(Map<String, Object> input) {
    return render("sports/players/profile.ftl", input);
  }

  public String productRecoveryInitiate(Map<String, Object> input) {
    return render("recovery/initiate_request.ftl", input);
  }

  private String render(String templatePath, Map<String, Object> input) {
    try {
      Template template = config.getTemplate(templatePath);
      StringWriter stringWriter = new StringWriter();
      template.process(input, stringWriter);
      return stringWriter.toString();
    } catch (IOException | TemplateException e) {
      log.error("Error populating Freemarker template!", e);
      throw new RuntimeException("Error populating Freemarker template!");
    }
  }

  public static void main(String[] args) throws Exception {
    TemplateFactory templateFactory = new TemplateFactory();
    Map<String, Object> input = new HashMap<>();

//    input.put("matchUrn", "sr:match:33176369");
//    System.out.println(templateFactory.sportsEventsSummary(input));

//    System.out.println(templateFactory.descriptionsBetStopReasons(input));
//    System.out.println(templateFactory.descriptionsBettingStatus(input));
//    System.out.println(templateFactory.descriptionsMarkets(input));
//    System.out.println(templateFactory.descriptionsVariants(input));
//    System.out.println(templateFactory.descriptionsMarketsVariants(input));
//    System.out.println(templateFactory.sportsSchedulesSchedule(input));
//    System.out.println(templateFactory.sportsPlayersProfile(input));

//    input.put("requestID", "666");
//    input.put("bookmakerID", "12345");
//    System.out.println(templateFactory.productRecoveryInitiate(input));

    System.out.println(templateFactory.sportsEventsFixtureChangeFixture(input));
  }
}
