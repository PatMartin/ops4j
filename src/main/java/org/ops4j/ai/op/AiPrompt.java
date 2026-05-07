package org.ops4j.ai.op;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.ops4j.Ops4J;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;
import org.ops4j.io.InputSource;
import org.ops4j.util.StreamUtil;

import com.google.auto.service.AutoService;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@AutoService(Op.class)
@Command(name = "prompt", description = "Ask a prompted question.")
public class AiPrompt extends AiOp<AiPrompt>
{
  @Parameters(index = "1", arity = "1..*",
      description = "Zero or more parameters for the prompt.")
  public @Getter @Setter Map<String, Object> symbols = new HashMap<>();

  @Parameters(index = "0", arity = "1..1",
      description = "The prompt.")
  private @Getter @Setter String             prompt;

  public AiPrompt()
  {
    super("prompt");
    getLifecycle().willProvide(PhaseType.INITIALIZE);
    setDefaultView("DEFAULT.AI");
  }

  public AiPrompt initialize() throws OpsException
  {
    super.initialize();
    InputSource<?> is = Ops4J.locator().resolveSource(getPrompt());
    String promptContent;
    try
    {
      promptContent = StreamUtil.readInputStreamFully(is.stream());
    }
    catch(IOException ex)
    {
      throw new OpsException(ex);
    }

    PromptTemplate promptTemplate = PromptTemplate.from(promptContent);

    String key = fallback(getKey(), config().getString("key"));
    debug("CONFIG ", config());
    debug("PROMPT: '" + promptContent + "'");
    debug("SYMBOLS: '" + getSymbols() + "'");

    ChatLanguageModel model = OpenAiChatModel.builder().apiKey(key)
        .modelName(GPT_4_O_MINI).build();

    String answer = model.generate(promptTemplate.apply(getSymbols()).text());

    System.out.println(answer);
    return this;
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new AiPrompt(), args);
  }
}
