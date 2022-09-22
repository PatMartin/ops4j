package org.ops4j.util;

import com.github.javafaker.Faker;

public class FakerUtil
{
  private static Faker faker = null;

  public static Faker faker()
  {
    if (faker == null)
    {
      faker = new Faker();
    }
    return faker;
  }
}
