package org.ops4j.op.mongo;

import org.ops4j.base.BaseOp;
import org.ops4j.exception.OpsException;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "mongo:op",
    description = "Insert documents into a mongo collection.")
public abstract class MongoOp<T extends MongoOp<?>> extends BaseOp<MongoOp<T>>
{
  @Option(names = { "-u", "-url" }, required = false,
      description = "The connection string.")
  private @Getter @Setter String           url     = null;

  @Option(names = { "-d", "-db" }, description = "The database name.")
  private @Getter @Setter String           db      = "test";

  protected com.mongodb.client.MongoClient client  = null;
  protected MongoDatabase                  mongoDb = null;

  public MongoOp(String name)
  {
    super(name);
    setDefaultView("/DEFAULT/MONGO");
  }

  public MongoOp<T> initialize() throws OpsException
  {
    info("MONGO-CONFIG: ", config());
    return this;
  }

  public MongoOp<T> open() throws OpsException
  {
    super.open();
    this.client = MongoClients
        .create(fallback(getUrl(), config().getString("connectionString")));
    this.mongoDb = client
        .getDatabase(fallback(getDb(), config().getString("db")));
    return this;
  }

  public MongoOp<T> close() throws OpsException
  {
    client.close();
    return this;
  }
}
