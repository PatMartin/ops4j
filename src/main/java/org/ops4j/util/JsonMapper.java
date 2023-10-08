package org.ops4j.util;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.ops4j.Locator;
import org.ops4j.Ops4J;
import org.ops4j.exception.OpsException;
import org.ops4j.log.OpLogger;
import org.ops4j.log.OpLoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import lombok.Getter;
import lombok.Setter;

public class JsonMapper
{
  private OpLogger                logger = OpLoggerFactory
      .getLogger("ops.mapper");
  private Map<String, String>     mappings;

  private @Getter @Setter Locator locator;

  public JsonMapper(Map<String, String> mappings, Locator locator)
  {
    this.mappings = mappings;
    this.locator = locator;
  }

  public JsonMapper(Map<String, String> mappings) throws OpsException
  {
    this(mappings, Ops4J.locator());
  }

  // TODO: support output arrays
  public JsonNode map(JsonNode source) throws OpsException
  {
    logger.trace("JsonMapper.map(", source, ")");
    ObjectNode dest = JacksonUtil.createObjectNode();
    for (String destPath : mappings.keySet())
    {
      map(mappings.get(destPath), destPath, source, dest);
    }
    return dest;
  }

  public JsonNode map(String srcPath, String dstPath, JsonNode src,
      JsonNode dst) throws OpsException
  {
    JsonNode srcNode;
    if (logger.isTraceEnabled())
    {
      logger.trace("map(srcPath='", srcPath, "', dstPath='", dstPath,
          "', src='", src, "', dst='", dst, "')");

      // trace("**************************");
      // trace("SRC-PATH: '", srcPath, "'");
      // trace("DST-PATH: '", dstPath, "'");
      // trace("DST: '", dst, "'");
      // trace("DST-NODE: '", dst.at(dstPath), "'");
      // trace("**************************");
    }
    if (locator.isNodeOp(srcPath))
    {
      logger.trace("NodeOp(srcPath='", srcPath, "', src='", src.toString(),
          "')");
      srcNode = locator.execute(srcPath, src);
    }
    else if (srcPath.startsWith("/"))
    {
      srcNode = (srcPath.equals("/") ? src : src.at(srcPath));
    }
    else
    {
      srcNode = new TextNode(srcPath);
    }

    JacksonUtil.put(dstPath, dst, srcNode);

    return dst;
  }

  public ObjectNode makePath(String path, JsonNode doc)
  {
    return makePath(StringUtils.split(path, "/"), doc, false);
  }

  public ObjectNode makePath(String path, JsonNode doc, boolean stopAtParent)
  {
    return makePath(StringUtils.split(path, "/"), doc);
  }

  public ObjectNode makePath(String path[], JsonNode doc)
  {
    return makePath(path, doc, false);
  }

  public ObjectNode makePath(String path[], JsonNode doc, boolean stopAtParent)
  {
    logger.trace("makePath(" + ((path == null || path.length <= 0) ? "NULL"
        : StringUtils.join(path, "/")) + ")");
    if (path == null || path.length <= 0)
    {
      return (ObjectNode) doc;
    }
    else if (path.length == 1)
    {
      if (doc.isObject())
      {
        if (stopAtParent)
        {
          return (ObjectNode) doc;
        }
        if (doc.has(path[0]))
        {
          JsonNode existingNode = doc.get(path[0]);
          if (existingNode != null && existingNode.isObject())
          {
            return (ObjectNode) existingNode;
          }
        }
        ((ObjectNode) doc).set(path[0], JacksonUtil.createObjectNode());
        return (ObjectNode) doc.get(path[0]);
      }
      logger.warn("Doc was of type ", doc.getNodeType(), " expected object.");
      return null;
    }
    else
    {
      if (doc.isObject())
      {
        JsonNode existingNode = doc.get(path[0]);

        // trace(
        // "STOP-AT-PARENT: " + stopAtParent + ", PATH.LENGTH=" + path.length);
        if (stopAtParent && path.length == 2)
        {
          if (existingNode != null)
          {
            if (existingNode.isObject())
            {
              return (ObjectNode) existingNode;
            }
            else
            {
              ((ObjectNode) doc).set(path[0], JacksonUtil.createObjectNode());
              return (ObjectNode) doc.get(path[0]);
            }
          }
          // Not existing
          ((ObjectNode) doc).set(path[0], JacksonUtil.createObjectNode());
          return (ObjectNode) doc.get(path[0]);
        }
        else
        {
          String subPath[] = Arrays.copyOfRange(path, 1, path.length);
          // trace("PATH: " + StringUtils.join(path, "/"));
          // trace("SUBPATH: " + StringUtils.join(subPath, "/"));
          if (existingNode != null && existingNode.isObject())
          {

            return makePath(subPath, existingNode, stopAtParent);
          }
          ((ObjectNode) doc).set(path[0], JacksonUtil.createObjectNode());
          return makePath(subPath, doc.get(path[0]), stopAtParent);
        }
      }
      else
      {
        logger.warn("Doc was of type ", doc.getNodeType(), " expected object.");
        return null;
      }
    }
  }

  public JsonNode parentOf(String path, JsonNode doc)
  {
    String parts[] = StringUtils.split(path, "/");
    if (parts == null || parts.length <= 0)
    {
      return NullNode.getInstance();
    }
    else if (parts.length == 1)
    {
      return doc;
    }
    else
    {
      String left[] = Arrays.copyOf(parts, parts.length - 1);
      String parentPath = StringUtils.join(left, "/");
      JsonNode parentNode = doc.at(parentPath);
      return parentNode;
    }
  }

  public static void main(String args[])
      throws JsonProcessingException, OpsException
  {
    Map<String, String> mappings = new LinkedHashMap<String, String>();
    mappings.put("/b", "/a");
    mappings.put("/c/d/e/f", "/a");
    mappings.put("/b/c", "/a");
    mappings.put("/b/c/d", "/a");
    // mappings.put("/b/c/d/e", "/a");
    // mappings.put("/b/c/d/e/f", "/a");
    JsonMapper mapper = new JsonMapper(mappings);

    ObjectNode srcNode = JacksonUtil.createObjectNode();
    srcNode.put("a", "A");

    JsonNode mappedNode = mapper.map(srcNode);
    System.out.println("SRC: " + srcNode);
    System.out.println("DST: " + mappedNode);
  }
}
