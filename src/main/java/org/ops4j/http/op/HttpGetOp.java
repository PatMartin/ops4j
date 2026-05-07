package org.ops4j.http.op;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ops4j.OpData;
import org.ops4j.base.BaseOp;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.JsonSource;
import org.ops4j.inf.Op;
import org.ops4j.util.JacksonUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@AutoService(Op.class)
@Command(name = "http-get", description = "An http get client.")
public class HttpGetOp extends BaseOp<HttpGetOp> implements JsonSource
{
  @Parameters(index = "0", arity = "0..*",
      description = "0 or more headers in name=value format.")
  private @Getter @Setter Map<String, String> headers = new HashMap<>();

  @Option(names = { "--url" }, description = "The url.")
  public @Getter @Setter String               url     = null;

  private Iterator<JsonNode>     it        = null;
  
  public HttpGetOp()
  {
    super("http-get");
  }

  public HttpGetOp initialize() throws OpsException
  {
    HttpClient client = HttpClient.newHttpClient();

    // Define the request URI for character listing
    URI uri;
    try
    {
      uri = new URI(getUrl());

      HttpRequest request = HttpRequest.newBuilder().uri(uri).build();
      
      // Send the request and get the response
      HttpResponse<String> response = client.send(request,
          HttpResponse.BodyHandlers.ofString());

      // Print the response
      DEBUG("BODY: ", response.body());
      JsonNode node = JacksonUtil.mapper().readTree(response.body());
      List<JsonNode> nodes = new ArrayList<>();
      nodes.add(node);
      this.it = nodes.iterator();
    }
    catch(URISyntaxException | IOException | InterruptedException ex)
    {
      throw new OpsException(ex);
    }

    return this;
  }

  public HttpGetOp open() throws OpsException
  {
    return this;
  }

  public List<OpData> execute(OpData input)
  {
    return new OpData(it.next()).asList();
  }

  public List<OpData> close() throws OpsException
  {
    return OpData.emptyList();
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new HttpGetOp(), args);
  }
  
  @Override
  public Iterator<JsonNode> getIterator()
  {
    return it;
  }
}
