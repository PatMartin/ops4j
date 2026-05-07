package org.ops4j.inf;

public interface Fallback
{
  public default String fallback(String ... strings)
  {
    if (strings != null)
    {
      for (String s : strings)
      {
        if (s != null)
        {
          return s;
        }
      }
    }
    return null;
  }
  
  public default Integer fallback(Integer ... ints)
  {
    if (ints != null)
    {
      for (Integer i : ints)
      {
        if (i != null)
        {
          return i;
        }
      }
    }
    return null;
  }
  
  public default Double fallback(Double ... doubles)
  {
    if (doubles != null)
    {
      for (Double d : doubles)
      {
        if (d != null)
        {
          return d;
        }
      }
    }
    return null;
  }
}
