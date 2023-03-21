package org.ops4j.util;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.ops4j.exception.OpsException;
import org.ops4j.log.OpLogger;
import org.ops4j.log.OpLoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import lombok.NonNull;

public class JacksonUtil
{
  // public static LogLevel logLevel = LogLevel.INFO;
  public static ObjectMapper    mapper;
  public static ObjectMapper    prettyMapper;
  public static XmlMapper       xmlMapper;
  public static XmlMapper       prettyXmlMapper;
  public static YAMLMapper      yamlMapper;
  public static CBORMapper      cborMapper;
  public static JavaPropsMapper propsMapper;

  public static OpLogger        logger = OpLoggerFactory
      .getLogger("ops.jackson");

  public final static ObjectMapper mapper()
  {
    if (mapper == null)
    {
      mapper = new ObjectMapper();
      mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
      // mapper.enableDefaultTyping();
    }
    return mapper;
  }

  public final static ObjectMapper prettyMapper()
  {
    if (prettyMapper == null)
    {
      prettyMapper = new ObjectMapper();
      prettyMapper.enable(SerializationFeature.INDENT_OUTPUT);
      prettyMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
    return prettyMapper;
  }

  public final static ObjectMapper propsMapper()
  {
    if (propsMapper == null)
    {
      propsMapper = new JavaPropsMapper();
    }
    return propsMapper;
  }

  public final static XmlMapper xmlMapper()
  {
    if (xmlMapper == null)
    {
      xmlMapper = new XmlMapper();
    }
    return xmlMapper;
  }

  public final static XmlMapper prettyXmlMapper()
  {
    if (prettyXmlMapper == null)
    {
      prettyXmlMapper = new XmlMapper();
      prettyXmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }
    return prettyXmlMapper;
  }

  public final static YAMLMapper yamlMapper()
  {
    if (yamlMapper == null)
    {
      yamlMapper = new YAMLMapper();
    }
    return yamlMapper;
  }

  public final static CBORMapper cborMapper()
  {
    if (cborMapper == null)
    {
      cborMapper = new CBORMapper();
    }
    return cborMapper;
  }

  public static String toString(Object obj) throws OpsException
  {
    try
    {
      return mapper().writeValueAsString(obj);
    }
    catch(JsonProcessingException ex)
    {
      throw new OpsException(ex);
    }
  }

  public static String toString(Object obj, String fallback)
  {
    try
    {
      return toString(obj);
    }
    catch(OpsException ex)
    {
      return fallback;
    }
  }

  public static String toString(ObjectMapper mapper, Object obj)
      throws OpsException
  {
    try
    {
      return mapper.writeValueAsString(obj);
    }
    catch(JsonProcessingException ex)
    {
      throw new OpsException(ex);
    }
  }

  public static String toPrettyString(Object obj) throws OpsException
  {
    try
    {
      return prettyMapper().writeValueAsString(obj);
    }
    catch(JsonProcessingException ex)
    {
      throw new OpsException(ex);
    }
  }

  public static String toPrettyString(Object obj, String fallback)
  {
    try
    {
      return toPrettyString(obj);
    }
    catch(OpsException ex)
    {
      return fallback;
    }
  }

  public static String toXmlString(Object obj) throws OpsException
  {
    try
    {
      return xmlMapper().writeValueAsString(obj);
    }
    catch(JsonProcessingException ex)
    {
      throw new OpsException(ex);
    }
  }

  public static String toXmlString(Object obj, String fallback)
  {
    try
    {
      return toXmlString(obj);
    }
    catch(OpsException ex)
    {
      return fallback;
    }
  }

  public static String toYamlString(Object obj) throws OpsException
  {
    try
    {
      return yamlMapper().writeValueAsString(obj);
    }
    catch(JsonProcessingException ex)
    {
      throw new OpsException(ex);
    }
  }

  public static String toYamlString(Object obj, String fallback)
  {
    try
    {
      return toYamlString(obj);
    }
    catch(OpsException ex)
    {
      return fallback;
    }
  }

  public static byte[] toCborString(Object obj) throws OpsException
  {
    try
    {
      return cborMapper().writeValueAsBytes(obj);
    }
    catch(JsonProcessingException ex)
    {
      throw new OpsException(ex);
    }
  }

  public static String toCborString(Object obj, String fallback)
  {
    try
    {
      return toYamlString(obj);
    }
    catch(OpsException ex)
    {
      return fallback;
    }
  }

  public static ObjectNode createObjectNode()
  {
    return mapper().createObjectNode();
  }

  public static ArrayNode createArrayNode()
  {
    return mapper().createArrayNode();
  }

  public static ObjectNode toObjectNode(Map<String, ?> map)
  {
    return (ObjectNode) mapper.valueToTree(map);
  }

  public static ObjectNode makePath(String path, JsonNode doc,
      boolean stopAtParent)
  {
    return makePath(StringUtils.split(path, "/"), doc);
  }

  public static Entry<String, JsonNode> getParentEntry(@NonNull String path,
      JsonNode doc)
  {
    int index = path.lastIndexOf("/");
    if (index < 0)
    {
      return new SimpleEntry<String, JsonNode>(path.trim(), doc);
    }
    else
    {
      JsonNode node = doc.at(path.substring(0, path.lastIndexOf("/")));
      return new SimpleEntry<String, JsonNode>(path.substring(index + 1), node);
    }
  }

  public static void remove(String path, JsonNode node)
  {
    Entry<String, JsonNode> parentEntry = getParentEntry(path, node);
    ((ObjectNode) parentEntry.getValue()).remove(parentEntry.getKey());
  }

  public static List<JsonNode> unwind(List<String> paths, JsonNode doc)
      throws OpsException
  {
    List<JsonNode> nodes = null;

    for (String path : paths)
    {
      if (nodes == null)
      {
        nodes = unwind(path, doc);
      }
      else
      {
        List<JsonNode> subNodes = new ArrayList<>();
        for (JsonNode node : nodes)
        {
          logger.TRACE("L2: subNodes.addAll(unwind(", path, ", ", node, ")");
          subNodes.addAll(unwind(path, node));
        }
        nodes.clear();
        nodes.addAll(subNodes);
        subNodes.clear();
      }
    }

    return nodes;
  }

  public static List<JsonNode> unwind(@NonNull String path, JsonNode doc)
      throws OpsException
  {
    List<JsonNode> unwound = new ArrayList<>();
    @NonNull
    JsonNode target = doc.at(path);

    if (!target.isArray())
    {
      OpLogger.syserr("PATH: ", path, ", DOC: ", doc, ", TARGET: ", target);
      throw new OpsException("target '" + path + "' is not an array but is a "
          + target.getNodeType() + " instead with value=" + target);
    }

    // Prep the base record
    ArrayNode array = (ArrayNode) target;
    JsonNode base = doc.deepCopy();
    remove(path, base);

    logger.TRACE("array.size()=", array.size());
    for (int i = 0; i < array.size(); i++)
    {
      JsonNode elt = base.deepCopy();
      logger.TRACE("unwound.add(put(", path, ", ", elt, ", ", array.get(i),
          ")");
      logger.TRACE("BEFORE: ", elt);
      JsonNode after = put(path, elt, array.get(i));
      logger.TRACE("AFTER: ", elt);
      logger.TRACE(logger.getName(), "=", logger.getLogLevel());
      unwound.add(elt);
    }

    return unwound;
  }

  public static ObjectNode makePath(String path[], JsonNode doc)
  {
    return makePath(path, doc, false);
  }

  public static ObjectNode makePath(String path[], JsonNode doc,
      boolean stopAtParent)
  {
    logger.TRACE("makePath(" + ((path == null || path.length <= 0) ? "NULL"
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
      logger.WARN("Doc was of type ", doc.getNodeType(), " expected object.");
      return null;
    }
    else
    {
      if (doc.isObject())
      {
        JsonNode existingNode = doc.get(path[0]);

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
      else
      {
        logger.WARN("Doc was of type ", doc.getNodeType(), " expected object.");
        return null;
      }
    }
  }

  public static JsonNode put(String path, JsonNode target, Object value)
      throws OpsException
  {
    logger.TRACE("put(path=", path, ", target=", target, ", value=", value);
    if (path == null || !path.startsWith("/"))
    {
      throw new OpsException("Invalid path");
    }
    logger.TRACE("target.isObject()=", target.isObject());
    if (target != null && target.isObject() && path.equals("/"))
    {
      ObjectNode objNode = (ObjectNode) target;
      objNode.removeAll();
      JsonNode valueNode = mapper.valueToTree(value);
      logger.trace("valueNode=", valueNode);
      if (valueNode.isObject())
      {
        ObjectNode valueObjNode = (ObjectNode) valueNode;
        Iterator<String> fields = valueObjNode.fieldNames();
        while (fields.hasNext())
        {
          String fieldName = fields.next();
          objNode.set(fieldName, valueObjNode.get(fieldName));
        }
        return target;
      }
      return valueNode;
    }
    else if (path.equals("/"))
    {
      return mapper.valueToTree(value);
    }
    else
    {
      return put(StringUtils.split(path, "/"), target, value);
    }
  }

  public static JsonNode put(String path[], JsonNode target, Object value)
      throws OpsException
  {
    logger.TRACE("put(path=[", StringUtils.join(path, ","), "], target=",
        target, ", value=", value);
    if (path == null || target == null)
    {
      throw new OpsException("Null path or target.");
    }
    switch (path.length)
    {
      case 0:
      {
        return mapper.valueToTree(value);
      }
      case 1:
      {
        return putObject(path[0], target, value);

      }
      default:
      {
        if (!target.has(path[0]))
        {
          if (target.isObject())
          {
            ((ObjectNode) target).set(path[0], createObjectNode());
          }
        }
        return put(Arrays.copyOfRange(path, 1, path.length),
            target.get(path[0]), value);
      }
    }
  }

  public static JsonNode toJsonNode(Object obj)
  {
    if (obj == null)
    {
      return NullNode.getInstance();
    }
    else if (obj instanceof JsonNode)
    {
      return (JsonNode) obj;
    }
    else if (obj instanceof String)
    {
      return new TextNode((String) obj);
    }
    else if (obj instanceof Integer)
    {
      return new IntNode((Integer) obj);
    }
    else if (obj instanceof Long)
    {
      return new LongNode((Long) obj);
    }
    else if (obj instanceof Double)
    {
      return new DoubleNode((Double) obj);
    }
    else if (obj instanceof Boolean)
    {
      return BooleanNode.valueOf((Boolean) obj);
    }
    else
    {
      return new TextNode("UNDEFINED: " + obj.getClass().getName() + "=" + obj);
    }
  }

  public static JsonNode putObject(String name, JsonNode target, Object value)
      throws OpsException
  {
    logger.TRACE("putObject(name=", name, ", target=", target, ", value=",
        value);
    if (name == null || target == null)
    {
      return target;
    }
    JsonNode valueNode = mapper.valueToTree(value);
    switch (target.getNodeType())
    {
      case OBJECT:
      {
        ((ObjectNode) target).set(name, valueNode);
        return target;
      }
      case ARRAY:
      {
        ArrayNode dst = (ArrayNode) target;
        dst.removeAll();
        switch (valueNode.getNodeType())
        {
          case ARRAY:
          {
            ArrayNode array = (ArrayNode) valueNode;
            for (int i = 0; i < array.size(); i++)
            {
              dst.add(array.get(i));
            }
          }
          default:
          {
            dst.add(valueNode);
          }
        }
        return target;
      }
      default:
      {
        throw new OpsException("Unhandled nodetype: " + target.getNodeType()
            + " for putObject - " + name);
      }
    }
  }

  public static String interpolate(String text, JsonNode context)
  {
    // OpLogger.syserr("INTERPOLATING: ", text, " vs ", context);
    if (text == null || text.trim().length() <= 0)
    {
      return text;
    }

    Pattern varPattern = Pattern.compile("\\$\\{[^\\}]+\\}");
    Matcher varMatcher = varPattern.matcher(text);
    List<String> variables = new ArrayList<>();

    while (varMatcher.find())
    {
      variables
          .add(text.substring(varMatcher.start() + 2, varMatcher.end() - 1));
    }

    String itext = new String(text);

    for (String variable : variables)
    {
      // OpLogger.syserr("VARIABLE: ", variable);
      JsonNode node = context.at(variable);
      if (node != null)
      {
        itext = itext.replace("${" + variable + "}", node.toString());
      }
    }
    return itext;
  }

  public static JsonNode at(JsonNode node, String path)
  {
    if (path == null || node == null || path.equals("/"))
    {
      return node;
    }
    return node.at(path);
  }

  public static JsonNode removeNulls(JsonNode node)
  {
    if (node == null)
    {
      return null;
    }
    switch (node.getNodeType())
    {
      case OBJECT:
      {
        ObjectNode onode = (ObjectNode) node;
        Iterator<String> fnIt = onode.fieldNames();
        while (fnIt.hasNext())
        {
          String name = fnIt.next();
          if (onode.get(name) == null || onode.get(name).isMissingNode()
              || onode.get(name).isNull())
          {
            onode.remove(name);
          }
          else
          {
            removeNulls(onode.get(name));
          }
        }
      }
    }
    return node;
  }

  public static List<String> keys(JsonNode obj)
  {
    List<String> keys = new ArrayList<>();

    if (obj != null && !obj.isEmpty() && !obj.isNull() && obj.isObject())
    {
      Iterator<String> fieldNameIt = obj.fieldNames();
      while (fieldNameIt.hasNext())
      {
        keys.add(fieldNameIt.next());
      }
    }

    return keys;
  }
}
