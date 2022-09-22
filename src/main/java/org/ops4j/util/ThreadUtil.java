package org.ops4j.util;

public class ThreadUtil
{
  public static void sleep(Long durationMs)
  {
    if (durationMs > 0)
    {
      try
      {
        Thread.sleep(durationMs);
      }
      catch(InterruptedException e)
      {
        // I'll probably regret this.
      }
    }
  }
}
