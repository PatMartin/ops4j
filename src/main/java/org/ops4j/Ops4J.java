package org.ops4j;

import java.util.Map;
import java.util.Map.Entry;

import org.ops4j.exception.OpsException;
import org.ops4j.inf.NodeOp;
import org.ops4j.inf.Op;
import org.ops4j.inf.OpModule;
import org.ops4j.inf.OpRepo;
import org.ops4j.io.InputSource;
import org.ops4j.io.OutputDestination;
import org.ops4j.log.OpLogger;
import org.ops4j.log.OpLoggerFactory;
import org.ops4j.util.ConfigUtil;
import org.ops4j.util.JacksonUtil;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class Ops4J
{
  private static Locator  locator = null;
  private static OpLogger logger  = null;
  private static Config   config  = null;
  private static OpRepo   repo    = null;

  public Ops4J()
  {
  }

  public static enum AlgorithmType {
    AES, RC4, DES, Blowfish
  }

  public static OpLogger logger()
  {
    if (logger == null)
    {
      logger = OpLoggerFactory.getLogger("ops");
    }
    return logger;
  }

  public static Locator locator() throws OpsException
  {
    if (locator == null)
    {
      locator = new Locator();
    }
    return locator;
  }

  public static OpRepo repo() throws OpsException
  {
    if (repo == null)
    {
      String defaultRepo = config().getString("DEFAULT.REPO");
      logger().DEBUG("DEFAULT-REPO: ", defaultRepo);
      Config repoConfig = config().getConfig(defaultRepo);

      logger().DEBUG("REPO-CONFIG: ", repoConfig);
      String classType = repoConfig.getString("type");
      try
      {
        logger().DEBUG("CLASS-TYPE: ", classType);
        repo = (OpRepo) Class.forName(classType).newInstance();
        ConfigUtil.configure(repo, repoConfig.getString("args").split(" "));
      }
      catch(InstantiationException | IllegalAccessException
          | ClassNotFoundException ex)
      {
        throw new OpsException(ex);
      }
    }
    return repo;
  }

  // TODO: Find modules and load associated configuration.
  public static Config config() throws OpsException
  {
    if (config == null)
    {
      config = ConfigFactory.load("ops4j.conf");
      logger().trace("CONFIG: " + config.root().render());
      Map<String, OpModule<?>> modules = locator().getModules();
      for (String name : modules.keySet())
      {
        logger().INFO("LOADING MODULE: " + name + " with config: "
            + modules.get(name).config());
        config = config.withFallback(modules.get(name).config());
      }
    }
    return config;
  }

  public static ArrayNode getInfo() throws OpsException
  {
    ArrayNode info = JacksonUtil.createArrayNode();

    JacksonUtil.addArray(info, "module", getModulesInfo());
    JacksonUtil.addArray(info, "op", getOpsInfo());
    JacksonUtil.addArray(info, "nodeop", getNodeOpsInfo());
    JacksonUtil.addArray(info, "repo", getReposInfo());
    JacksonUtil.addArray(info, "source", getInputSourcesInfo());
    JacksonUtil.addArray(info, "destination", getOutputDestinatonsInfo());

    return info;
  }

  public static ArrayNode getModulesInfo() throws OpsException
  {
    ArrayNode info = JacksonUtil.createArrayNode();
    Map<String, OpModule<?>> modules = Ops4J.locator().getModules();
    for (Entry<String, OpModule<?>> module : modules.entrySet())
    {
      ObjectNode moduleInfo = JacksonUtil.createObjectNode();
      moduleInfo.put("name", module.getValue().getName());
      moduleInfo.put("namespace", module.getValue().getNamespace());
      info.add(moduleInfo);
    }
    return info;
  }

  public static ArrayNode getOpsInfo() throws OpsException
  {
    ArrayNode info = JacksonUtil.createArrayNode();
    Map<String, Op<?>> ops = Ops4J.locator().getOps();
    for (Entry<String, Op<?>> op : ops.entrySet())
    {
      ObjectNode opInfo = JacksonUtil.createObjectNode();
      opInfo.put("name", op.getValue().getName());
      opInfo.put("class-name", op.getValue().getClass().getName());
      info.add(opInfo);
    }
    return info;
  }

  public static ArrayNode getNodeOpsInfo() throws OpsException
  {
    ArrayNode info = JacksonUtil.createArrayNode();
    Map<String, NodeOp<?>> nodeops = Ops4J.locator().getNodeOps();
    for (Entry<String, NodeOp<?>> nodeop : nodeops.entrySet())
    {
      ObjectNode nodeopInfo = JacksonUtil.createObjectNode();
      nodeopInfo.put("name", nodeop.getValue().getName());
      nodeopInfo.put("class-name", nodeop.getValue().getClass().getName());
      info.add(nodeopInfo);
    }
    return info;
  }

  public static ArrayNode getReposInfo() throws OpsException
  {
    ArrayNode info = JacksonUtil.createArrayNode();
    Map<String, OpRepo> repos = Ops4J.locator().getRepos();
    for (Entry<String, OpRepo> repo : repos.entrySet())
    {
      ObjectNode repoInfo = JacksonUtil.createObjectNode();
      repoInfo.put("name", repo.getValue().getName());
      repoInfo.put("class-name", repo.getValue().getClass().getName());
      info.add(repoInfo);
    }
    return info;
  }

  public static ArrayNode getInputSourcesInfo() throws OpsException
  {
    ArrayNode info = JacksonUtil.createArrayNode();
    Map<String, InputSource<?>> sources = Ops4J.locator().getSources();
    for (Entry<String, InputSource<?>> source : sources.entrySet())
    {
      ObjectNode sourceInfo = JacksonUtil.createObjectNode();
      sourceInfo.put("name", source.getValue().getName());
      sourceInfo.put("class-name", source.getValue().getClass().getName());
      info.add(sourceInfo);
    }
    return info;
  }

  public static ArrayNode getOutputDestinatonsInfo() throws OpsException
  {
    ArrayNode info = JacksonUtil.createArrayNode();
    Map<String, OutputDestination<?>> dests = Ops4J.locator().getDestinations();
    for (Entry<String, OutputDestination<?>> dest : dests.entrySet())
    {
      ObjectNode destInfo = JacksonUtil.createObjectNode();
      destInfo.put("name", dest.getValue().getName());
      destInfo.put("class-name", dest.getValue().getClass().getName());
      info.add(destInfo);
    }
    return info;
  }
}
