package org.ops4j.buddies;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.ops4j.exception.OpsException;
import org.ops4j.util.JacksonUtil;

import com.fasterxml.jackson.databind.JsonNode;

public class StringBuddy
{
  private String str;

  public StringBuddy()
  {
    str = new String();
  }

  public StringBuddy(String str)
  {
    // Inner str will never be null, so we can avoid null checking overhead
    // for the most part. Tradeoff is we do not support null strings.
    this.str = (str == null) ? "" : str;
  }

  public static StringBuddy from(String s)
  {
    return new StringBuddy(s);
  }

  public static StringBuddy from(JsonNode node)
  {
    if (node == null)
    {
      return new StringBuddy();
    }
    try
    {
      return new StringBuddy(JacksonUtil.toString(node));
    }
    catch(OpsException e)
    {
      return new StringBuddy();
    }
  }

  public StringBuddy trim()
  {
    str = str.trim();
    return this;
  }

  public StringBuddy upperCase()
  {
    str = str.toUpperCase();
    return this;
  }

  public String banner()
  {
    return banner("=", 80);
  }

  public String banner(int width)
  {
    return banner("=", width);
  }

  public String banner(String bannerStr, int width)
  {
    List<String> lines = new ArrayList<String>(3);
    lines.add(from(bannerStr).repeat(width).get());
    lines.add(from(str).center(width - 4, " ").center(width, bannerStr).get());
    lines.add(from(bannerStr).repeat(width).get());
    return StringUtils.join(lines, "\n");
  }

  public String join(List<String> strings)
  {
    return StringUtils.join(strings, ",");
  }

  public String join(List<String> strings, String separator)
  {
    return StringUtils.join(strings, separator);
  }

  public String[] split()
  {
    return StringUtils.split(str);
  }

  public String[] split(String separator)
  {
    return StringUtils.split(str, separator);
  }

  public boolean contains(String search)
  {
    return StringUtils.contains(str, search);
  }

  public boolean containsAnyIgnoreCase(String search)
  {
    return StringUtils.containsAnyIgnoreCase(str, search);
  }

  public boolean containsAny(String search)
  {
    return StringUtils.containsAny(str, search);
  }

  public boolean containsIgnoreCase(String search)
  {
    return StringUtils.containsIgnoreCase(str, search);
  }

  public boolean containsNone(String invalid)
  {
    return StringUtils.containsNone(str, invalid);
  }

  public boolean containsOnly(String valid)
  {
    return StringUtils.containsOnly(str, valid);
  }

  public boolean containsWhitespace()
  {
    return StringUtils.containsWhitespace(str);
  }

  public boolean endsWith(String sequence)
  {
    return StringUtils.endsWith(str, sequence);
  }

  public boolean startsWith(String sequence)
  {
    return StringUtils.startsWith(str, sequence);
  }

  public boolean endsWithIgnoreCase(String sequence)
  {
    return StringUtils.endsWithIgnoreCase(str, sequence);
  }

  public boolean startsWithIgnoreCase(String sequence)
  {
    return StringUtils.startsWithIgnoreCase(str, sequence);
  }

  public StringBuddy uc()
  {
    str = str.toUpperCase();
    return this;
  }

  public StringBuddy lowerCase()
  {
    str = str.toLowerCase();
    return this;
  }

  public StringBuddy lc()
  {
    str = str.toLowerCase();
    return this;
  }

  // TODO: Implement
  public StringBuddy camelCase()
  {
    return this;
  }

  public StringBuddy abbreviate()
  {
    return abbreviate("...", 20);
  }

  public StringBuddy abbreviate(String abbrevSymbol, int width)
  {
    str = StringUtils.abbreviate(str, abbrevSymbol, width);
    return this;
  }

  public StringBuddy capitalize()
  {
    str = StringUtils.capitalize(str);
    return this;
  }

  public StringBuddy center()
  {
    center(str.length() + 4, "  ");
    return this;
  }

  public StringBuddy center(int width, String spacing)
  {
    str = StringUtils.center(str, width, spacing);
    return this;
  }

  public StringBuddy chomp()
  {
    str = StringUtils.chomp(str);
    return this;
  }

  public StringBuddy chop()
  {
    str = StringUtils.chop(str);
    return this;
  }

  public StringBuddy removeWhitespace()
  {
    str = StringUtils.deleteWhitespace(str);
    return this;
  }

  public StringBuddy digits()
  {
    str = StringUtils.getDigits(str);
    return this;
  }

  public StringBuddy diff(String str)
  {
    this.str = StringUtils.difference(this.str, str);
    return this;
  }

  public StringBuddy normalizeSpace()
  {
    str = StringUtils.normalizeSpace(str);
    return this;
  }

  public StringBuddy overlay(String overlay, int start)
  {
    str = StringUtils.overlay(str, overlay, start,
        start + overlay.length() - 1);
    return this;
  }

