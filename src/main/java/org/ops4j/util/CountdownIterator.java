package org.ops4j.util;

import java.io.IOException;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;

public class CountdownIterator implements Iterator<JsonNode>
{
  private int count   = 0;
  private int current = 0;

  public CountdownIterator(int count) throws JsonParseException, IOException
  {
    this.count = count;
  }

  @Override
  public boolean hasNext()
  {
    return current < count;
  }

  @Override
  public JsonNode next()
  {
    if (current < count)
    {
      current++;
      return JacksonUtil.createObjectNode();
    }
    return null;
  }
}
