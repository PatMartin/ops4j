package org.ops4j.buddies;

import org.ops4j.exception.OpsException;
import org.ops4j.util.JacksonUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;

import lombok.Getter;
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

  public String toString()
  {
    return (json == null) ? null : json.toString();
  }

  public static void main(String args[]) throws OpsException
  {
    JsonBuddy bud = new JsonBuddy().set("/a", "A").set("/b", "B");

    System.out.println("BUD: " + bud.toString());
  }
}
