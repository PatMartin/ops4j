package org.ops4j.inf;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public interface QueuesOf<T>
{
  enum QueueType {
    BLOCKING_ARRAY, CONCURRENT_LINKED
  };

  public default Queue<T> queueOf(QueueType type)
  {
    switch (type)
    {
      case BLOCKING_ARRAY:
      {
        return new ArrayBlockingQueue<T>(32768);
      }
      case CONCURRENT_LINKED:
      {
        return new ConcurrentLinkedQueue<T>();
      }
      default:
      {
        return new ArrayBlockingQueue<T>(32768);
      }
    }
  }
}
