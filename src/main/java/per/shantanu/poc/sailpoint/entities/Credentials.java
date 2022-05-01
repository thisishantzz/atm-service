package per.shantanu.poc.sailpoint.entities;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.IOException;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@JsonDeserialize(using = Credentials.CredentialsDeserializer.class)
public class Credentials {

  private final String customerID;

  private final String pin;

  public static @NonNull Credentials create(String customerID, String pin) {
    if (customerID == null || customerID.isEmpty()) {
      throw new IllegalArgumentException("customer id cannot be null/empty");
    }
    if (pin == null || pin.isEmpty()) {
      throw new IllegalArgumentException("pin cannot be null/empty");
    }

    final Pattern pinPatt = Pattern.compile("^\\d{4}$");
    if (!pinPatt.matcher(pin).matches()) {
      throw new IllegalArgumentException("Invalid pin. It should be a 4 digit integer");
    }

    return new Credentials(customerID, pin);
  }

  public static final class CredentialsDeserializer extends JsonDeserializer<Credentials> {

    @Override
    @NonNull
    public Credentials deserialize(@NonNull JsonParser p, DeserializationContext ctxt)
        throws IOException {
      final JsonNode node = p.getCodec().readTree(p);

      return Credentials.create(node.get("customerID").asText(), node.get("pin").asText());
    }
  }
}
