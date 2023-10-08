package org.ops4j.op;

import java.util.List;

import org.ops4j.OpData;
import org.ops4j.base.BaseOp;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;
import org.ops4j.inf.Router;
import org.ops4j.router.RoundRobinRouter;
import org.ops4j.router.SplitRouter;
import org.ops4j.router.WeightedRouter;

import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@AutoService(Op.class)
@Command(name = "route", description = "Route to operations.")
public class Route extends BaseOp<Route>
{
  enum RouteType {
    RR, WT, SPLIT
  }

  @Option(names = { "-t", "--type" },
      description = "The type of route. "
          + "Valid values: ${COMPLETION-CANDIDATES}.%n"
          + "  RR    = Round Robin%n" + "  WT    = Weight based routing.%n"
          + "  SPLIT = Split routing.  Routes to all in sequence.")
  private @Getter @Setter RouteType    type = RouteType.RR;

  @Parameters(index = "0", arity = "1..+",
      description = "The routes/operations.")
  private @Getter @Setter List<String> routes;

  private Router                       router;

  public Route()
  {
    super("route");
  }

  @Override
  public Route initialize() throws OpsException
  {
// Parse the route.
    switch (getType())
    {
      case WT:
      {
        router = new WeightedRouter(getRoutes());
        break;
      }
      case SPLIT:
      {
        router = new SplitRouter(getRoutes());
        break;
      }
      default:
      {
        router = new RoundRobinRouter(getRoutes());
      }
    }

    return this;
  }

  public List<OpData> execute(OpData input) throws OpsException
  {
    return router.route(input);
  }

  public List<OpData> close() throws OpsException
  {
    return router.close();
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new Route(), args);
  }
}
