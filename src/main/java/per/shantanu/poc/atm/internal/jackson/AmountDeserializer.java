package per.shantanu.poc.atm.internal.jackson;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import lombok.NonNull;
import per.shantanu.poc.atm.handlers.ATMServiceController;

public class AmountDeserializer extends JsonDeserializer<ATMServiceController.Amount> {

  @Override
  public ATMServiceController.Amount deserialize(@NonNull JsonParser p, DeserializationContext ctxt)
      throws IOException, JacksonException {
    final JsonNode node = p.getCodec().readTree(p);

    return new ATMServiceController.Amount(node.get("amount").asDouble());
  }
}
