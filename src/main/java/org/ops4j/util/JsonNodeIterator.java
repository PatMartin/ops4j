package org.ops4j.util;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonNodeIterator implements Iterator<JsonNode>
{
  private JsonParser parser      = null;
  private boolean    FETCH_FIRST = true;

  public JsonNodeIterator(Reader reader, ObjectMapper mapper)
      throws JsonParseException, IOException
  {
    JsonFactory factory = new JsonFactory();
    parser = factory.createParser(reader);
    parser.setCodec(mapper);
  }

  public static JsonNodeIterator fromReader(Reader reader)
      throws JsonParseException, IOException
  {
    return new JsonNodeIterator(reader, JacksonUtil.mapper());
  }

  public static JsonNodeIterator fromInputStream(InputStream is)
      throws JsonParseException, IOException
  {
    return new JsonNodeIterator(new InputStreamReader(is, "UTF-8"),
        JacksonUtil.mapper());
  }

  public static JsonNodeIterator fromPath(String path)
      throws JsonParseException, IOException
  {
    return new JsonNodeIterator(new FileReader(path), JacksonUtil.mapper());
  }

  @Override
  public boolean hasNext()
  {
    // OpLogger.syserr("hasNext()...");
    if (FETCH_FIRST)
    {
      FETCH_FIRST = false;
      try
      {
        parser.nextToken();
      }
      catch(IOException e)
      {
        // OpLogger.syserr("hasNext()=false");
        return false;
      }
    }
    boolean result = parser.hasCurrentToken();
    // OpLogger.syserr("hasNext()=" + result);
    return result;
  }

  @Override
  public JsonNode next()
  {
    if (FETCH_FIRST)
    {
      FETCH_FIRST = false;
      try
      {
        parser.nextToken();
      }
      catch(IOException ex)
      {
      }
    }
    if (parser.hasCurrentToken())
    {
      try
      {
        JsonNode node = (JsonNode) parser.readValueAs(JsonNode.class);
        parser.nextToken();
        // OpLogger.syserr("NODE=", node);
        return node;
      }
      catch(IOException e)
      {
        return null;
      }
    }
    return null;
  }
}
