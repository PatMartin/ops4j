package org.ops4j.ai;

import org.ops4j.base.BaseModule;
import org.ops4j.inf.OpModule;

import com.google.auto.service.AutoService;

import picocli.CommandLine.Command;

@AutoService(OpModule.class)
@Command(name = "ai", description = "This module provides AI support.")
public class AiModule extends BaseModule<AiModule>
{
  public AiModule()
  {
    super("ai", "ai");
  }
}
