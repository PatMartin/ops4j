package org.ops4j.inf;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.ops4j.exception.OpsException;

public interface Repo<T>
{
  public String getType();

  public String getName();

  public default String type()
  {
    return getType();
  }

  public default String name()
  {
    return getName();
  }

  public void setName(String name);

  public List<String> names() throws OpsException;

  public default boolean exists(String name) throws OpsException
  {
    return names().contains(name);
  }

  public default List<String> find(String pattern) throws OpsException
  {
    Pattern p = Pattern.compile(pattern);
    List<String> matched = new ArrayList<>();

    for (String name : names())
    {
      if (p.matcher(name).matches())
      {
        matched.add(name);
      }
    }

    return matched;
  }

  public T load(String name) throws OpsException;

  public void store(String name, T value) throws OpsException;
}