package org.ops4j;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.NodeOp;
import org.ops4j.inf.Op;
import org.ops4j.inf.OpModule;
import org.ops4j.inf.OpRepo;
import org.ops4j.io.FileDestination;
import org.ops4j.io.FileSource;
import org.ops4j.io.InputSource;
import org.ops4j.io.OutputDestination;
import org.ops4j.log.OpLogger;
import org.ops4j.log.OpLoggerFactory;
import org.ops4j.util.JacksonUtil;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;

public class Locator
{
  private @Getter Map<String, Op<?>>                ops           = new HashMap<>();
  private @Getter Map<String, NodeOp<?>>            nodeOps       = new HashMap<>();
  private @Getter Map<String, InputSource<?>>       sources       = new HashMap<>();
  private @Getter Map<String, OutputDestination<?>> destinations  = new HashMap<>();

  private @Getter Map<String, OpModule<?>>          modules       = new HashMap<>();
  private @Getter Map<String, OpRepo>               repos         = new HashMap<>();

  // node-op(args)
  // node-op:/path(args)
  private Pattern                                   fnPattern     = Pattern
      .compile("^\\s*(\\S+)\\((.*)\\)\\s*", Pattern.CASE_INSENSITIVE);
  // node-op
  // node-op:/path
  private Pattern                                   noargsPattern = Pattern
      .compile("^\\s*(\\S+):\\s*$", Pattern.CASE_INSENSITIVE);
  private Pattern                                   withPath      = Pattern
      .compile("^(\\S+):(/\\S*)$");
  private Map<String, NodeOp<?>>                    nodeOpCache   = new HashMap<String, NodeOp<?>>();
  private Map<String, InputSource<?>>               sourceCache   = new HashMap<String, InputSource<?>>();
  private Map<String, OutputDestination<?>>         destCache     = new HashMap<String, OutputDestination<?>>();
  private OpLogger                                  logger        = OpLoggerFactory
      .getLogger("ops.loc");

  public Locator() throws OpsException
  {
    loadOps();
    loadNodeOps();
    loadSources();
    loadDestinations();
    loadModules();
    loadRepos();
  }

  private void loadOps()
  {
    ServiceLoader<Op> loader = ServiceLoader.load(Op.class);
    Iterator<Op> it = loader.iterator();

    while (it.hasNext())
    {
      Op<?> op = it.next();
      logger.info("Discovered Operation: " + op.getName());
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
    ServiceLoader<OutputDestination> loader = ServiceLoader
        .load(OutputDestination.class);
    Iterator<OutputDestination> it = loader.iterator();
    while (it.hasNext())
    {
      OutputDestination<?> dest = it.next();
      logger.info("Discovered Output Destination: " + dest.getName());
      destinations.put(dest.getName(), dest);
    }
  }

  private void loadModules()
  {
    logger.info("*************************");
    logger.info("**** Modules:");
    logger.info("*************************");
    ServiceLoader<OpModule> loader = ServiceLoader.load(OpModule.class);
    Iterator<OpModule> it = loader.iterator();
    while (it.hasNext())
    {
      OpModule dest = it.next();
      logger.info("Discovered OpModule: " + dest.getName());
      modules.put(dest.getName(), dest);
    }
  }

  private void loadRepos()
  {
    logger.info("*************************");
    logger.info("**** Repos:");
    logger.info("*************************");
    ServiceLoader<OpRepo> loader = ServiceLoader.load(OpRepo.class);
    Iterator<OpRepo> it = loader.iterator();
    while (it.hasNext())
    {
      OpRepo dest = it.next();
      logger.info("Discovered OpRepo: " + dest.getType());
      repos.put(dest.getType(), dest);
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
    // System.out.println("Attempting to resolve: '" + expression + "'");
    Matcher matcher = fnPattern.matcher(expression);
    boolean matchFound = matcher.find();
    if (matchFound)
    {
      String fnName = matcher.group(1);
      String path = null;
      Matcher pathMatcher = withPath.matcher(matcher.group(1));
      if (pathMatcher.matches())
      {
        path = pathMatcher.group(2);
        fnName = pathMatcher.group(1);
      }
      String fnArgs = matcher.group(2);
      logger.DEBUG("Resolving node-op: '", fnName, "', PATH=", path,
          ", ARGS: '", fnArgs, "'");

      if (nodeOpCache.containsKey(expression))
      {
        return nodeOpCache.get(expression);
      }
      else if (nodeOps.containsKey(fnName))
      {
        // System.out.println("FOUND: " + fnName);
        NodeOp<?> ctor = nodeOps.get(fnName);
        NodeOp<?> op = ctor.create();
        if (path != null)
        {
          op.insertArg(path);
        }
        String args[] = StringUtils.split(fnArgs, " ");
        op.configure(args);
        nodeOpCache.put(expression, op);
        return op;
      }
      else
      {
        throw new OpsException("Unresolved operation: fn='" + fnName
            + "', expression='" + expression + "'");
      }
    }

    matcher = noargsPattern.matcher(expression);
    matchFound = matcher.find();
    if (matchFound)
    {
      String fnName = matcher.group(1);
      logger.debug("Resolving node-op: FN: '" + fnName + "'");

      String path = null;
      Matcher pathMatcher = withPath.matcher(fnName);
      if (pathMatcher.matches())
      {
        path = pathMatcher.group(2);
        fnName = pathMatcher.group(1);
      }

      if (nodeOpCache.containsKey(expression))
      {
        return nodeOpCache.get(expression);
      }
      else if (nodeOps.containsKey(fnName))
      {
        // System.out.println("FOUND: " + fnName);
        NodeOp<?> ctor = nodeOps.get(fnName);
        NodeOp<?> op = ctor.create();
        if (path != null)
        {
          op.insertArg(path);
        }
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
    logger.DEBUG("resolveSource(", expression, ")");
    Matcher fnMatcher = fnPattern.matcher(expression);
    Matcher noArgsMatcher = noargsPattern.matcher(expression);

    boolean MATCH = false;
    String fnName = null;
    String fnArgs = null;

    if (fnMatcher.matches())
    {
      fnName = fnMatcher.group(1);
      fnArgs = fnMatcher.group(2);
      MATCH = true;
    }
    else if (noArgsMatcher.matches())
    {
      fnName = noArgsMatcher.group(1);
      fnArgs = "";
      MATCH = true;
    }
    if (MATCH)
    {
      logger.DEBUG("Resolving input: FN: '", fnName, "', ARGS: '", fnArgs, "'");

      if (sourceCache.containsKey(expression))
      {
        logger.DEBUG("Source Cache contains: '", expression, "'");
        return sourceCache.get(expression);
      }
      else if (sources.containsKey(fnName))
      {
        logger.DEBUG("Sources contain: ", fnName);
        InputSource<?> ctor = sources.get(fnName);
        InputSource<?> op = ctor.create();
        op.configure(fnArgs);
        sourceCache.put(expression, op);
        return op;
      }
      // Try a file.
      else if (new File(expression).exists())
      {
        logger.DEBUG("Reading file: '", expression, "'");
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
      return null;
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
      FileDestination dest = new FileDestination();
      dest.setLocation(expression);
      return dest;
      // throw new OpsException(expression + " is not a valid function.");
    }
  }

  public OutputDestination<?> resolveRepo(String expression) throws OpsException
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
