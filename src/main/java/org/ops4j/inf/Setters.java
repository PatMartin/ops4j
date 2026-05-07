package org.ops4j.inf;

import org.ops4j.exception.ConfigurationException;

public interface Setters<T extends Setters<T>>
    extends Portable<T>
{
  public T set(String name, Object value) throws ConfigurationException;
}
