package org.ops4j.util;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.NonNull;

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
    // System.out.println("randomString(min=" + min + ", max=" + max + ")");
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

    // System.err.println("width=" + width + ", spacing=" + spacing + ", max="
    // + max + ", column-width=" + columnWidth + ", ncols=" + ncols);

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

  public static List<String> splitNestedQuotes(@NonNull String str)
  {
    // Pattern unnestQuotePattern = Pattern
    // .compile("\"[^\"\\\\]*(?:\\\\.[^\"\\\\]*)*\"|\\S+");
    Pattern unnestQuotePattern = Pattern.compile("(\".*?\"|\'.*?\'|[^\'\"]+)");
    Matcher matcher = unnestQuotePattern.matcher(str);
    List<String> results = new ArrayList<>();
    while (matcher.find())
    {
      // System.err.println("MATCH: '" + matcher.group(0) + "'");
      results.add(matcher.group(0));
    }
    return results;
  }

  public static List<String> columnize(List<String> names, int width)
  {
    // Calculate the maximum length of each name
    int maxNameLength = names.stream().mapToInt(String::length).max().orElse(0);

    // Calculate the number of columns that fit within the given width
    int columns = Math.max(1, width / (maxNameLength + 1)); // +1 for spacing
                                                            // between columns

    // Determine the number of rows needed
    int rows = (int) Math.ceil((double) names.size() / columns);

    // Create a list of lines to hold the columnized output
    List<String> result = new ArrayList<>();

    // Construct each row
    for (int row = 0; row < rows; row++)
    {
      StringBuilder line = new StringBuilder();

      for (int col = 0; col < columns; col++)
      {
        int index = col * rows + row;

        if (index < names.size())
        {
          String name = names.get(index);

          // Add the name and pad with spaces to align columns
          line.append(String.format("%-" + (maxNameLength + 1) + "s", name));
        }
      }

      // Trim trailing spaces and add to result
      result.add(line.toString().stripTrailing());
    }

    return result;
  }

  public static void main(String args[])
  {
    System.out.println(
        splitNestedQuotes("Now is the time for all \"good 'men'\" to fight"));
  }
}
