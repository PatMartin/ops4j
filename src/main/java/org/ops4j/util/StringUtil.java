package org.ops4j.util;

import java.security.SecureRandom;
import java.util.List;

public class StringUtil
{
  private final static SecureRandom rand        = new SecureRandom();
  public final static String        ALPHANUM_UC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ01234567890";

  public final static String randomString(int min, int max)
  {
    return randomString(min, max, ALPHANUM_UC);
  }

  public final static String randomString(int min, int max, String charset)
  {
    //System.out.println("randomString(min=" + min + ", max=" + max + ")");
    int length = (min < max) ? rand.nextInt(max - min + 1) + min : min;
    char[] buffer = new char[length];
    for (int i = 0; i < buffer.length; i++)
    {
      buffer[i] = charset.charAt(rand.nextInt(charset.length()));
    }
    return new String(buffer);
  }

  public final static String align(List<String> items)
  {
    return align(items, 80);
  }

  public final static String align(List<String> items, int width)
  {
    return align(items, width, 2);
  }

  public final static String align(List<String> items, int width, int spacing)
  {
    StringBuffer sb = new StringBuffer();

    if (items == null)
    {
      return "";
    }
    int max = 0;
    for (String item : items)
    {
      if (max < item.length())
      {
        max = item.length();
      }
    }
    int columnWidth = max + spacing;
    int ncols = Math.max(1, width / columnWidth);

    //System.err.println("width=" + width + ", spacing=" + spacing + ", max="
    //    + max + ", column-width=" + columnWidth + ", ncols=" + ncols);

    int i = 0;
    for (String s : items)
    {
      sb.append(String.format("%-" + columnWidth + "s", s));
      i++;
      if (i % ncols == 0)
      {
        sb.append("\n");
      }
    }
    return sb.toString();
  }
}
