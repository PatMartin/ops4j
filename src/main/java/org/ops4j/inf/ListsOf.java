package org.ops4j.inf;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public interface ListsOf<T>
{
  enum ListType {
    ARRAY, LINKED, COW
  };

  public default List<T> listOf(ListType type)
  {
    switch (type)
    {
      case ARRAY:
      {
        return new ArrayList<T>();
      }
      case LINKED:
      {
        return new LinkedList<T>();
      }
      case COW:
      {
        return new CopyOnWriteArrayList<T>();
      }
      default:
      {
        return new ArrayList<T>();
      }
    }
  }
}
