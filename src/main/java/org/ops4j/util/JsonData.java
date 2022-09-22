package org.ops4j.util;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;
import lombok.Setter;

public class JsonData
{
  private @Getter @Setter JsonNode data;

  public JsonData()
  {
    setData(JacksonUtil.createObjectNode());
  }
}
