package per.shantanu.poc.atm.entities;

import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
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
}
