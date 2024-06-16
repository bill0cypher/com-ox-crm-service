package com.ox.crm.core.properties;

import java.util.List;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {
  @NestedConfigurationProperty
  private Cors cors;
  @NestedConfigurationProperty
  private Security security;

  @Data
  public static class Cors {
    private List<String> allowedOrigins;
    private List<String> allowedMethods;
    private List<String> allowedHeaders;
  }

  @Data
  public static class Security {
    private String secretKey;
    private long jwtExpirationInMs;
  }
}
