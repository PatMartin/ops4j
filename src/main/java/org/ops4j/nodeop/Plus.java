package org.ops4j.nodeop;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.ops4j.base.BaseNodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.NodeOp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BigIntegerNode;
import com.fasterxml.jackson.databind.node.DecimalNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@AutoService(NodeOp.class)
@Command(name = "plus", mixinStandardHelpOptions = false,
    description = "Adds the specified value to the specified node.")
public class Plus extends BaseNodeOp<Plus>
{
  @Parameters(index = "0", arity = "1", description = "The operand.")
  private @Getter @Setter String operand;

  public Plus()
  {
    super("plus");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    JsonNode target = getTarget(input);
    
    if (target == null)
    {
      return input;
    }
    switch (target.getNodeType())
    {
      case NUMBER:
      {
        if (target.isInt())
        {
          return new IntNode(target.asInt() + Integer.parseInt(getOperand()));
        }
        else if (target.isLong())
        {
          return new LongNode(target.asLong() + Long.parseLong(getOperand()));
        }
        else if (target.isDouble() || target.isFloat())
        {
          return new DoubleNode(
              target.asDouble() + Double.parseDouble(getOperand()));
        }
        else if (target.isBigInteger())
        {
          return new BigIntegerNode(
              target.bigIntegerValue().add(new BigInteger(getOperand())));
        }
        else if (target.isBigDecimal())
        {
          return new DecimalNode(
              target.decimalValue().add(new BigDecimal(getOperand())));
        }
      }
      default:
      {
        return target;
      }
    }
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new Plus(), args);
  }
}
