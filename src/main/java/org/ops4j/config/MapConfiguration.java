package org.ops4j.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.ops4j.OpLogger;
import org.ops4j.exception.AccessibilityException;
import org.ops4j.exception.ConfigurationException;
import org.ops4j.exception.OpsException;
import org.ops4j.util.JacksonUtil;

import com.fasterxml.jackson.databind.JsonNode;

public class MapConfiguration implements Configuration<MapConfiguration>
{
  private Map<String, Object> data = null;

  public MapConfiguration()
  {
    this(new HashMap<String, Object>());
  }

  public MapConfiguration(Map<String, Object> configMap)
  {
    this.data = configMap;
  }

  @Override
  public String get(String name) throws AccessibilityException
  {
    // TODO Auto-generated method stub
    return null;
  }

  public String get(String name[], Object config) throws AccessibilityException
  {
    if (name == null)
    {
      throw new AccessibilityException("name is null");
    }
    switch (name.length)
    {
      case 0:
      {
        throw new AccessibilityException("invalid name");
      }
      case 1:
      {
        if (config instanceof Map)
        {
          if (((Map<String, ?>) config).containsKey(name[0]))
          {
            return "" + ((Map<String, ?>) config).get(name[0]);
          }
        }
        throw new AccessibilityException("Invalid path");
      }
      default:
      {
        if (config instanceof Map)
        {
          Map<String, ?> map = (Map<String, ?>) config;
          if (map.containsKey(name[0]))
          {
            return get(Arrays.copyOfRange(name, 1, name.length),
                map.get(name[0]));
          }
        }
        throw new AccessibilityException("Invalid path");
      }
    }
  }

  public static void main(String args[]) throws OpsException
  {
    MapConfiguration config = new MapConfiguration().set("foo", "bar")
        .set("biz", "baz");
    OpLogger.sysout("Map Config : ", JacksonUtil.toString(config));
    OpLogger.sysout("Json Config: ", config.toJson());
    OpLogger.sysout("XML Config : ", config.toXml());
    OpLogger.sysout("YAML Config: ", config.toYaml());
    OpLogger.sysout("CBOR Config: ", config.toCbor());
  }

  @Override
  public MapConfiguration set(String name, Object value)
      throws AccessibilityException
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public MapConfiguration fromJson(JsonNode info) throws OpsException
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public JsonNode toJson() throws OpsException
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Configuration<MapConfiguration> view(String perspective)
      throws ConfigurationException
  {
    // TODO Auto-generated method stub
    return null;
  }
}
