package org.ops4j.inf;

import org.ops4j.exception.ConfigurationException;

public interface Configuration<T extends Configuration<T>>
    extends Getters<T>, Setters<T>, Portable<T>
{
  public Configuration<T> view(String perspective)
      throws ConfigurationException;

  public default boolean defines(String name)
  {
    try
    {
      return get(name) != null;
    }
    catch(ConfigurationException e)
    {
      return false;
    }
  }

  public default String fallback(String name, String... values)
      throws ConfigurationException
  {
    for (String value : values)
    {
      if (value != null)
      {
        return value;
      }
    }
    return getString(name);
  }
}
