package org.ops4j.ai.op;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;

import java.io.File;

import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;

import com.google.auto.service.AutoService;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.Response;
import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@AutoService(Op.class)
@Command(name = "query-image", description = "Ask a question about an image.")
public class QueryImage extends AiOp<QueryImage>
{
  @Parameters(index = "0", arity = "1..1",
      description = "The question to ask.  DEFAULT='${DEFAULT-VALUE}'")
  private @Getter @Setter String  question    = "What do you see?";

  @Option(names = { "-i", "--image" }, description = "The image to query.")
  private @Getter @Setter String  image;

  @Option(names = { "-m", "--max-tokens" },
      description = "The maximum number of tokens to generate.")
  private @Getter @Setter Integer maxTokens   = 100;

  @Option(names = { "-t", "--temp" }, description = "The temperature.")
  private @Getter @Setter Double  temperature = 0.1;

  public QueryImage()
  {
    super("query-image");
    getLifecycle().willProvide(PhaseType.INITIALIZE);
    setDefaultView("DEFAULT.AI");
  }

  public QueryImage initialize() throws OpsException
  {
    super.initialize();
    String key = fallback(getKey(), config().getString("key"));

    debug("CONFIG ", config());
    ChatLanguageModel model = OpenAiChatModel.builder().apiKey(key)
        .modelName(GPT_4_O_MINI).temperature(getTemperature())
        .maxTokens(getMaxTokens()).build();

    File file = new File(getImage());
    // byte[] fileContent = Files.readAllBytes(file.toPath());
    // String base64String = Base64.getEncoder().encodeToString(fileContent);
    // Image img = Image.builder().base64Data(base64String).build();
    // ImageContent ic = ImageContent.from(base64String, "image/jpeg");
    UserMessage userMessage = UserMessage.from(TextContent.from(getQuestion()),
        ImageContent.from(getImage()));
    Response<AiMessage> response = model.generate(userMessage);

    System.out.println(response.content().text());

    return this;
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new QueryImage(), args);
  }
}
