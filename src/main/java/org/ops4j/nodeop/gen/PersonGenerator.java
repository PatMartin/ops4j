package org.ops4j.nodeop.gen;

import org.ops4j.base.BaseNodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.NodeOp;
import org.ops4j.util.FakerUtil;
import org.ops4j.util.JacksonUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.javafaker.Faker;
import com.google.auto.service.AutoService;

import picocli.CommandLine.Command;

@AutoService(NodeOp.class)
@Command(name = "gen:person", mixinStandardHelpOptions = false,
    description = "Generate a person.%n" + "%nExample: gen:person")
public class PersonGenerator extends BaseNodeOp<PersonGenerator>
{
  public PersonGenerator()
  {
    super("gen:person");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    Faker faker = FakerUtil.faker();
    ObjectNode person = JacksonUtil.createObjectNode();

    person.put("first", faker.name().firstName());
    person.put("last", faker.name().lastName());
    person.put("cell-phone", faker.phoneNumber().cellPhone());
    person.put("work-phone", faker.phoneNumber().phoneNumber());
    person.put("work-extension", faker.phoneNumber().extension());
    person.put("marital-status", faker.demographic().maritalStatus());
    person.put("race", faker.demographic().race());
    person.put("sex", faker.demographic().sex());

    return person;
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new PersonGenerator(), args);
  }
}
