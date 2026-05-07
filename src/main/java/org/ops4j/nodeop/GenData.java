package org.ops4j.nodeop;

import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.ops4j.base.BaseNodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.NodeOp;
import org.ops4j.util.JacksonUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@AutoService(NodeOp.class)
@Command(name = "gen-data", mixinStandardHelpOptions = false,
    description = "Generate data with the specified properties.")
public class GenData extends BaseNodeOp<GenData>
{
  enum DistributionType {
    UNIFORM
  }

  @Option(names = { "-min" }, required = false,
      description = "The minimum size of the data.")
  private @Getter @Setter Integer          min          = 1;

  @Option(names = { "-max" }, required = false,
      description = "The maximum size of the data.")
  private @Getter @Setter Integer          max          = 100;

  @Option(names = { "-dist" }, required = false,
      description = "The distribution type.")
  private @Getter
  @Setter DistributionType                 distribution = DistributionType.UNIFORM;

  private UniformIntegerDistribution               dist;

  public GenData()
  {
    super("gen-data");
  }

  public GenData initialize() throws OpsException
  {
    return this;
  }
  
  public JsonNode execute(JsonNode input) throws OpsException
  {
    if (dist == null)
    {
      dist = new UniformIntegerDistribution(getMin(), getMax());
    }
    return new IntNode(dist.sample());
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new GenData(), args);
  }
}
