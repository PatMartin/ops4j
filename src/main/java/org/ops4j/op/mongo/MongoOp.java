package org.ops4j.op.mongo;

import org.ops4j.base.BaseOp;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Configuration;

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
  private Configuration<?>                 config  = null;

  public MongoOp(String name)
  {
    super(name);
    setDefaultView("/DEFAULT/MONGO");
  }

  public MongoOp<T> initialize() throws OpsException
  {
    config = config();
    info("MONGO-CONFIG: ", config.toJson());
    return this;
  }

  public MongoOp<T> open() throws OpsException
  {
    super.open();
    this.client = MongoClients
        .create(config.fallback("/connectionString", getUrl()));
    this.mongoDb = client.getDatabase(config.fallback("/database", getDb()));
    return this;
  }

  public MongoOp<T> close() throws OpsException
  {
    client.close();
    return this;
  }
}
