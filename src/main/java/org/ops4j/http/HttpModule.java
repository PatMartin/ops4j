package org.ops4j.http;

import org.ops4j.base.BaseModule;
import org.ops4j.inf.OpModule;

import com.google.auto.service.AutoService;

import picocli.CommandLine.Command;

@AutoService(OpModule.class)
@Command(name = "http", description = "This module provides HTTP support.")
public class HttpModule extends BaseModule<HttpModule>
{
  public HttpModule()
  {
    super("http", "http");
  }
}
