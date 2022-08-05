package com.sportradar.unifiedodds.sdk.integration.fixtures.environment;

import com.sportradar.unifiedodds.sdk.impl.EnvironmentManager;
import com.sportradar.unifiedodds.sdk.impl.EnvironmentSetting;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnvironmentManagerFixture {

  private static final Logger log = LoggerFactory.getLogger(EnvironmentManagerFixture.class);
  private final List<EnvironmentSetting> environmentSettings;

  public EnvironmentManagerFixture() {
    environmentSettings = extractEnvironmentSettingList();
  }

  public void setApiServerPort(int wiremockPort) {
    overrideAllApiHosts(wiremockPort);
  }

  private void overrideAllApiHosts(int wiremockPort) {
    for (int i = 0; i < environmentSettings.size(); i++) {
      EnvironmentSetting environmentSetting = environmentSettings.remove(i);
      EnvironmentSetting wiremockEnvironmentSetting = createWiremockEnvironmentSetting(
          environmentSetting, wiremockPort);
      environmentSettings.add(i, wiremockEnvironmentSetting);
    }
    log.info("Updated all api config to localhost:{}", wiremockPort);
  }

  private List<EnvironmentSetting> extractEnvironmentSettingList() {
    List<EnvironmentSetting> actualField = new ArrayList<>();
    try {
      Field environmentSettings = EnvironmentManager.class.getDeclaredField("environmentSettings");
      environmentSettings.setAccessible(true);
      actualField = (List<EnvironmentSetting>) environmentSettings.get(
          actualField);
    } catch (NoSuchFieldException e) {
      log.error("List<EnvironmentSetting> not found in EnvironmentManager!", e);
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      log.error("Error accessing List<EnvironmentSetting>!", e);
      throw new RuntimeException(e);
    }
    return actualField;
  }

  private EnvironmentSetting createWiremockEnvironmentSetting(EnvironmentSetting environmentSetting,
      int wiremockPort) {
    boolean noSSL = false;

    EnvironmentSetting wiremockSetting = new EnvironmentSetting(
        environmentSetting.getEnvironment(),
        environmentSetting.getMqHost(),
        "localhost", // override the apiHost to our wiremock
        wiremockPort, // override the apiPort to our wiremock
        noSSL, // disable SSL
        environmentSetting.getEnvironmentRetryList());
    return wiremockSetting;
  }
}
