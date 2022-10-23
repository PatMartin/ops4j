package org.ops4j.op;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.ops4j.OpData;
import org.ops4j.base.BaseOp;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@AutoService(Op.class)
@Command(name = "benchmark", description = "Benchmark something.")
public class Benchmark extends BaseOp<Benchmark>
{
  @Parameters(index = "0", arity = "0..1", description = "The number of "
      + "transactions between reports.  Default = 0 = No progress reports")
  private @Getter @Setter Long   transactionThreshold = 0L;

  private AtomicLong             count                = new AtomicLong(0);
  private AtomicLong             winCount             = new AtomicLong(0);
  private long                   startTime;
  private AtomicLong             winStartTime;
  private DecimalFormat          doubleFormat         = new DecimalFormat(
      "#,###.00");
  private DecimalFormat          intFormat            = new DecimalFormat(
      "#,###");

  @JsonIgnore
  private @Getter @Setter String header               = "";

  public Benchmark()
  {
    super("benchmark");
  }

  public Benchmark open() throws OpsException
  {
    setHeader("** " + getName() + " @ " + new Date());
    startTime = System.currentTimeMillis();
    winStartTime = new AtomicLong(startTime);
    return this;
  }

  public List<OpData> execute(OpData input)
  {
    long cnt = count.incrementAndGet();
    long winCnt = winCount.incrementAndGet();
    if (getTransactionThreshold() > 0 && winCnt >= getTransactionThreshold())
    {
      progressReport();
      winCount.set(0);
      winStartTime.set(System.currentTimeMillis());
    }
    return input.asList();
  }

  public Benchmark cleanup() throws OpsException
  {
    finalReport();
    return this;
  }

  private void progressReport()
  {
    long endTime = System.currentTimeMillis();

    if (getHeader() != null && getHeader().length() > 0)
    {
      info("***********************************************************");
      info(getHeader());
      info("***********************************************************");
      setHeader(null);
    }
    info(String.format("tps: %s, cur-tps: %s, count=%s",
        doubleFormat.format(1000.0 * count.get() / (endTime - startTime)),
        doubleFormat.format(1000.0 * getTransactionThreshold()
            / (endTime - winStartTime.getAndSet(endTime))),
        intFormat.format(count.get())));
  }

  private void finalReport()
  {
    if (getHeader() != null && getHeader().length() > 0)
    {
      info("***********************************************************");
      info(getHeader());
      info("***********************************************************");
      setHeader(null);
    }
    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;
    info("***********************************************************");
    info(String.format("TPS: %4.1f, TXNS: %d, Duration: %1.3f seconds",
        1000.0 * count.get() / (endTime - startTime), count.get(),
        duration / 1000.0));
    info("***********************************************************");
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new Benchmark(), args);
  }
}
