package org.ops4j.inf;

import org.ops4j.exception.OpsException;

import com.fasterxml.jackson.databind.JsonNode;

public interface JsonTransform
{
  public JsonNode transform(JsonNode json) throws OpsException;
}
