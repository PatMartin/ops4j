package org.ops4j.config;

import java.io.IOException;
import java.io.InputStream;

import org.ops4j.exception.OpsException;
import org.ops4j.util.JacksonUtil;

import com.fasterxml.jackson.databind.JsonNode;

public class YamlConfiguration extends MapConfiguration
{
  public YamlConfiguration(InputStream is) throws OpsException
  {
    try
    {
      JsonNode node = JacksonUtil.yamlMapper().readTree(is);
    }
    catch(IOException ex)
    {
      throw new OpsException(ex);
    }
  }
}
