package org.ops4j;

import java.util.List;
import java.util.Map;

import javax.naming.ConfigurationException;

import org.ops4j.exception.AccessibilityException;

public interface Accessible<T extends Accessible<T>>
    extends Portable<T>
{
  public String get(String name) throws AccessibilityException;

  public default String getString(String name) throws AccessibilityException
  {
    return get(name);
  }

  public default Long getLong(String name) throws AccessibilityException
  {
    return Long.parseLong(get(name));
  }

  public default Integer getInteger(String name) throws AccessibilityException
  {
    return Integer.parseInt(get(name));
  }

  public default Double getDouble(String name) throws AccessibilityException
  {
    return Double.parseDouble(get(name));
  }

  public default Boolean getBoolean(String name) throws AccessibilityException
  {
    return Boolean.parseBoolean(get(name));
  }

  public default List<String> getList(String name) throws AccessibilityException
  {
    return null;
  }

  public default Map<String, String> getMap(String name)
      throws ConfigurationException
  {
    return null;
  }

  
  public T set(String name, Object value) throws AccessibilityException;
}
