package org.ops4j.op;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.ops4j.base.BaseOp;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;

import com.codahale.usl4j.Measurement;
import com.codahale.usl4j.Model;
import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@AutoService(Op.class) @Command(name = "model:usl",
    description = "Pause execution for the specified number of milliseconds.")
public class ModelUsl extends BaseOp<ModelUsl>
{
  @Parameters(index = "0", arity = "1..*",
      description = "Two or more measurements in the form: N=TPS where N is the value of the "
          + "number of threads and TPS is the transactions per second.")
  private @Getter @Setter Map<String, String> measurements = new HashMap<>();

  @Option(names = { "-min", "--min-concurrency" },
      description = "The minimum concurrency to model.")
  private @Getter @Setter int                 min          = 1;

  @Option(names = { "-max", "--max-concurrency" },
      description = "The maximum concurrency to model.")
  private @Getter @Setter int                 max          = 64;

  @Option(names = { "-inc", "--inc-concurrency" },
      description = "The concurrency increment to model.")
  private @Getter @Setter int                 inc          = 1;

  public ModelUsl()
  {
    super("model:usl");
    lifecycle().willProvide(PhaseType.OPEN);
  }

  public ModelUsl open() throws OpsException
  {
    DEBUG("Measurements: ", getMeasurements());
    // N / (1+α(N −1)+βN(N −1))

    double[][] data = new double[Math.max(getMeasurements().size(), 6)][];

    if (measurements.size() < 2)
    {
      throw new OpsException("A minimum of 2 measurements is required.");
    }

    int i = 0;
    double minN = Double.MAX_VALUE;
    double maxN = 0.0;
    double minV = 0.0;
    double maxV = 1.0;

    for (String n : measurements.keySet())
    {
      double N = Double.parseDouble(n);
      double V = Double.parseDouble(measurements.get(n));

      if (N < minN)
      {
        minN = N;
        minV = V;
      }

      if (N > maxN)
      {
        maxN = N;
        maxV = V;
      }

      data[i] = new double[2];
      data[i][0] = N;
      data[i][1] = V;
      i++;
      DEBUG("ADDING: N=", n, ", TPS=", measurements.get(n));
    }

    int missing = 6 - measurements.size();
    double run = maxN - minN;
    double rise = maxV - minV;
    double m = rise / run;
    double b = data[0][1] - (rise * data[0][0] / run);

    double increment = run / (missing + 1);
    for (int k = 0; k < missing; k++)
    {
      double y = m * (minN + ((k + 1) * increment)) + b;
      data[measurements.size() + k] = new double[2];
      data[measurements.size() + k][0] = minN + ((k + 1) * increment);
      data[measurements.size() + k][1] = y;
      DEBUG("ADDING-INTERPOLATED: [ ", data[measurements.size() + k][0], ", ",
          data[measurements.size() + k][1], " ]");
    }

    Model model = Arrays.stream(data)
        .map(Measurement.ofConcurrency()::andThroughput)
        .collect(Model.toModel());

    INFO(String.format("sim:usl -l=%f, -s=%f, -k=%f", model.lambda(),
        model.sigma(), model.kappa()));
    INFO("=================================");
    INFO("max-concurrency..........: ", model.maxConcurrency());
    INFO("is-coherency-constrained.: ", model.isCoherencyConstrained());
    INFO("is-contention-constrained: ", model.isContentionConstrained());
    INFO("is-limitless.............: ", model.isLimitless());
    INFO("max-throughput...........: ", model.maxThroughput());
    INFO("=================================");
    for (int n = getMin(); n <= getMax(); n += getInc())
    {
      INFO("TPS @ ", n, " = ", model.throughputAtConcurrency(n));
    }

    return this;
  }

  public ModelUsl inc(int inc)
  {
    setInc(inc);
    return this;
  }

  public ModelUsl min(int min)
  {
    setMin(min);
    return this;
  }

  public ModelUsl max(int max)
  {
    setMax(max);
    return this;
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new ModelUsl(), args);
  }
}
