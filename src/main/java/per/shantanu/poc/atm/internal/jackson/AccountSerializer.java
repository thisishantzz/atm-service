package per.shantanu.poc.atm.internal.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import lombok.NonNull;
import per.shantanu.poc.atm.entities.Account;

public class AccountSerializer extends JsonSerializer<Account> {

  @Override
  public void serialize(
      @NonNull Account account, @NonNull JsonGenerator gen, SerializerProvider serializers)
      throws IOException {
    gen.writeStartObject();

    gen.writeStringField("accountNumber", account.accountNumber());
    gen.writeStringField("customerID", account.customerID());
    gen.writeNumberField("balance", account.balance());

    gen.writeEndObject();
  }
}
