package org.ops4j.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.ops4j.Ops4J;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;

import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;

public class Ops
{
  public static List<Op<?>> create(String args) throws OpsException
  {
    if (args == null || args.trim().length() == 0)
    {
      return new ArrayList<Op<?>>();
    }
    return create(StringUtils.split(args, '|'));
  }

  public static List<Op<?>> create(List<String> args) throws OpsException
  {
    return create(args.toArray(new String[0]));
  }

  public static List<Op<?>> create(String args[]) throws OpsException
  {
    Map<String, Op<?>> opMap = Ops4J.locator().getOps();
    List<Op<?>> ops = new ArrayList<Op<?>>();

    String opName;
    String opArgs[] = null;
    if (args != null)
    {
      for (String arg : args)
      {
        arg = arg.trim();
        int opMarker = arg.indexOf(" ");
        if (opMarker < 0)
        {
          opName = arg;
          opArgs = new String[0];
        }
        else
        {
          opName = arg.substring(0, opMarker);
          opArgs = StringUtils.split(arg.substring(opMarker), " ");
        }
        //System.err.println("OP-NAME: " + opName + ", ARGS=["
        //    + StringUtils.join(opArgs, ", ") + "]");
        //System.out.println("OP-MAP: " + opMap);
        if (opMap.containsKey(opName))
        {
          //System.err.println("OP " + opName + " was found.");
          Op<?> ctor = opMap.get(opName);
          Op<?> op = ctor.create();
          CommandSpec spec = CommandSpec.create();
          new CommandLine(op).parseArgs(opArgs);
          //System.err.println(
          //    "Ops.create(" + opName + ")=" + JacksonUtil.toString(op, "N/A"));
          ops.add(op);
        }
      }
    }
    return ops;
  }
}
