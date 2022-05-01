package per.shantanu.poc.sailpoint.errors;

public class ResourceNotFoundError extends Exception {

  private static final String MSG_TEMPLATE = "%s(%s) : Resource not found";

  public ResourceNotFoundError(String resource, String id) {
    super(String.format(MSG_TEMPLATE, resource, id));
  }
}
