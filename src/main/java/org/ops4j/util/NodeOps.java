package org.ops4j.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.ops4j.Ops4J;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.NodeOp;

public class NodeOps
{
  public static List<NodeOp<?>> create(String args) throws OpsException
  {
    if (args == null || args.trim().length() == 0)
    {
      return new ArrayList<NodeOp<?>>();
    }
    return create(StringUtils.split(args, "=>"));
  }

  public static List<NodeOp<?>> create(List<String> args) throws OpsException
  {
    return create(args.toArray(new String[0]));
  }

  public static List<NodeOp<?>> create(String args[]) throws OpsException
  {
    List<NodeOp<?>> ops = new ArrayList<NodeOp<?>>();

    if (args != null)
    {
      for (String arg : args)
      {
        ops.add(Ops4J.locator().resolveNodeOp(arg));
      }
    }
    return ops;
  }
}
