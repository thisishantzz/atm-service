package per.shantanu.poc.sailpoint.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import software.amazon.awssdk.regions.Region;

import java.net.URI;

@Getter
@Slf4j
@ConfigurationProperties(prefix = DynamodbProperties.PREFIX)
public class DynamodbProperties {

    public static final String PREFIX = "db.dynamodb";

    /**
     * <p>The DynamoDB endpoint to connect to</p>
     */
    private URI endpoint;

    /**
     * <p>The region where the table should exist. Default is <code>us-west-2</code>.</p>
     */
    private Region region = Region.US_WEST_2;

    /**
     * <p>The access key to use to connect to the dynamodb table</p>
     */
    private String accessKey;

    /**
     * <p>The secret key to use to connect to the dynamodb table</p>
     */
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
