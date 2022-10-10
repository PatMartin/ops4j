package org.ops4j.buddies;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.ops4j.exception.OpsException;
import org.ops4j.util.JacksonUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class JsonBuddy
{
  private @Getter @Setter JsonNode json = null;

  public JsonBuddy()
  {
    this(JacksonUtil.createObjectNode());
  }

  public JsonBuddy(JsonNode json)
  {
    this.json = json;
  }

  public JsonBuddy array(String path, Object... objects) throws OpsException
  {
    JacksonUtil.put(path, json, objects);
    return this;
  }

  public JsonBuddy set(String path, Object obj) throws OpsException
  {
    JacksonUtil.put(path, json, obj);
    return this;
  }

  public JsonNode get(String path)
  {
    return json.at(path);
  }

  public JsonNode get()
  {
    return json;
  }

  public JsonBuddy flatten()
  {
    ObjectNode flat = JacksonUtil.createObjectNode();
    flatten("", flat, json);
    setJson(flat);
    return this;
  }

  public void flatten(String prefix, ObjectNode flat, JsonNode nested)
  {
    if (nested != null)
    {
      Iterator<String> fieldNameIt = nested.fieldNames();
      while (fieldNameIt.hasNext())
      {
        String fieldName = fieldNameIt.next();
        JsonNode fieldValue = nested.get(fieldName);
        String fqName = (prefix.length() > 0) ? prefix + "." + fieldName
            : fieldName;
        System.err.println("Setting: " + fqName);
        if (fieldValue.isContainerNode())
        {
          if (fieldValue.isArray())
          {
            ArrayNode array = (ArrayNode) fieldValue;
            for (int i = 0; i < array.size(); i++)
            {
              flatten(fqName + "." + i, flat, array.get(i));
            }
          }
          else if (fieldValue.isObject())
          {
            Iterator<Entry<String, JsonNode>> childIt = fieldValue.fields();
            while (childIt.hasNext())
            {
              Entry<String, JsonNode> child = childIt.next();
              flatten(fqName + "." + child.getKey(), flat, child.getValue());
            }
          }
        }
        else
        {
          System.err.println("Setting: " + fqName);
          flat.set(fqName, fieldValue);
        }
      }
    }
  }

  public List<JsonNode> unwind(@NonNull String... paths) throws OpsException
  {
    List<JsonNode> nodes = null;

    for (String path : paths)
    {
      if (nodes == null)
      {
        nodes = JacksonUtil.unwind(path, json);
      }
      else
      {
        List<JsonNode> subNodes = new ArrayList<>();
        for (JsonNode n : nodes)
        {
          subNodes.addAll(JacksonUtil.unwind(path, n));
        }
        nodes = subNodes;
      }
    }

    return nodes;
  }

  public String toString()
  {
    return (json == null) ? null : json.toString();
  }

  public JsonNode json()
  {
    return getJson();
  }

  public static void main(String args[]) throws OpsException
  {
    JsonBuddy bud = new JsonBuddy().set("/a", "A").set("/b", "B")
        .set("/N1/N2/N3", "N").flatten();

    System.out.println("BUD: " + bud.toString());
  }
}
