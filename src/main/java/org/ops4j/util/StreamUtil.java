package org.ops4j.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamUtil
{
  public static String readInputStreamFully(InputStream inputStream)
      throws IOException
  {
    StringBuilder stringBuilder = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(inputStream)))
    {
      String line;
      while ((line = reader.readLine()) != null)
      {
        stringBuilder.append(line).append("\n");
      }
    }
    return stringBuilder.toString();
  }
}
