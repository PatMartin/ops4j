package org.ops4j.it;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.ops4j.util.JacksonUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ArrayOfLinesIterator implements Iterator<JsonNode>
{
  private InputStream is;
  private boolean     DONE = false;

  public static ArrayOfLinesIterator from(String path) throws FileNotFoundException
  {
    return new ArrayOfLinesIterator(new FileInputStream(path));
  }

  public ArrayOfLinesIterator(InputStream is)
  {
    this.is = is;
  }

  @Override
  public boolean hasNext()
  {
    if (DONE)
    {
      return false;
    }
    try
    {
      return (is != null && is.available() > 0);
    }
    catch(IOException e)
    {
      return false;
    }
  }

  @Override
  public JsonNode next()
  {
    if (!DONE)
    {
      DONE = true;
      try
      {
        List<String> lines = IOUtils.readLines(is, "UTF-8");
        ObjectNode json = JacksonUtil.createObjectNode();
        ArrayNode array = JacksonUtil.createArrayNode();
        for (String item : lines)
        {
          array.add(item);
        }
        json.set("lines", array);
        return json;
      }
      catch(Exception ex)
      {
        return null;
      }
    }
    return null;
  }
}
