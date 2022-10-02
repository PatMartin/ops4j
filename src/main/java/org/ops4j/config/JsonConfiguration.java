package org.ops4j.config;

import java.io.IOException;
import java.io.InputStream;

import org.ops4j.exception.ConfigurationException;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Configuration;
import org.ops4j.util.JacksonUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.Getter;
import lombok.Setter;

public class JsonConfiguration implements Configuration<JsonConfiguration>
{
  private @Getter @Setter ObjectNode config = null;

  public JsonConfiguration(InputStream is) throws IOException
  {
    this(JacksonUtil.mapper().readTree(is));
  }

  public JsonConfiguration(JsonNode config)
  {
    this((ObjectNode) config);
  }

  public JsonConfiguration(ObjectNode config)
  {
    setConfig(config);
  }

  @Override
  public String get(String name) throws ConfigurationException
  {
    return (config.at(name).asText());
  }

  @Override
  public JsonConfiguration set(String name, Object value)
      throws ConfigurationException
  {
    try
    {
      JacksonUtil.put(name, config, value);
    }
    catch(OpsException ex)
    {
      throw new ConfigurationException(ex);
    }
    return this;
  }

  @Override
  public JsonConfiguration fromJson(JsonNode info) throws OpsException
  {
    if (info != null && info.isObject())
    {
      return new JsonConfiguration((ObjectNode) info);
    }
    throw new OpsException("Invalid JSON.");
  }

  @Override
  public JsonNode toJson() throws OpsException
  {
    return config;
  }

  public String toString()
  {
    return (config == null) ? "" : config.toString();
  }
  
  @Override
  public JsonConfiguration view(String perspective)
      throws ConfigurationException
  {
    return new JsonConfiguration(config.at(perspective));
  }

  public static void main(String args[]) throws IOException, OpsException
  {
    JsonConfiguration config = new JsonConfiguration(
        JsonConfiguration.class.getResourceAsStream("/ops4j.json"));
    System.out.println("CONFIG: " + config.toYaml());
  }
}
