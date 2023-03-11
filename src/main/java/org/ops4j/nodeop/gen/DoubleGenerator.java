package org.ops4j.nodeop.gen;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang3.RandomUtils;
import org.ops4j.base.BaseNodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.NodeOp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@AutoService(NodeOp.class)
@Command(name = "int", mixinStandardHelpOptions = false,
    description = "Generate a double."
        + "%n%nExample: map /score=double(-min=1.0 -max=100.0 "
        + "-precision 2)")
public class DoubleGenerator extends BaseNodeOp<DoubleGenerator>
{
  @Option(names = { "-min" }, required = false,
      description = "The minimum double to be generated.")
  private @Getter @Setter double min       = 0.0;

  @Option(names = { "-max" }, required = false,
      description = "The maximum double to be generated.")
  private @Getter @Setter double max       = 100.0;

  @Option(names = { "-precision" }, required = false,
      description = "The precision of the double to be generated.")
  private @Getter @Setter int    precision = 2;

  public DoubleGenerator()
  {
    super("double");
  }

  public DoubleGenerator create()
  {
    return new DoubleGenerator();
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    return new DoubleNode(
        BigDecimal.valueOf(RandomUtils.nextDouble(getMin(), getMax()))
            .setScale(getPrecision(), RoundingMode.HALF_UP).doubleValue());
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new DoubleGenerator(), args);
  }
}
