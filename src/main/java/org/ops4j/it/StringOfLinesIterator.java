package org.ops4j.it;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.ops4j.util.JacksonUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class StringOfLinesIterator implements Iterator<JsonNode>
{
  private InputStream is;
  private boolean     DONE = false;

  public static StringOfLinesIterator from(String path) throws FileNotFoundException
  {
    return new StringOfLinesIterator(new FileInputStream(path));
  }

  public StringOfLinesIterator(InputStream is)
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
        json.put("lines", StringUtils.join(lines, "\n"));
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
