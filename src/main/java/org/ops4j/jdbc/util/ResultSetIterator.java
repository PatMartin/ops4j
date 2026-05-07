package org.ops4j.jdbc.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import org.ops4j.jdbc.serde.ResultSetSerializer;
import org.ops4j.log.OpLogger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class ResultSetIterator implements Iterator<JsonNode>
{
  private ResultSet    rs;
  private boolean      NEXT = false;
  private ObjectMapper mapper;

  public ResultSetIterator(ResultSet rs)
  {
    this.rs = rs;
    mapper = new ObjectMapper();

    SimpleModule module = new SimpleModule();
    module.addSerializer(ResultSet.class, new ResultSetSerializer());
    mapper.registerModule(module);
  }

  public boolean hasNext()
  {
    if (NEXT)
    {
      return true;
    }
    try
    {
      NEXT = rs.next();
    }
    catch(SQLException ex)
    {
      NEXT = false;
    }
    return NEXT;
  }

  public JsonNode next()
  {
    if (hasNext())
    {
      try
      {
        JsonNode node = mapper.readTree(mapper.writeValueAsString(rs));
        NEXT = rs.next();
        return node;
      }
      catch(SQLException | JsonProcessingException ex)
      {
        // TODO Auto-generated catch block
        ex.printStackTrace();
      }
    }
    return null;
  }
}