package org.ops4j.op.mongo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bson.Document;
import org.ops4j.Op;
import org.ops4j.OpData;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.util.JsonSource;
import org.ops4j.util.MongoIterator;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.auto.service.AutoService;
import com.mongodb.client.MongoCollection;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@AutoService(Op.class)
@Command(name = "mongo:stream", description = "Stream documents from mongo.")
public class MongoStream extends MongoOp<MongoStream> implements JsonSource
{
  @Option(names = { "-c", "-collection" }, required = true,
      description = "The collection name.")
  private @Getter @Setter String       collection      = null;

  @Parameters(index = "0", arity = "0..*",
      description = "An aggregation pipeline, default = match everything in "
          + "the collection.  Default = {\"$match\":{}}.")
  private @Getter @Setter List<String> pipeline        = null;

  private MongoCollection<Document>    mongoCollection = null;

  private @Getter MongoIterator        it              = null;

  public MongoStream()
  {
    super("mongo:stream");
  }

  public MongoStream open() throws OpsException
  {
    super.open();
    this.mongoCollection = mongoDb.getCollection(getCollection());
    List<Document> aggregate = new ArrayList<Document>();
    if (getPipeline() == null || getPipeline().size() == 0)
    {
      aggregate.add(Document.parse("{\"$match\":{}}"));
    }
    else
    {
      for (String line : getPipeline())
      {
        aggregate.add(Document.parse(line));
      }
    }
    this.it = new MongoIterator(this.mongoCollection.aggregate(aggregate));
    return this;
  }

  public List<OpData> execute(OpData input)
  {
    return input.asList();
  }

  public MongoStream close() throws OpsException
  {
    super.close();
    return this;
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new MongoStream(), args);
  }

  @Override
  public Iterator<JsonNode> getIterator()
  {
    return it;
  }
}
