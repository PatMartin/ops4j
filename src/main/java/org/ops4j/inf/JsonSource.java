package org.ops4j.inf;

import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;

public interface JsonSource
{
  public Iterator<JsonNode> getIterator();
}
