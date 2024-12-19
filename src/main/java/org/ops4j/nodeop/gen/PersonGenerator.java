package org.ops4j.nodeop.gen;

import org.ops4j.base.BaseNodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.NodeOp;
import org.ops4j.util.FakerUtil;
import org.ops4j.util.JacksonUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.javafaker.Address;
import com.github.javafaker.Demographic;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.github.javafaker.PhoneNumber;
import com.google.auto.service.AutoService;

import picocli.CommandLine.Command;

@AutoService(NodeOp.class)
@Command(name = "gen-person", mixinStandardHelpOptions = false,
    description = "Generate a person.%n" + "%nExample: gen-person")
public class PersonGenerator extends BaseNodeOp<PersonGenerator>
{
  public PersonGenerator()
  {
    super("gen-person");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    Faker faker = FakerUtil.faker();
    ObjectNode person = JacksonUtil.createObjectNode();
    Address address = faker.address();
    Demographic demo = faker.demographic();
    Name name = faker.name();
    PhoneNumber phone = faker.phoneNumber();

    person.put("first", name.firstName());
    person.put("last", name.lastName());
    person.put("cell-phone", phone.cellPhone());
    person.put("work-phone", phone.phoneNumber());
    person.put("marital-status", demo.maritalStatus());
    person.put("race", demo.race());
    person.put("sex", demo.sex());
    person.put("city", address.city());
    person.put("state", address.stateAbbr());

    person.put("address", address.streetAddress());
    person.put("zip", address.zipCode());

    return person;
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new PersonGenerator(), args);
  }
}
