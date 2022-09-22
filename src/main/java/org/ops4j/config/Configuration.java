package org.ops4j.config;

import org.ops4j.Accessible;
import org.ops4j.Portable;
import org.ops4j.exception.ConfigurationException;

public interface Configuration<T extends Configuration<T>>
    extends Accessible<T>, Portable<T>
{
  public Configuration<T> view(String perspective)
      throws ConfigurationException;
}
