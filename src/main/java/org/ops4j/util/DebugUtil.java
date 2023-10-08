package org.ops4j.util;

public class DebugUtil
{
  public static String ste()
  {
    String str = "";
    StackTraceElement ste[] = Thread.currentThread().getStackTrace();

    for (int i = 2; i < ste.length; i++)
    {
      str += "" + ste[i].getClassName() + "." + ste[i].getMethodName() + ":"
          + ste[i].getLineNumber() + "\n";
    }
    return str;
  }

  public static String ste2()
  {
    String str = "";
    StackTraceElement ste[] = Thread.currentThread().getStackTrace();

    for (int i = 1; i < ste.length; i++)
    {
      str += "" + ste[i] + "\n";
    }
    return str;
  }
}
