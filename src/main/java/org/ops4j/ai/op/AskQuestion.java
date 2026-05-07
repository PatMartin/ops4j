package org.ops4j.ai.op;

import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;

import com.google.auto.service.AutoService;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.github.GitHubModelsChatModel;
import dev.langchain4j.model.github.GitHubModelsChatModelName;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@AutoService(Op.class) @Command(name = "ask", description = "Ask a question.")
public class AskQuestion extends AiOp<AskQuestion>
{
  public static enum ChatModelType {
    OPENAI, GITHUB
  }

  @Option(names = { "-gh" },
      description = "The type of github chat model.%n%n"
          + "  DEFAULT='${DEFAULT-VALUE}'%n"
          + "  VALID VALUES: ${COMPLETION-CANDIDATES})")
  private @Getter
  @Setter GitHubModelsChatModelName                 githubModel = GitHubModelsChatModelName.GPT_4_O_MINI;

  @Option(names = { "-m", "--model" }, description = "The type of chat model.")
  private @Getter
  @Setter ChatModelType                             model       = ChatModelType.OPENAI;

  @Parameters(index = "0", arity = "1..1",
      description = "The question to ask.  DEFAULT='${DEFAULT-VALUE}'")
  public @Getter
  @Setter String                                    question    = "Why did the chicken cross the road?";

  public AskQuestion()
  {
    super("ask");
    getLifecycle().willProvide(PhaseType.INITIALIZE);
    setDefaultView("DEFAULT.AI");
  }

  public AskQuestion initialize() throws OpsException
  {
    super.initialize();
    String key = fallback(getKey(), config().getString("key"));

    debug("CONFIG ", config());
    ChatLanguageModel chat;

    switch (getModel())
    {
      case GITHUB:
      {
        chat = GitHubModelsChatModel.builder().gitHubToken(key)
            // .modelName(GitHubModelsChatModelName.GPT_4_O_MINI)
            .modelName(getGithubModel())
            // .logRequestsAndResponses(true)
            .build();

        break;
      }
      default:
      {
        chat = OpenAiChatModel.builder().apiKey(key)
            .modelName(OpenAiChatModelName.GPT_4_O_MINI).build();
      }
    }

    String answer = chat.generate(getQuestion());

    System.out.println(answer);
    return this;
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new AskQuestion(), args);
  }
}
