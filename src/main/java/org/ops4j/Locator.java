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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;

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
      logger.DEBUG("Discovered Operation: " + op.getName());
      ops.put(op.getName(), op);
    }
  }

  private void loadNodeOps()
  {
    logger.DEBUG("*********************");
    logger.DEBUG("**** Loading NodeOps:");
    logger.DEBUG("*********************");
    ServiceLoader<NodeOp> loader = ServiceLoader.load(NodeOp.class);
    Iterator<NodeOp> it = loader.iterator();
    while (it.hasNext())
    {
      NodeOp<?> nodeOp = it.next();
      logger.DEBUG("Discovered Node Operation: ", nodeOp.getName());
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

  public JsonNode execute(String expression, JsonNode input) throws OpsException
  {
    NodeOp<?> op;
    try
    {
      return resolveNodeOp(expression).execute(input);
    }
    catch(OpsException ex)
    {
      return new TextNode(expression);
    }
  }

  public NodeOp<?> resolveNodeOp(String expression) throws OpsException
  {
    if (expression == null)
    {
      throw new OpsException("Null node operation expression.");
    }
    String exp = expression.trim();
    if (exp.length() == 0)
    {
      throw new OpsException(
          "Empty or whitespace only node operation expression.");
    }

    if (nodeOpCache.containsKey(exp))
    {
      logger.DEBUG("RESOLVED FROM CACHE: '", exp, "'");
      return nodeOpCache.get(exp);
    }

    // Functional expression:
    String fn = null, path = null, argStr = null;
    // If we have a functional expression:
    if (exp.endsWith(")"))
    {
      Pattern funcPattern = Pattern.compile("^(.*)\\((.*)\\)$");
      Matcher matcher = funcPattern.matcher(exp);

      // If we have a syntactically valid functional expression:
      if (matcher.find())
      {
        fn = matcher.group(1);
        argStr = matcher.group(2);
        int i = exp.indexOf(":/");
        if (i > 0)
        {
          fn = exp.substring(0, i);
          path = exp.substring(i + 1);
        }
        // Slip on through to the other side...
      }
      else
      {
        throw new OpsException(
            "Invalid functional node-op expression: '" + expression + "'");
      }
    }
    else
    {
      int i = exp.indexOf(":/");
      if (i > 0)
      {
        fn = exp.substring(0, i);
        path = exp.substring(i + 1);
        // slip on through...
      }
      else if (exp.endsWith(":"))
      {
        fn = exp.substring(0, exp.length() - 1);
        // slip on through
      }
      else
      {
        throw new OpsException(
            "Invalid node-op expression: '" + expression + "'");
      }
    }

    logger.DEBUG("fn='", fn, "', path='", path, "', argStr='", argStr, "'");

    if (nodeOps.containsKey(fn))
    {
      logger.DEBUG("RESOLVED: '", fn, "'");
      NodeOp<?> ctor = nodeOps.get(fn);
      NodeOp<?> op = ctor.create();

      // String args[] = StringUtils.split(argStr, " ");
      if (path == null)
      {
        op.configure(argStr);
      }
      else if (argStr == null || argStr.length() == 0)
      {
        op.configure(path);
      }
      else
      {
        op.configure(path + " " + argStr);
      }
      nodeOpCache.put(exp, op);
      return op;
    }
    else
    {
      throw new OpsException("Unresolved operation: fn='" + fn
          + "', expression='" + expression + "'");
    }
  }

  // <module>:<node-op>([[name=value][, name=value]*])
  public NodeOp<?> resolveNodeOpOld(String expression) throws OpsException
  {
    logger.DEBUG("Attempting to resolve: '" + expression + "'");

    // fn(args)
    Matcher matcher = fnPattern.matcher(expression);
    boolean matchFound = matcher.find();
    if (matchFound)
    {
      String fnName = matcher.group(1);
      String path = null;

      String fnArgs = matcher.group(2);
      logger.INFO("Resolving node-op: '", fnName, "', PATH=", path, ", ARGS: '",
          fnArgs, "'");

      if (nodeOpCache.containsKey(expression))
      {
        logger.INFO("RESOLVED FROM CACHE: '", expression, "'");
        return nodeOpCache.get(expression);
      }
      else if (nodeOps.containsKey(fnName))
      {
        logger.INFO("RESOLVED: '", fnName, "'");
        // System.out.println("FOUND: " + fnName);
        NodeOp<?> ctor = nodeOps.get(fnName);
        NodeOp<?> op = ctor.create();

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
      logger.INFO("Resolving node-op: FN: '" + fnName + "'");

      if (nodeOpCache.containsKey(expression))
      {
        return nodeOpCache.get(expression);
      }
      else if (nodeOps.containsKey(fnName))
      {
        // System.out.println("FOUND: " + fnName);
        NodeOp<?> ctor = nodeOps.get(fnName);
        NodeOp<?> op = ctor.create();
        nodeOpCache.put(expression, op);
        return op;
      }
      else
      {
        throw new OpsException("Unresolved operation: fn='" + fnName
            + "', expression='" + expression + "'");
      }
    }

    matcher = withPath.matcher(expression);
    matchFound = matcher.find();
    if (matchFound)
    {
      String fnName = matcher.group(1);
      String path = matcher.group(2);

      logger.INFO("Resolving node-op: FN: '" + fnName + "'");

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

  public static void main(String args[]) throws OpsException
  {
    Locator locator = new Locator();
    locator.resolveNodeOp("now:");

    // OpLogger.syserr("OUT: ", locator.evaluate("gen:now(--offset=86400)",
    // JacksonUtil.createObjectNode()));
    // OpLogger.syserr("OUT: ",
    // locator.evaluate("gen:now()", JacksonUtil.createObjectNode()));
  }
}
