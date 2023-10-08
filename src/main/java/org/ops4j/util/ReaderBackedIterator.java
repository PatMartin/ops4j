package org.ops4j.util;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

import org.ops4j.log.OpLogger;

public class ReaderBackedIterator<T> implements Iterator<T>
{
  private Reader      reader;
  private Iterator<T> it;

  public ReaderBackedIterator(Reader reader, Iterator<T> it)
  {
    this.reader = reader;
    this.it = it;
  }

  @Override
  public boolean hasNext()
  {

    boolean status = false;
    try
    {
      if (it != null && reader != null && reader.ready())
      {
        status = it.hasNext();
      }
    }
    catch(IOException e)
    {
      // Fall through...
    }

    return status;
  }

  @Override
  public T next()
  {
    if (hasNext())
    {
      return it.next();
    }
    return null;
  }

}
