package org.ops4j.inf;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.jetty.util.BlockingArrayQueue;

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
        return new BlockingArrayQueue<T>();
      }
      case CONCURRENT_LINKED:
      {
        return new ConcurrentLinkedQueue<T>();
      }
      default:
      {
        return new BlockingArrayQueue<T>();
      }
    }
  }
}
