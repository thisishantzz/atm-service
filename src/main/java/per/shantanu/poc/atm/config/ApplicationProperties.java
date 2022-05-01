package per.shantanu.poc.atm.config;

import java.time.ZoneId;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Slf4j
@ConfigurationProperties(prefix = ApplicationProperties.PREFIX)
public class ApplicationProperties {

  public static final String PREFIX = "app";

  public enum DB {
    DYNAMODB;
  }

  /** The database that the application is supposed to use. Default is {@link DB#DYNAMODB}. */
  private DB db = DB.DYNAMODB;

  /**
   * The timezone the application is supposed to operate in. Default is <code>America/Los_Angeles
   * </code>.
   */
  private ZoneId timezone = ZoneId.of("America/Los_Angeles");

  public void setDb(DB db) {
    this.db = db;
  }

  public void setTimezone(ZoneId timezone) {
    this.timezone = timezone;
  }
}
