package org.ops4j.inf;

import com.typesafe.config.Config;

public interface OpModule<T extends OpModule<T>>
{
  public String getName();

  public default String name()
  {
    return getName();
  }

  public String getNamespace();

  public default String namespace()
  {
    return getNamespace();
  }
  
  public Config config();
}