  public StringBuddy remove(String... remove)
  {
    if (remove != null)
    {
      for (String r : remove)
      {
        str = StringUtils.remove(str, r);
      }
    }
    return this;
  }

  public StringBuddy repeat(int repetitions)
  {
    str = StringUtils.repeat(str, repetitions);
    return this;
  }

  public StringBuddy set(String str)
  {
    if (str != null)
    {
      this.str = str;
    }
    return this;
  }

  public String get()
  {
    return str;
  }

  public StringBuddy left(int length)
  {
    str = StringUtils.left(str, length);
    return this;
  }

  public StringBuddy leftPad(int size)
  {
    return leftPad(size, " ");
  }

  public StringBuddy leftPad(int size, String pad)
  {
    str = StringUtils.leftPad(str, size, pad);
    return this;
  }

  public StringBuddy removeAll(String pattern)
  {
    str = RegExUtils.removeAll(str, pattern);
    return this;
  }

  public StringBuddy removeAll(Pattern pattern)
  {
    str = RegExUtils.removeAll(str, pattern);
    return this;
  }

  public StringBuddy replace(String search, String replacement)
  {
    str = StringUtils.replace(str, search, replacement);
    return this;
  }

  public StringBuddy replaceAll(String regex, String replacement)
  {
    str = RegExUtils.replaceAll(str, regex, replacement);
    return this;
  }

  public StringBuddy replaceAll(Pattern regex, String replacement)
  {
    str = RegExUtils.replaceAll(str, regex, replacement);
    return this;
  }

  public StringBuddy replaceIgnoreCase(String search, String replacement)
  {
    str = StringUtils.replaceIgnoreCase(str, search, replacement);
    return this;
  }

  public StringBuddy replaceFirst(String regex, String replacement)
  {
    str = RegExUtils.replaceFirst(str, regex, replacement);
    return this;
  }

  public StringBuddy replaceOnce(String search, String replacement)
  {
    str = StringUtils.replaceOnce(str, search, replacement);
    return this;
  }

  public StringBuddy replaceOnceIgnoreCase(String search, String replacement)
  {
    str = StringUtils.replaceOnceIgnoreCase(str, search, replacement);
    return this;
  }

  public StringBuddy right(int length)
  {
    str = StringUtils.right(str, length);
    return this;
  }

  public StringBuddy rightPad(int size)
  {
    return rightPad(size, " ");
  }

  public StringBuddy rightPad(int size, String pad)
  {
    str = StringUtils.rightPad(str, size, pad);
    return this;
  }

  public StringBuddy rotate(int shift)
  {
    str = StringUtils.rotate(str, shift);
    return this;
  }

  public StringBuddy strip(String remove)
  {
    str = StringUtils.strip(str, remove);
    return this;
  }

  public StringBuddy strip()
  {
    str = StringUtils.strip(str);
    return this;
  }

  public StringBuddy stripAccents()
  {
    str = StringUtils.stripAccents(str);
    return this;
  }

  public StringBuddy stripEnd(String remove)
  {
    str = StringUtils.stripEnd(str, remove);
    return this;
  }

  public StringBuddy stripStart(String remove)
  {
    str = StringUtils.stripStart(str, remove);
    return this;
  }

  public StringBuddy stripToEmpty()
  {
    str = StringUtils.stripToEmpty(str);
    return this;
  }

  public StringBuddy substring(int start)
  {
    str = StringUtils.substring(str, start);
    return this;
  }

  public StringBuddy substring(int start, int end)
  {
    str = StringUtils.substring(str, start, end);
    return this;
  }

  public StringBuddy substringAfter(String separator)
  {
    str = StringUtils.substringAfter(str, separator);
    return this;
  }

  public StringBuddy substringAfterLast(String separator)
  {
    str = StringUtils.substringAfterLast(str, separator);
    return this;
  }

  public StringBuddy substringBefore(String separator)
  {
    str = StringUtils.substringBefore(str, separator);
    return this;
  }

  public StringBuddy substringBetween(String separator)
  {
    str = StringUtils.substringBetween(str, separator);
    return this;
  }

  public StringBuddy substringBetween(String open, String close)
  {
    str = StringUtils.substringBetween(str, open, close);
    return this;
  }

  public StringBuddy swapCase()
  {
    str = StringUtils.swapCase(str);
    return this;
  }

  public StringBuddy encode(byte bytes[], Charset charset)
  {
    str = StringUtils.toEncodedString(bytes, charset);
    return this;
  }

  public StringBuddy truncate(int width)
  {
    str = StringUtils.truncate(str, width);
    return this;
  }
  
  // Stuff which returns stuff other than StringBuddy

  public static void main(String args[])
  {
    StringBuddy bud = new StringBuddy(
        "Now is the time for all good men to fight!");
    System.out.println(bud.banner());
  }
}
