package org.ops4j.router;

import java.util.ArrayList;
import java.util.List;

import org.ops4j.OpData;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;
import org.ops4j.inf.Router;
import org.ops4j.util.Ops;

public class SplitRouter implements Router
{
  private List<Op<?>> ops;
  private int         cur = 0;

  public SplitRouter(List<String> routes) throws OpsException
  {
    ops = Ops.parseCommands(routes);
    for (Op<?> op : ops)
    {
      op.initialize().open();
    }
  }

  @Override
  public List<OpData> route(OpData input) throws OpsException
  {
    List<OpData> results = new ArrayList<>();
    for (Op<?> op : ops)
    {
      results.addAll(op.execute(input));
    }
    return results;
  }

  @Override
  public List<OpData> close() throws OpsException
  {
    List<OpData> results = OpData.emptyList();
    for (Op<?> op : ops)
    {
      results.addAll(op.close());
      op.cleanup();
    }
    return results;
  }

}
