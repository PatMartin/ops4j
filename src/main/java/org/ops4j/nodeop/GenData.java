package org.ops4j.nodeop;

import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.ops4j.BaseNodeOp;
import org.ops4j.NodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.util.JacksonUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@AutoService(NodeOp.class)
@Command(name = "gen:data", mixinStandardHelpOptions = false,
    description = "Generate data with the specified properties.")
public class GenData extends BaseNodeOp<GenData>
{
  enum DistributionType {
    UNIFORM
  }

  @Option(names = { "-min" }, required = false,
      description = "The minimum size of the data.")
  private @Getter @Setter Integer          min          = 500;

  @Option(names = { "-max" }, required = false,
      description = "The maximum size of the data.")
  private @Getter @Setter Integer          max          = 1000;

  @Option(names = { "-dist" }, required = false,
      description = "The distribution type.")
  private @Getter
  @Setter DistributionType                 distribution = DistributionType.UNIFORM;

  UniformIntegerDistribution               dist;

  public GenData()
  {
    name("gen:data");

    // Import common PRNG interface and factory class that instantiates the
    // PRNG.
    // Create (and possibly seed) a PRNG.
    long seed = 17399225432L; // Fixed seed means same results every time
    dist = new UniformIntegerDistribution(getMin(), getMax());
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    ObjectNode node = JacksonUtil.createObjectNode();
    info("size: ", dist.sample());
    return node;
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new GenData(), args);
  }
}
