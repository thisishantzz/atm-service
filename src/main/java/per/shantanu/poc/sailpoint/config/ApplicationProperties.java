package per.shantanu.poc.sailpoint.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.ZoneId;

@Getter
@Slf4j
@ConfigurationProperties(prefix = ApplicationProperties.PREFIX)
public class ApplicationProperties {

    public static final String PREFIX = "app";

    public enum DB {
        DYNAMODB;
    }

    /**
     * <p>The database that the application is supposed to use. Default is {@link DB#DYNAMODB}.</p>
     */
    private DB db = DB.DYNAMODB;

    /**
     * <p>The timezone the application is supposed to operate in. Default is <code>America/Los_Angeles</code>.</p>
     */
    private ZoneId timezone = ZoneId.of("America/Los_Angeles");

    public void setDb(DB db) {
        this.db = db;
    }

    public void setTimezone(ZoneId timezone) {
        this.timezone = timezone;
    }
}
