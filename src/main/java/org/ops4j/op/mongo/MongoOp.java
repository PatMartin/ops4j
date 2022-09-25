package org.ops4j.op.mongo;

import org.ops4j.BaseOp;
import org.ops4j.Op;
import org.ops4j.exception.OpsException;

import com.google.auto.service.AutoService;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "mongo:insert",
    description = "Insert documents into a mongo collection.")
public abstract class MongoOp<T extends MongoOp<?>> extends BaseOp<MongoOp<T>>
{
  @Option(names = { "-u", "-url" }, required = false,
      description = "The connection string.")
  private @Getter @Setter String         url     = "mongodb://localhost:27017";

  @Option(names = { "-d", "-db" }, description = "The database name.")
  private @Getter @Setter String         db      = "test";

  protected com.mongodb.client.MongoClient client  = null;
  protected MongoDatabase                  mongoDb = null;

  public MongoOp(String name)
  {
    super(name);
  }

  public MongoOp<T> open() throws OpsException
  {
    super.open();
    this.client = MongoClients.create(getUrl());
    this.mongoDb = client.getDatabase(getDb());
    return this;
  }

  public MongoOp<T> close() throws OpsException
  {
    client.close();
    return this;
  }
}
