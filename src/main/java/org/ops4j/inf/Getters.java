package org.ops4j.inf;

import java.util.List;
import java.util.Map;

import org.ops4j.exception.ConfigurationException;

public interface Getters<T extends Getters<T>>
    extends Portable<T>
{
  public String get(String name) throws ConfigurationException;

  public default String getString(String name) throws ConfigurationException
  {
    return get(name);
  }

  public default Long getLong(String name) throws ConfigurationException
  {
    return Long.parseLong(get(name));
  }

  public default Integer getInteger(String name) throws ConfigurationException
  {
    return Integer.parseInt(get(name));
  }

  public default Double getDouble(String name) throws ConfigurationException
  {
    return Double.parseDouble(get(name));
  }

  public default Boolean getBoolean(String name) throws ConfigurationException
  {
    return Boolean.parseBoolean(get(name));
  }

  public default List<String> getList(String name) throws ConfigurationException
  {
    return null;
  }

  public default Map<String, String> getMap(String name)
      throws ConfigurationException
  {
    return null;
  }
}
