package org.ops4j.op;

import java.util.Iterator;
import java.util.List;

import org.ops4j.OpData;
import org.ops4j.base.BaseOp;
import org.ops4j.buddies.JsonBuddy;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;
import org.ops4j.util.JacksonUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.auto.service.AutoService;

import picocli.CommandLine.Command;

@AutoService(Op.class)
@Command(name = "xray", description = "XRay a stream of documents.")
public class XRay extends BaseOp<XRay>
{
  private ObjectNode stats;

  public XRay()
  {
    super("xray");
  }

  public XRay initialize() throws OpsException
  {
    stats = JacksonUtil.createObjectNode();
    return this;
  }

  public List<OpData> execute(OpData input)
  {
    addStatistics(input.getJson());
    return OpData.emptyList();
  }

  public List<OpData> close() throws OpsException
  {
    List<OpData> list = OpData.emptyList();
    Iterator<String> fnIt = stats.fieldNames();
    while (fnIt.hasNext())
    {
      String fieldName = fnIt.next();
      ObjectNode stat = (ObjectNode) stats.get(fieldName);
      ObjectNode entry = JacksonUtil.createObjectNode();
      entry.put("name", fieldName);
      Iterator<String> typeIt = ((ObjectNode) stat
          .get("type")).fieldNames();
      while (typeIt.hasNext()) {
        ObjectNode onode = JacksonUtil.createObjectNode();
        onode.put("fieldname",  fieldName);
        String stype = typeIt.next();
        onode.put("type", stype);
        onode.put("count", stat.get("type").get(stype).asInt());
        list.add(new OpData(onode));
      }
    }
    return list;
  }

  private void addStatistics(ObjectNode input)
  {
    ObjectNode flatNode = (ObjectNode) ((new JsonBuddy(input)).flatten()
        .json());
    Iterator<String> fieldIt = flatNode.fieldNames();
    while (fieldIt.hasNext())
    {
      String name = fieldIt.next();
      JsonNode node = flatNode.get(name);

      ObjectNode stat;
      if (!stats.has(name))
      {
        stat = JacksonUtil.createObjectNode();
        stat.set("type", JacksonUtil.createObjectNode());
        stat.put("count", 0);
        stats.set(name, stat);
      }

      stat = (ObjectNode) stats.get(name);
      ObjectNode types = (ObjectNode) stat.get("type");
      if (!types.has(node.getNodeType().toString()))
      {
        types.set(node.getNodeType().toString(), new IntNode(1));
      }
      else
      {
        int typeCount = types.get(node.getNodeType().toString()).intValue();
        typeCount++;
        types.set(node.getNodeType().toString(), new IntNode(typeCount));
      }

      int count = stat.get("count").intValue() + 1;
      stat.set("count", new IntNode(count));
    }
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new XRay(), args);
  }
}
