package org.ops4j.ai.op;

import static dev.langchain4j.model.openai.OpenAiImageModelName.DALL_E_3;

import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;

import com.google.auto.service.AutoService;

import dev.langchain4j.data.image.Image;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.openai.OpenAiImageModel;
import dev.langchain4j.model.output.Response;
import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@AutoService(Op.class)
@Command(name = "draw", description = "Ask AI to draw a picture.")
public class AiDraw extends AiOp<AiDraw>
{
  @Parameters(index = "0", arity = "1..1",
      description = "The question to ask.  DEFAULT='${DEFAULT-VALUE}'")
  public @Getter
  @Setter String question = "Please draw darth vader fighting puss n boots.";

  public AiDraw()
  {
    super("draw");
    getLifecycle().willProvide(PhaseType.INITIALIZE);
    setDefaultView("DEFAULT.AI");
  }

  public AiDraw initialize() throws OpsException
  {
    super.initialize();
    String key = fallback(getKey(), config().getString("key"));
    debug("CONFIG ", config());
    System.out.println("KEY: '" + key + "'");
    ImageModel model = OpenAiImageModel.builder().apiKey(key)
        .modelName(DALL_E_3).build();

    Response<Image> response = model.generate(getQuestion());

    System.out.println(response.content().url());

    return this;
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new AiDraw(), args);
  }
}
