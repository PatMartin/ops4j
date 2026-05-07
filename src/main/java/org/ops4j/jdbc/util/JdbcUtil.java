package org.ops4j.jdbc.util;

public class JdbcUtil
{
  public static String toJdbcName(String name)
  {
    if (name == null)
    {
      return name;
    }
    else
    {
      return name.toUpperCase().replaceAll("[-=*$]", "_");
    }
  }
}
