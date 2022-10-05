package org.ops4j.util;

import java.util.List;

public class Debugger
{
  public static void printErr(List<?> list)
  {
    if (list == null)
    {
      System.err.println("printErr(list): list=null");
    }
    int i = 1;
    for (Object obj : list)
    {
      System.err.println("LIST[" + i + "] = class='" + obj.getClass().getName()
          + "', value='" + obj + "'");
    }
  }
}
