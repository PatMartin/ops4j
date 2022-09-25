package org.ops4j.util;

import java.util.Iterator;

import org.bson.Document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCursor;

public class MongoIterator implements Iterator<JsonNode>
{
  private MongoCursor<Document> it = null;

  public MongoIterator(AggregateIterable<Document> it)
  {
    this.it = it.iterator();
  }

  @Override
  public boolean hasNext()
  {
    return it.hasNext();
  }

  @Override
  public JsonNode next()
  {
    Document doc = it.next();
    if (doc != null)
    {
      try
      {
        return JacksonUtil.mapper().readTree(doc.toJson());
      }
      catch(JsonProcessingException ex)
      {
        ex.printStackTrace();
      }
    }
    return null;
  }

}
