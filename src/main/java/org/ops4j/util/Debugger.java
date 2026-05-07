package org.ops4j.util;

import java.io.PrintStream;
import java.util.List;

public class Debugger
{
  public static void syserr(List<?> list)
  {
    out(System.err, list);
  }

  public static void sysout(List<?> list)
  {
    out(System.out, list);
  }
  
  public static void out(PrintStream out, List<?> list)
  {
    if (list == null)
    {
      out.println("printErr(list): list=null");
    }
    int i = 1;
    for (Object obj : list)
    {
      out.println("LIST[" + i + "] = class='" + obj.getClass().getName()
          + "', value='" + obj + "'");
    }
  }
}
