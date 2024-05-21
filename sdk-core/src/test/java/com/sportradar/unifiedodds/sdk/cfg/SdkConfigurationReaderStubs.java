/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.cfg;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.utils.domain.config.Tokens;
import com.sportradar.utils.domain.names.Languages;
import java.util.Optional;
import lombok.val;

class SdkConfigurationReaderStubs {

    public static class Props {

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {

            private Optional<Environment> environment = Optional.empty();

            @SuppressWarnings("HiddenField")
            public Builder with(Environment environment) {
                this.environment = Optional.of(environment);
                return this;
            }

            public SdkConfigurationPropertiesReader build() {
                val reader = mock(SdkConfigurationPropertiesReader.class);
                when(reader.readEnvironment()).thenReturn(environment.orElse(Environments.any()));
                when(reader.readAccessToken()).thenReturn(Optional.of(Tokens.any()));
                when(reader.readDefaultLanguage()).thenReturn(Optional.of(Languages.any()));
                return reader;
            }
        }
    }

    public static class Yaml {

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {

            private Optional<Environment> environment = Optional.empty();

            @SuppressWarnings("HiddenField")
            public Builder with(Environment environment) {
                this.environment = Optional.of(environment);
                return this;
            }

            public SdkConfigurationYamlReader build() {
                val reader = mock(SdkConfigurationYamlReader.class);
                when(reader.readEnvironment()).thenReturn(environment.orElse(Environments.any()));
                when(reader.readAccessToken()).thenReturn(Optional.of(Tokens.any()));
                when(reader.readDefaultLanguage()).thenReturn(Optional.of(Languages.any()));
                return reader;
            }
        }
    }
}
