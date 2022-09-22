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
  private InputStream  is;
  private JsonNode     next   = null;
  private ObjectMapper mapper = JacksonUtil.mapper();
  private JsonParser   parser = null;

  public JsonNodeIterator(Reader reader, ObjectMapper mapper)
      throws JsonParseException, IOException
  {
    JsonFactory factory = new JsonFactory();
    parser = factory.createParser(reader);
    parser.setCodec(mapper);
    parser.nextToken();
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
    return parser.hasCurrentToken();
  }

  @Override
  public JsonNode next()
  {
    if (parser.hasCurrentToken())
    {
      try
      {
        JsonNode node = (JsonNode) parser.readValueAs(JsonNode.class);
        parser.nextToken();
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
