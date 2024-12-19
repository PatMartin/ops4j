package org.ops4j.op;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.ops4j.OpData;
import org.ops4j.base.BaseOp;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;

import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@AutoService(Op.class) @Command(name = "shuffle", description = "Shuffle data.")
public class Shuffle extends BaseOp<Shuffle>
{
  private List<OpData>        data   = new ArrayList<OpData>();

  @Option(names = { "-w", "--window" }, description = "The shuffle window.%n"
      + "  DEFAULT='${DEFAULT-VALUE}'")
  private @Getter @Setter int window = 100;

  public Shuffle() throws OpsException
  {
    super("shuffle");
  }

  public List<OpData> execute(OpData input) throws OpsException
  {
    data.add(input);
    if (data.size() < getWindow())
    {
      return OpData.emptyList();
    }
    return data.remove(RandomUtils.nextInt(0, data.size())).asList();
  }

  public List<OpData> close() throws OpsException
  {
    List<OpData> shuffled = new ArrayList<>();
    while (data.size() > 0)
    {
      shuffled.add(data.remove(RandomUtils.nextInt(0, data.size())));
    }
    return shuffled;
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new Shuffle(), args);
  }
}
