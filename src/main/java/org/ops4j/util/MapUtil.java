package org.ops4j.util;

import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import lombok.NonNull;

public class MapUtil
{
  public static String findKey(@NonNull String path,
      @NonNull Map<String, ?> map, @NonNull String delimiter)
  {
    String parts[] = StringUtils.split(path, delimiter);
    for (int i = parts.length; i > 0; i--)
    {
      String key = StringUtils.join(Arrays.copyOfRange(parts, 0, i), delimiter);
      if (map.containsKey(key))
      {
        return key;
      }
    }
    return null;
  }
}
