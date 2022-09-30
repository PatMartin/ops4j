package org.ops4j.config;

import org.ops4j.exception.ConfigurationException;
import org.ops4j.util.Accessible;
import org.ops4j.util.Portable;

public interface Configuration<T extends Configuration<T>>
    extends Accessible<T>, Portable<T>
{
  public Configuration<T> view(String perspective)
      throws ConfigurationException;
}
