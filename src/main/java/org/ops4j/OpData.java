package org.ops4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ops4j.exception.AccessibilityException;
import org.ops4j.exception.OpsException;
import org.ops4j.util.JacksonUtil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.Getter;
import lombok.Setter;

public class OpData implements Portable<OpData>, Accessible<OpData>
{
  private @Getter @Setter ObjectNode json;

  public OpData()
  {
    this(JacksonUtil.createObjectNode());
  }

  public OpData(JsonNode json)
  {
    if (json != null)
    {
      if (json.isObject())
      {
        setJson((ObjectNode) json);
      }
      else if (json.isArray())
      {
        setJson(JacksonUtil.createObjectNode());
        getJson().set("array", (ArrayNode) json);
      }
      else
      {
        setJson(JacksonUtil.createObjectNode());
        getJson().put("value", json.asText());
      }
    }
    else
    {
      setJson(JacksonUtil.createObjectNode());
    }
  }

  public static OpData from(String json)
      throws JsonMappingException, JsonProcessingException
  {
    return new OpData((ObjectNode) JacksonUtil.mapper().readTree(json));
  }

  public static OpData from(Map<String, ?> map)
      throws JsonMappingException, JsonProcessingException
  {
    return new OpData(JacksonUtil.toObjectNode(map));
  }

  public List<OpData> asList()
  {
    List<OpData> list = new ArrayList<OpData>(1);
    list.add(this);
    return list;
  }

  public static List<OpData> emptyList()
  {
    return new ArrayList<OpData>(0);
  }

  public String toString()
  {
    try
    {
      return JacksonUtil.toString(getJson());
    }
    catch(OpsException ex)
    {
      return json.asText();
    }
  }

  @Override
  public OpData fromJson(JsonNode info) throws OpsException
  {
    try
    {
      return JacksonUtil.mapper().readValue(info.toString(), OpData.class);
    }
    catch(JsonProcessingException ex)
    {
      throw new OpsException(ex);
    }
  }

  @Override
  public JsonNode toJson() throws OpsException
  {
    return getJson();
  }

  @Override
  public String get(String name) throws AccessibilityException
  {
    JsonNode got = json.at(name);
    if (got == null)
    {
      return null;
    }
    return got.toString();
  }

  public static void main(String args[]) throws OpsException
  {
    OpData data = new OpData()
        .set("/tests/array", new String[] { "a", "b", "c" })
        .set("/tests/string", "String test passed");
    OpLogger.syserr("DATA-JSON=", data);
    OpLogger.syserr("DATA-XML =", data.toXml());
    OpLogger.syserr("DATA-YAML =", data.toYaml());
  }

  @Override
  public OpData set(String name, Object value) throws AccessibilityException
  {
    try
    {
      JacksonUtil.put(name, json, value);
    }
    catch(OpsException ex)
    {
      throw new AccessibilityException(ex);
    }
    return this;
  }
}
