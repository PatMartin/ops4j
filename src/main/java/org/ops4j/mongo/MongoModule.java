package org.ops4j.mongo;

import org.ops4j.base.BaseModule;
import org.ops4j.inf.OpModule;

import com.google.auto.service.AutoService;

import picocli.CommandLine.Command;

@AutoService(OpModule.class)
@Command(name = "mongo", description = "This module provides Mongo support.")
public class MongoModule extends BaseModule<MongoModule>
{
  public MongoModule()
  {
    super("mongo", "mongo");
  }
}
