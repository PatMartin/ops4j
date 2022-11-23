package org.ops4j.nodeop.gen;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.apache.commons.math3.distribution.LogisticDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.ops4j.base.BaseNodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.NodeOp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@AutoService(NodeOp.class) @Command(name = "dist",
    mixinStandardHelpOptions = false, description = "Generate a distribution."
        + "%n%nExample:%n%nmap dist(-precision 2)")
public class DistributionGenerator extends BaseNodeOp<DistributionGenerator>
{
  @Option(names = { "-p", "-precision" },
      description = "The precision of the distribution")
  private @Getter @Setter int             precision  = -1;

  @ArgGroup(exclusive = true, multiplicity = "1")
  private @Getter @Setter List<Composite> composites = null;

  private RealDistribution                dist       = null;

  static class Composite
  {
    @ArgGroup(exclusive = false,
        heading = "%nUniform Distributions:%n======================%n",
        multiplicity = "1")
    UniformSeries  uniform;

    @ArgGroup(exclusive = false,
        heading = "%nNormal Distributions:%n=====================%n",
        multiplicity = "1")
    NormalSeries   normal;

    @ArgGroup(exclusive = false,
        heading = "%nT Distributions:%n================%n", multiplicity = "1")
    TSeries        tseries;

    @ArgGroup(exclusive = false,
        heading = "%nLogistic Distributions:%n=======================%n",
        multiplicity = "1")
    LogisticSeries logistic;
  }

  static class UniformSeries
  {
    @Option(names = "-uniform", required = true)
    boolean uniform;
    @Option(names = "-min", required = true)
    double  min;
    @Option(names = "-max", required = true)
    double  max;
  }

  static class NormalSeries
  {
    @Option(names = "-normal", required = true)
    boolean normal;
    @Option(names = "-mean", required = true)
    double  mean;
    @Option(names = "-variance", required = true)
    double  variance;
  }

  static class TSeries
  {
    @Option(names = "-tseries", required = true)
    boolean tseries;
    @Option(names = "-freedom", required = true)
    double  freedom;
  }

  static class LogisticSeries
  {
    @Option(names = "-logistic", required = true)
    boolean logistic;
    @Option(names = "-s", required = true)
    double  s;
    @Option(names = "-mu", required = true)
    double  mu;
  }

  public DistributionGenerator()
  {
    super("dist");
  }

  public DistributionGenerator create()
  {
    return new DistributionGenerator();
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    if (dist == null)
    {
      for (Composite composite : composites)
      {
        if (composite.uniform != null && composite.uniform.uniform)
        {
          dist = new UniformRealDistribution(composite.uniform.min,
              composite.uniform.max);
        }
        else if (composite.normal != null && composite.normal.normal)
        {
          dist = new NormalDistribution(composite.normal.mean,
              composite.normal.variance);
        }
        else if (composite.tseries != null && composite.tseries.tseries)
        {
          dist = new TDistribution(composite.tseries.freedom);
        }
        else if (composite.logistic != null && composite.logistic.logistic)
        {
          dist = new LogisticDistribution(composite.logistic.mu,
              composite.logistic.s);
        }
        else
        {
          throw new OpsException("Unrecognized series specification.");
        }
      }
    }
    if (getPrecision() >= 0)
    {
      Double truncatedDouble = BigDecimal.valueOf(dist.sample())
          .setScale(getPrecision(), RoundingMode.HALF_UP).doubleValue();
      return new DoubleNode(truncatedDouble);
    }
    return new DoubleNode(dist.sample());
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new DistributionGenerator(), args);
  }
}
