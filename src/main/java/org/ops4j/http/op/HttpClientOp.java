package org.ops4j.http.op;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ops4j.OpData;
import org.ops4j.base.BaseOp;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;

import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@AutoService(Op.class)
@Command(name = "http-client", description = "An http client.")
public class HttpClientOp extends BaseOp<HttpClientOp>
{
  @Parameters(index = "0", arity = "0..*",
      description = "The headers in name=value format.")
  private @Getter @Setter Map<String, String> headers = new HashMap<>();

  @Option(names = { "--url" },
      description = "The url.  DEFAULT='${DEFAULT-VALUE}'")
  public @Getter @Setter String               url     = "/ops";

  @Option(names = { "--token", "-t" }, description = "The api token.")
  public @Getter @Setter String               token   = null;

  public HttpClientOp()
  {
    super("http-client");
  }

  public HttpClientOp initialize() throws OpsException
  {
    HttpClient client = HttpClient.newHttpClient();

    // Define the request URI for character listing
    URI uri;
    try
    {
      uri = new URI(getUrl());

      HttpRequest request;

      if (getToken() != null)
      {
        // Create the HTTP GET request with OAuth token in header
        request = HttpRequest.newBuilder().uri(uri)
            .header("Authorization", "Bearer " + getToken()).build();
      }
      else
      {
        request = HttpRequest.newBuilder().uri(uri).build();
      }
      // Send the request and get the response
      HttpResponse<String> response = client.send(request,
          HttpResponse.BodyHandlers.ofString());

      // Print the response
      System.out.println(response.body());
    }
    catch(URISyntaxException | IOException | InterruptedException ex)
    {
      throw new OpsException(ex);
    }

    return this;
  }

  public HttpClientOp open() throws OpsException
  {
    return this;
  }

  public List<OpData> execute(OpData input)
  {
    return input.asList();
  }

  public List<OpData> close() throws OpsException
  {
    return OpData.emptyList();
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new HttpClientOp(), args);
  }
}
