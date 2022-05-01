package per.shantanu.poc.sailpoint.internal.jackson;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import lombok.NonNull;
import per.shantanu.poc.sailpoint.entities.Credentials;

public class CredentialDeserializer extends JsonDeserializer<Credentials> {

  @Override
  public Credentials deserialize(@NonNull JsonParser p, DeserializationContext ctxt)
      throws IOException, JacksonException {
    final JsonNode node = p.getCodec().readTree(p);

    return Credentials.create(node.get("customerID").asText(), node.get("pin").asText());
  }
}
