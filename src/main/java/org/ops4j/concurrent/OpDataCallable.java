package org.ops4j.concurrent;

import java.util.List;
import java.util.concurrent.Callable;

import org.ops4j.OpData;
import org.ops4j.inf.Op;

public class OpDataCallable implements Callable<List<OpData>>
{
  Op<?>  op;
  OpData input;

  public OpDataCallable(Op<?> op, OpData input)
  {
    this.op = op;
    this.input = input;
  }

  @Override
  public List<OpData> call() throws Exception
  {
    return op.execute(input);
  }
}
