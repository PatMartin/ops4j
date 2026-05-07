package org.ops4j.http.op;

import static io.undertow.Handlers.path;
import static io.undertow.Handlers.resource;
import static io.undertow.Handlers.websocket;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ops4j.OpData;
import org.ops4j.base.BaseOp;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;
import org.ops4j.util.JacksonUtil;
import org.xnio.IoFuture;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.auto.service.AutoService;

import io.undertow.Undertow;
import io.undertow.server.DefaultByteBufferPool;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.PathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.client.WebSocketClient;
import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@AutoService(Op.class) @Command(name = "wss",
    description = "Run a web socket server to enable web views "
        + "with realtime updates into the streaming data.")
public class WebSocketServer extends BaseOp<WebSocketServer>
{
  @Parameters(index = "0", arity = "0..*",
      description = "The name value pair arguments.")
  private @Getter @Setter Map<String, String> args   = new HashMap<>();

  @Option(names = { "--host" },
      description = "The web-socket host.  DEFAULT='${DEFAULT-VALUE}'")
  public @Getter @Setter String               host   = "localhost";

  @Option(names = { "--port" },
      description = "The web-socket port.  DEFAULT='${DEFAULT-VALUE}'")
  public @Getter @Setter int                  port   = 8080;

  @Option(names = { "--size" }, description = "The buffer size.  "
      + "DEFAULT='${DEFAULT-VALUE}'")
  public @Getter @Setter int                  size   = 100;

  Undertow                                    server = null;

  private List<JsonNode>                      cache;

  private WebSocketChannel                    wsc    = null;

  public WebSocketServer()
  {
    super("wss");
  }

  public WebSocketServer initialize() throws OpsException
  {
    cache = new ArrayList<>();
    server = Undertow.builder().addHttpListener(getPort(), getHost())
        .setHandler(path()
            .addPrefixPath("/wss", websocket(new WebSocketConnectionCallback()
            {

              @Override
              public void onConnect(WebSocketHttpExchange exchange,
                  WebSocketChannel channel)
              {
                channel.getReceiveSetter().set(new AbstractReceiveListener()
                {
                  @Override
                  protected void onFullTextMessage(WebSocketChannel channel,
                      BufferedTextMessage message)
                  {
                    System.out
                        .println("Received message: " + message.getData());
                    try
                    {
                      WebSockets.sendText(JacksonUtil.toString(cache), channel,
                          null);
                    }
                    catch(OpsException ex)
                    {
                      // TODO Auto-generated catch block
                      ex.printStackTrace();
                    }
                  }
                });
                channel.resumeReceives();
              }
            }))
            .addPrefixPath("/",
                resource(new ClassPathResourceManager(
                    WebSocketServer.class.getClassLoader(),
                    WebSocketServer.class.getPackage()))
                        .addWelcomeFiles("index.html"))
            .addPrefixPath("/apps",
                new ResourceHandler(new PathResourceManager(
                    Paths.get(
                        "C:/ws/ops4j/http-ops/src/main/resources/site/apps/"),
                    100)).setDirectoryListingEnabled(true))
            .addPrefixPath("/css",
                new ResourceHandler(new PathResourceManager(
                    Paths.get(
                        "C:/ws/ops4j/http-ops/src/main/resources/site/css/"),
                    100)).setDirectoryListingEnabled(true))
            .addPrefixPath("/js",
                new ResourceHandler(new PathResourceManager(
                    Paths.get(
                        "C:/ws/ops4j/http-ops/src/main/resources/site/js/"),
                    100)).setDirectoryListingEnabled(true)))

        .build();

    return this;
  }

  public WebSocketServer open() throws OpsException
  {
    server.start();

    URI uri;
    try
    {
      uri = new URI("ws://" + getHost() + ":" + getPort() + "/wss");
      System.err.println("URI:" + uri);
    }
    catch(URISyntaxException ex)
    {
      throw new OpsException(ex);
    }

    IoFuture<WebSocketChannel> future = WebSocketClient
        .connectionBuilder(server.getWorker(),
            new DefaultByteBufferPool(false, 65535), uri)
        .connect();
    try
    {
      wsc = future.get();
    }
    catch(IOException ex)
    {
      throw new OpsException(ex);
    }
    return this;
  }

  public List<OpData> execute(OpData input) throws OpsException
  {
    if (input == null)
    {
      return null;
    }
    cache.add(input.getJson());
    if (cache.size() > getSize())
    {
      cache.remove(0);
    }
    return input.asList();
  }

  public List<OpData> close() throws OpsException
  {
    DEBUG("CLOSE: server.stop()");
    server.stop();
    DEBUG("CLOSE: server stopped...");
    return OpData.emptyList();
  }

  public static void main(String args[]) throws OpsException
  {
    // launch(WebViewer.class);
    OpCLI.cli(new WebSocketServer(), args);
  }
}
