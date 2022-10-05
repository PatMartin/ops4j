package org.ops4j.op.http;

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
import org.ops4j.util.ThreadUtil;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.auto.service.AutoService;
import com.typesafe.config.Config;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.resource.PathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletInfo;
import jakarta.servlet.Servlet;
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
  private @Getter @Setter Map<String, String> map        = new HashMap<>();

  @Option(names = { "--app" }, description = "The application context.")
  public @Getter @Setter String               app        = "/ops";

  @Option(names = { "--host" }, description = "The hostname.")
  public @Getter @Setter String               hostname   = "localhost";

  @Option(names = { "--port" }, description = "The hostname.")
  public @Getter @Setter Integer              port       = 4242;

  @Option(names = { "--linger" },
      description = "The number of seconds to linger.")
  public @Getter @Setter Long                 linger     = 0L;

  @Option(names = { "--root" }, description = "The server root directory.")
  public @Getter @Setter String               serverRoot = null;

  public static List<ObjectNode>              DATA       = new ArrayList<>();
  public static List<ObjectNode>              METRICS    = new ArrayList<>();

  public HttpServer()
  {
    super("http:server");
    setDefaultView("DEFAULT.HTTP");
  }

  public HttpServer initialize() throws OpsException
  {
    debug("CONFIG ", config());
    return this;
  }

  public HttpServer open() throws OpsException
  {
    try
    {
      List<ServletInfo> servlets = new ArrayList<>();
      String deploymentName = config().getString("deploymentName");
      String deploymentPath = config().getString("deploymentPath");
      // TODO: Improve the accessors. Do we want JSON config root? idk yet
      List<? extends Config> servletsConfig = config()
          .getConfigList("servlets");
      for (Config servletConfig : servletsConfig)
      {
        String servletName = servletConfig.getString("name");
        String servletClassname = servletConfig.getString("class");
        String servletPath = servletConfig.getString("path");

        @SuppressWarnings("unchecked")
        Class<? extends Servlet> servletClass = (Class<? extends Servlet>) Class
            .forName(servletClassname);

        ServletInfo info = Servlets.servlet(servletName, servletClass)
            .addMapping(servletPath);

        List<? extends Config> paramsConfig = servletConfig
            .getConfigList("initParams");

        for (Config paramConfig : paramsConfig)
        {
          info.addInitParam(paramConfig.getString("name"),
              paramConfig.getString("value"));
          info(paramConfig.getString("name"), "=",
              paramConfig.getString("value"));
        }
        servlets.add(info);
      }

      DeploymentInfo servletBuilder = Servlets.deployment()
          .setClassLoader(HttpServer.class.getClassLoader())
          .setDeploymentName(fallback(config().getString("deploymentName"), "ops"))
          .setContextPath(fallback(getApp(), config().getString("app")))
          .addServlets(servlets);
      DeploymentManager manager = Servlets.defaultContainer()
          .addDeployment(servletBuilder);
      manager.deploy();

      Undertow.builder()
          .addHttpListener(
              fallback(getPort(), config()
                  .getInt("port")),
              fallback(getHostname(), config().getString("host")))
          .setHandler(Handlers.path()

              // REST API path
              // .addPrefixPath("/api", Handlers.routing()
              // .get("/customers", exchange -> {...})
              // .delete("/customers/{customerId}", exchange -> {...})
              // .setFallbackHandler(exchange -> {...}))

              // Redirect root path to /static to serve the index.html by
              // default
              // .addExactPath("/", Handlers.redirect("/index.html"))

              .addPrefixPath("/servlet", manager.start())

              .addPrefixPath("/",
                  new ResourceHandler(new PathResourceManager(
                      Paths.get(fallback(getServerRoot(),
                          config().getString("serverRoot"))),
                      100)).setDirectoryListingEnabled(true))

              // Serve all static files from a folder
              .addPrefixPath("/docs",
                  new ResourceHandler(new PathResourceManager(
                      Paths
                          .get("C:/ws/ws1/ops4j/src/main/resources/site/docs/"),
                      100)).setDirectoryListingEnabled(true))

          ).build().start();
    }
    catch(ServletException |

        ClassNotFoundException ex)
    {
      throw new OpsException(ex);
    }
    return this;
  }

  public List<OpData> execute(OpData input)
  {
    DATA.add(input.getJson());
    return input.asList();
  }

  public HttpServer close() throws OpsException
  {
    ThreadUtil.sleep(linger * 1000);
    return this;
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new HttpServer(), args);
  }
}
