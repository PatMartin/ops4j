package org.ops4j.router;

import java.util.ArrayList;
import java.util.List;

import org.ops4j.OpData;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;
import org.ops4j.inf.Router;
import org.ops4j.log.OpLogger;
import org.ops4j.op.Pipeline;
import org.ops4j.util.JacksonUtil;
import org.ops4j.util.Ops;
import org.ops4j.util.WeightedCollection;

public class WeightedRouter implements Router
{
  // private int cur = 0;
  // private double totalWeight = 0.0;
  private List<Op<?>>       pipelines   = new ArrayList<>();
  WeightedCollection<Op<?>> weightedOps = new WeightedCollection<>();

  public WeightedRouter(List<String> routes) throws OpsException
  {
    for (String rt : routes)
    {
      //OpLogger.syserr("ROUTE: ", rt);
      int loc = rt.lastIndexOf("=");
      if (loc > 0 && loc < rt.length())
      {
        String sd = rt.substring(loc + 1);
        String cmd = rt.substring(0, loc);
        //OpLogger.syserr("COMMAND: '", cmd, "', WEIGHT='", sd, "'");
        List<Op<?>> ops = Ops.parseCommands(cmd);
        double weight = Double.parseDouble(sd);
        //OpLogger.syserr("ADDING WEIGHT: ", weight, ", ops.length=", ops.size());
        Pipeline pipeline = Pipeline.of(ops);
        pipeline.initialize().open();
        pipelines.add(pipeline);
        //OpLogger.syserr("ADDING WEIGHT: ", weight);
        weightedOps.add(weight, pipeline);
        // totalWeight += weight;
      }
    }
  }

  @Override
  public List<OpData> route(OpData input) throws OpsException
  {
    return weightedOps.next().execute(input);
  }

  @Override
  public List<OpData> close() throws OpsException
  {
    List<OpData> results = OpData.emptyList();
    for (Op<?> op : pipelines)
    {
      results.addAll(op.close());
      op.cleanup();
    }
    return results;
  }

}
