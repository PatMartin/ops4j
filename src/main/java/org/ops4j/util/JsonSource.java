package org.ops4j.util;

import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;

public interface JsonSource
{
  public Iterator<JsonNode> getIterator();
}
