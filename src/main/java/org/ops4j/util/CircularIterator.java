package org.ops4j.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import lombok.Getter;
import lombok.Setter;

public class CircularIterator<T> implements Iterator<T>
{
  private @Getter @Setter Iterator<T> iterator;
  private @Getter @Setter List<T>     cache        = new LinkedList<T>();
  private @Getter @Setter int         maxCacheSize = 10000;
  private @Getter @Setter long        limit        = 0;
  private AtomicLong                  count        = new AtomicLong(0);
  private Iterator<T>                 cacheIterator;

  public CircularIterator(Iterator<T> it)
  {
    this(it, 0);
  }

  public CircularIterator(Iterator<T> it, long limit)
  {
    this(it, limit, 10000);
  }

  public CircularIterator(Iterator<T> it, long limit, int cacheSize)
  {
    setIterator(it);
    setLimit(limit);
    setMaxCacheSize(cacheSize);
  }

  @Override
  public boolean hasNext()
  {
    return getLimit() == 0 || count.get() < getLimit();
  }

  @Override
  public T next()
  {
    if (!hasNext())
    {
      return null;
    }
    count.incrementAndGet();
    if (iterator.hasNext())
    {
      T item = iterator.next();
      if (getMaxCacheSize() == 0 || count.get() < getMaxCacheSize())
      {
        cache.add(item);
      }
      return item;
    }
    if (cacheIterator == null || !cacheIterator.hasNext())
    {
      cacheIterator = cache.iterator();
    }
    return cacheIterator.next();
  }

}
