package com.sportradar.unifiedodds.sdk.logs.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.Assert;
import org.junit.Test;

public class ConfigModelTest {
  @Test
  public void load() throws Exception {
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    ConfigModel configModel = mapper.readValue(ConfigModelTest.class.getResourceAsStream("/customer.yaml"), ConfigModel.class);

    Assert.assertEquals("eurobet", configModel.getCustomer());
    Assert.assertEquals(null, configModel.getLogOffsetLoggerName());
  }
}
