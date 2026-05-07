package org.ops4j.ai.op;

import java.util.List;

import org.ops4j.OpData;
import org.ops4j.base.BaseOp;
import org.ops4j.exception.OpsException;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "ai",
    description = "Insert documents into a mongo collection.")
public abstract class AiOp<T extends AiOp<?>> extends BaseOp<AiOp<T>>
{
  public static enum AiProviderType { CHATGPT, GEMINI };
  
  @Option(names = { "-k", "--key" }, required = false,
      description = "The API key.")
  private @Getter @Setter String key;

  @Option(names = { "-p", "--provider" }, required = false,
      description = "The AI provider.")
  private @Getter @Setter AiProviderType provider = AiProviderType.CHATGPT;
  
  public AiOp(String name)
  {
    super(name);
    defaultView("DEFAULT.AI");
  }

  public AiOp<T> initialize() throws OpsException
  {
    trace("AI-CONFIG: ", config());
    return this;
  }

  public AiOp<T> open() throws OpsException
  {
    super.open();
    return this;
  }

  public List<OpData> close() throws OpsException
  {
    return super.close();
  }
}
