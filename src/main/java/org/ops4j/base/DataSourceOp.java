package org.ops4j.base;

import java.util.Iterator;

import org.ops4j.OpData;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "datasource", description = "Generic datasource operation.")
public abstract class DataSourceOp<T extends DataSourceOp<?>>
    extends BaseOp<DataSourceOp<T>> implements Iterator<OpData>
{
  @Option(names = { "-t", "--target" },
      description = "An optional target node.")
  private @Getter @Setter String target = null;

  protected abstract Iterator<OpData> getIterator();

  public DataSourceOp()
  {
    super();
  }

  public DataSourceOp(String name)
  {
    super(name);
  }

  @Override
  public boolean hasNext()
  {
    return getIterator().hasNext();
  }

  @Override
  public OpData next()
  {
    return getIterator().next();
  }

}