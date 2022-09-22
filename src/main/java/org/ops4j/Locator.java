package org.ops4j;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ops4j.exception.OpsException;
import org.ops4j.io.FileSource;
import org.ops4j.util.JacksonUtil;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;

public class Locator
{
  private @Getter Map<String, Op<?>>                ops          = new HashMap<String, Op<?>>();
  private @Getter Map<String, NodeOp<?>>            nodeOps      = new HashMap<String, NodeOp<?>>();
  private @Getter Map<String, InputSource<?>>       sources      = new HashMap<String, InputSource<?>>();
  private @Getter Map<String, OutputDestination<?>> destinations = new HashMap<String, OutputDestination<?>>();
  private Pattern                                   fnPattern    = Pattern
      .compile("^\\s*(\\S+)\\((.*)\\)\\s*", Pattern.CASE_INSENSITIVE);
  private Map<String, NodeOp<?>>                    nodeOpCache  = new HashMap<String, NodeOp<?>>();
  private Map<String, InputSource<?>>               sourceCache  = new HashMap<String, InputSource<?>>();
  private Map<String, OutputDestination<?>>         destCache    = new HashMap<String, OutputDestination<?>>();
  private OpLogger                                  logger       = new OpLogger(
      "ops.locator");

  public Locator() throws OpsException
  {
    loadOps();
    loadNodeOps();
    loadSources();
    loadDestinations();
  }

  private void loadOps()
  {
    ServiceLoader<Op> loader = ServiceLoader.load(Op.class);
    Iterator<Op> it = loader.iterator();
    while (it.hasNext())
    {
      Op<?> op = it.next();
      logger.trace("Discovered Operation: " + op.getName());
      ops.put(op.getName(), op);
    }
  }

  private void loadNodeOps()
  {
    logger.info("*********************");
    logger.info("**** Loading NodeOps:");
    logger.info("*********************");
    ServiceLoader<NodeOp> loader = ServiceLoader.load(NodeOp.class);
    Iterator<NodeOp> it = loader.iterator();
    while (it.hasNext())
    {
      NodeOp<?> nodeOp = it.next();
      logger.info("Discovered Node Operation: " + nodeOp.getName());
      nodeOps.put(nodeOp.getName(), nodeOp);
    }
  }

  private void loadSources()
  {
    logger.info("*******************");
    logger.info("**** Input Sources:");
    logger.info("*******************");
    ServiceLoader<InputSource> loader = ServiceLoader.load(InputSource.class);
    Iterator<InputSource> it = loader.iterator();
    while (it.hasNext())
    {
      InputSource<?> source = it.next();
      logger.info("Discovered Input Source: " + source.getName());
      sources.put(source.getName(), source);
    }
  }

  private void loadDestinations()
  {
    logger.info("*************************");
    logger.info("**** Output Destinations:");
    logger.info("*************************");
    ServiceLoader<OutputDestination> loader = ServiceLoader.load(OutputDestination.class);
    Iterator<OutputDestination> it = loader.iterator();
    while (it.hasNext())
    {
      OutputDestination<?> dest = it.next();
      logger.info("Discovered Output Destination: " + dest.getName());
      destinations.put(dest.getName(), dest);
    }
  }
  
  public boolean isNodeOp(String expression)
  {
    try
    {
      resolveNodeOp(expression);
      return true;
    }
    catch(OpsException ex)
    {
      return false;
    }
  }

  // <module>:<node-op>([[name=value][, name=value]*])
  public NodeOp<?> resolveNodeOp(String expression) throws OpsException
  {
    Matcher matcher = fnPattern.matcher(expression);
    boolean matchFound = matcher.find();
    if (matchFound)
    {
      String fnName = matcher.group(1);
      String fnArgs = matcher.group(2);
      logger.trace("Resolving node-op: FN: '", fnName, "', ARGS: '", fnArgs,
          "'");

      if (nodeOpCache.containsKey(expression))
      {
        return nodeOpCache.get(expression);
      }
      else if (nodeOps.containsKey(fnName))
      {
        NodeOp<?> ctor = nodeOps.get(fnName);
        NodeOp<?> op = ctor.create();
        op.configure(fnArgs);
        nodeOpCache.put(expression, op);
        return op;
      }
      else
      {
        throw new OpsException("Unresolved operation: fn='" + fnName
            + "', expression='" + expression + "'");
      }
    }
    else
    {
      throw new OpsException(expression + " is not a valid function.");
    }
  }

  public InputSource<?> resolveSource(String expression) throws OpsException
  {
    Matcher matcher = fnPattern.matcher(expression);
    boolean matchFound = matcher.find();
    if (matchFound)
    {
      String fnName = matcher.group(1);
      String fnArgs = matcher.group(2);
      logger.trace("Resolving input: FN: '", fnName, "', ARGS: '", fnArgs, "'");

      if (sourceCache.containsKey(expression))
      {
        return sourceCache.get(expression);
      }
      else if (sources.containsKey(fnName))
      {
        InputSource<?> ctor = sources.get(fnName);
        InputSource<?> op = ctor.create();
        op.configure(fnArgs);
        sourceCache.put(expression, op);
        return op;
      }
      // Try a file.
      else if (new File(expression).exists())
      {
        FileSource fs = new FileSource();
        fs.setLocation(expression);
        return fs;
      }
      else
      {
        throw new OpsException("Unresolved operation: fn='" + fnName
            + "', expression='" + expression + "'");
      }
    }
    else
    {
      throw new OpsException(expression + " is not a valid function.");
    }
  }

  public OutputDestination<?> resolveDestination(String expression)
      throws OpsException
  {
    Matcher matcher = fnPattern.matcher(expression);
    boolean matchFound = matcher.find();
    if (matchFound)
    {
      String fnName = matcher.group(1);
      String fnArgs = matcher.group(2);
      logger.trace("Resolving input: FN: '", fnName, "', ARGS: '", fnArgs, "'");

      if (destCache.containsKey(expression))
      {
        return destCache.get(expression);
      }
      else if (destinations.containsKey(fnName))
      {
        OutputDestination<?> ctor = destinations.get(fnName);
        OutputDestination<?> dest = ctor.create();
        dest.configure(fnArgs);
        destCache.put(expression, dest);
        return dest;
      }
      else
      {
        throw new OpsException("Unresolved operation: fn='" + fnName
            + "', expression='" + expression + "'");
      }
    }
    else
    {
      throw new OpsException(expression + " is not a valid function.");
    }
  }

  public JsonNode evaluate(String expression, JsonNode context,
      JsonNode fallback)
  {
    NodeOp<?> op;
    try
    {
      op = resolveNodeOp(expression);
      return op.execute(context);
    }
    catch(OpsException ex)
    {
      return fallback;
    }
  }

  public JsonNode evaluate(String expression, JsonNode input)
      throws OpsException
  {
    return resolveNodeOp(expression).execute(input);
  }

  public static void main(String args[]) throws OpsException
  {
    Locator locator = new Locator();
    OpLogger.syserr("OUT: ", locator.evaluate("gen:now(--offset=86400)",
        JacksonUtil.createObjectNode()));
    OpLogger.syserr("OUT: ",
        locator.evaluate("gen:now()", JacksonUtil.createObjectNode()));
  }
}
