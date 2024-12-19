package org.ops4j.op;

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

@AutoService(Op.class) @Command(name = "jhead",
    description = "Output first specified number of records.")
public class Head extends BaseOp<Head>
{
  @Parameters(index = "0", arity = "1",
      description = "The number of records to output.")
  private @Getter @Setter int numRecords;

  private int                 count = 0;

  public Head() throws OpsException
  {
    super("jhead");
  }

  public List<OpData> execute(OpData input) throws OpsException
  {
    count++;
    if (count <= getNumRecords())
    {
      return input.asList();
    }
    return OpData.emptyList();
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new Head(), args);
  }
}
