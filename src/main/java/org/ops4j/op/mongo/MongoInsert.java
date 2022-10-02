package org.ops4j.op.mongo;

import java.util.List;

import org.bson.Document;
import org.ops4j.OpData;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;

import com.google.auto.service.AutoService;
import com.mongodb.client.MongoCollection;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@AutoService(Op.class) @Command(name = "mongo:insert",
    description = "Insert documents into a mongo collection.")
public class MongoInsert extends MongoOp<MongoInsert>
{
  @Option(names = { "-c", "-collection" }, description = "The collection name.")
  private @Getter @Setter String    collection      = "test";

  private MongoCollection<Document> mongoCollection = null;

  public MongoInsert()
  {
    super("mongo:insert");
  }

  public MongoInsert open() throws OpsException
  {
    super.open();
    this.mongoCollection = mongoDb.getCollection(getCollection());
    return this;
  }

  public List<OpData> execute(OpData input)
  {
    debug("Inserting: ", Document.parse(input.toString()));
    mongoCollection.insertOne(Document.parse(input.toString()));
    return input.asList();
  }

  public MongoInsert close() throws OpsException
  {
    super.close();
    return this;
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new MongoInsert(), args);
  }
}
