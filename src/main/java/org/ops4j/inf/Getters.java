package org.ops4j.inf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ops4j.exception.ConfigurationException;

public interface Getters<T extends Getters<T>> extends Portable<T>
{
  public Object get(String name) throws ConfigurationException;

  public default String getString(String name) throws ConfigurationException
  {
    return get(name).toString();
  }

  public default Long getLong(String name) throws ConfigurationException
  {
    return Long.parseLong(getString(name));
  }

  public default Integer getInteger(String name) throws ConfigurationException
  {
    return Integer.parseInt(getString(name));
  }

  public default Double getDouble(String name) throws ConfigurationException
  {
    return Double.parseDouble(getString(name));
  }

  public default Boolean getBoolean(String name) throws ConfigurationException
  {
    return Boolean.parseBoolean(getString(name));
  }

  public default List<Object> getList(String name) throws ConfigurationException
  {
    List<Object> list = new ArrayList<Object>();
    list.add(get(name));
    return list;
  }

  public default Map<String, String> getMap(String name)
      throws ConfigurationException
  {
    return null;
  }
}
