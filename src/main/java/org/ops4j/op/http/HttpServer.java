package org.ops4j.op.http;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ops4j.OpData;
import org.ops4j.base.BaseOp;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;
import org.ops4j.servlet.MessageServlet;

import com.google.auto.service.AutoService;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.PathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import jakarta.servlet.ServletException;
import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@AutoService(Op.class)
@Command(name = "http:server", description = "Execute a streaming http server.")
public class HttpServer extends BaseOp<HttpServer>
{
  @Parameters(index = "0", arity = "0..*",
      description = "The number of milliseconds to pause.")
  private @Getter @Setter Map<String, String> map      = new HashMap<>();

  @Option(names = { "--app" }, description = "The application context.")
  public @Getter @Setter String               app      = "/ops";

  @Option(names = { "--host" }, description = "The hostname.")
  public @Getter @Setter String               hostname = "localhost";

  @Option(names = { "--port" }, description = "The hostname.")
  public @Getter @Setter Integer              port     = 4242;

  public HttpServer()
  {
    super("http:server");
    setDefaultView("/DEFAULT/HTTP");
  }

  public HttpServer initialize() throws OpsException
  {
    debug("CONFIG: ", config());
    return this;
  }

  public HttpServer open() throws OpsException
  {
    try
    {
      DeploymentInfo servletBuilder = Servlets.deployment()
          .setClassLoader(HttpServer.class.getClassLoader())
          .setDeploymentName("ops").setContextPath(getApp())
          .addServlets(Servlets.servlet("MessageServlet", MessageServlet.class)
              .addMapping("/msg").addInitParam("message", "HOWDY"));
      DeploymentManager manager = Servlets.defaultContainer()
          .addDeployment(servletBuilder);
      manager.deploy();

      Undertow.builder().addHttpListener(4242, "localhost").setHandler(Handlers
          .path()

          // REST API path
          // .addPrefixPath("/api", Handlers.routing()
          // .get("/customers", exchange -> {...})
          // .delete("/customers/{customerId}", exchange -> {...})
          // .setFallbackHandler(exchange -> {...}))

          // Redirect root path to /static to serve the index.html by default
          // .addExactPath("/", Handlers.redirect("/index.html"))

          .addPrefixPath("/servlet", manager.start())

          .addPrefixPath("/",
              new ResourceHandler(new PathResourceManager(
                  Paths.get("C:/ws/ws1/ops4j/src/main/resources/site/"), 100))
                      .setDirectoryListingEnabled(true))

          // Serve all static files from a folder
          .addPrefixPath("/docs",
              new ResourceHandler(new PathResourceManager(
                  Paths.get("C:/ws/ws1/ops4j/src/main/resources/site/docs/"),
                  100)).setDirectoryListingEnabled(true))

      ).build().start();
    }
    catch(ServletException e)
    {
      throw new RuntimeException(e);
    }
    return this;
  }

  public List<OpData> execute(OpData input)
  {
    return input.asList();
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new HttpServer(), args);
  }
}
