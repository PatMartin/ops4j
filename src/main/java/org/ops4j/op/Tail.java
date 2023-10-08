package org.ops4j.op;

import java.util.ArrayList;
import java.util.List;

import org.ops4j.OpData;
import org.ops4j.base.BaseOp;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;

import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@AutoService(Op.class) @Command(name = "tail", description = "Tail data.")
public class Tail extends BaseOp<Tail>
{
  private List<OpData>        data = new ArrayList<OpData>();

  @Parameters(index = "0", arity = "1",
      description = "The number of records to tail.")
  private @Getter @Setter int numRecords;

  public Tail() throws OpsException
  {
    super("tail");
  }

  public List<OpData> execute(OpData input) throws OpsException
  {
    data.add(input);
    if (data.size() > getNumRecords())
    {
      data.remove(0);
    }
    return OpData.emptyList();
  }

  public List<OpData> close() throws OpsException
  {
    return data;
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new Tail(), args);
  }
}
