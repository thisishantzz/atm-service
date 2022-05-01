package per.shantanu.poc.atm.config;

import java.net.URI;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import software.amazon.awssdk.regions.Region;

@Getter
@Slf4j
@ConfigurationProperties(prefix = DynamodbProperties.PREFIX)
public class DynamodbProperties {

  public static final String PREFIX = "db.dynamodb";

  /** The DynamoDB endpoint to connect to */
  private URI endpoint;

  /** The region where the table should exist. Default is <code>us-west-2</code>. */
  private Region region = Region.US_WEST_2;

  /** The access key to use to connect to the dynamodb table */
  private String accessKey;

  /** The secret key to use to connect to the dynamodb table */
  private String secretKey;

  public void setEndpoint(URI endpoint) {
    this.endpoint = endpoint;
  }

  public void setRegion(Region region) {
    this.region = region;
  }

  public void setAccessKey(String accessKey) {
    this.accessKey = accessKey;
  }

  public void setSecretKey(String secretKey) {
    this.secretKey = secretKey;
  }
}
