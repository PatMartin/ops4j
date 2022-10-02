package org.ops4j.inf;

import org.ops4j.exception.OpsException;
import org.ops4j.util.JacksonUtil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

public interface Portable<T extends Portable<T>>
{
  public T fromJson(JsonNode info) throws OpsException;

  public JsonNode toJson() throws OpsException;

  public default T fromXml(String xml) throws OpsException
  {
    try
    {
      JsonNode json = JacksonUtil.xmlMapper().readTree(xml);
      return fromJson(json);
    }
    catch(JsonProcessingException ex)
    {
      throw new OpsException(ex);
    }
  }

  public default String toXml() throws OpsException
  {
    return JacksonUtil.toXmlString(toJson());
  }

  public default T fromYaml(String yaml) throws OpsException
  {
    try
    {
      JsonNode json = JacksonUtil.yamlMapper().readTree(yaml);
      return fromJson(json);
    }
    catch(JsonProcessingException ex)
    {
      throw new OpsException(ex);
    }
  }

  public default String toYaml() throws OpsException
  {
    return JacksonUtil.toYamlString(toJson());
  }

  public default T fromCbor(String cbor) throws OpsException
  {
    try
    {
      JsonNode json = JacksonUtil.yamlMapper().readTree(cbor);
      return fromJson(json);
    }
    catch(JsonProcessingException ex)
    {
      throw new OpsException(ex);
    }
  }

  public default String toCbor() throws OpsException
  {
    return JacksonUtil.toCborString(toJson());
  }
}
