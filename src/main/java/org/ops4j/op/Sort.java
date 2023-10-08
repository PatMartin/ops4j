package org.ops4j.op;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.ops4j.OpData;
import org.ops4j.base.BaseOp;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@AutoService(Op.class) @Command(name = "sort", description = "Sort data.")
public class Sort extends BaseOp<Sort>
{
  private List<OpData>                 data    = new ArrayList<OpData>();

  @Option(names = { "-by" }, description = "The fields to sort by")
  private @Getter @Setter List<String> sortBy;

  @Option(names = { "-r", "--reverse" }, description = "Sort in reverse order.")
  private @Getter @Setter boolean      reverse = false;

  private class OpDataComparator implements Comparator<OpData>
  {
    @Override
    public int compare(OpData data1, OpData data2)
    {
      if (data1 == null)
      {
        if (data2 == null)
        {
          return 0;
        }
        return 1;
      }
      if (data2 == null)
      {
        return -1;
      }

      JsonNode val1;
      JsonNode val2;

      String sortField;

      if (getSortBy() == null)
      {
        Iterator<String> fieldIt = data1.getJson().fieldNames();
        sortField = fieldIt.next();
      }
      else
      {
        sortField = getSortBy().get(0);
      }
      if (isReverse())
      {
        val1 = data2.getJson().get(sortField);
        val2 = data1.getJson().get(sortField);
      }
      else
      {
        val1 = data1.getJson().get(sortField);
        val2 = data2.getJson().get(sortField);
      }

      DEBUG("COMPARING: ", val1, " TO ", val2);

      if (val1 == null)
      {
        if (val2 == null)
        {
          return 0;
        }
        return 1;
      }
      if (val2 == null)
      {
        return -1;
      }

      switch (val1.getNodeType())
      {
        case NUMBER:
        {
          if (val2.isNumber())
          {
            double d1 = val1.asDouble();
            double d2 = val2.asDouble();
            if (d1 == d2)
            {
              return 0;
            }
            if (d1 < d2)
            {
              return -1;
            }
            if (d1 > d2)
            {
              return 1;
            }
          }
        }
        default:
        {
          String s1 = val1.asText();
          String s2 = val2.asText();
          return s1.compareTo(s2);
        }
      }
    }
  }

  public Sort() throws OpsException
  {
    super("sort");
  }

  public List<OpData> execute(OpData input) throws OpsException
  {
    data.add(input);
    return OpData.emptyList();
  }

  public List<OpData> close() throws OpsException
  {
    data.sort(new OpDataComparator());
    return data;
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new Sort(), args);
  }
}
