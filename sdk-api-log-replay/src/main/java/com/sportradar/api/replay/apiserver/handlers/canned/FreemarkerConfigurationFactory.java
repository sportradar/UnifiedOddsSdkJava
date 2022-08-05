package com.sportradar.api.replay.apiserver.handlers.canned;

import freemarker.template.Configuration;
import freemarker.template.Version;
import java.util.Locale;

class FreemarkerConfigurationFactory {

  Configuration create() {
    Configuration config = new Configuration(new Version(2, 3, 31));
    config.setDefaultEncoding("UTF-8");
    config.setLocale(Locale.UK);
    config.setClassForTemplateLoading(this.getClass(), "/templates/");
    return config;
  }
}
