package org.ops4j.ai.op;

import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;

import com.google.auto.service.AutoService;

import dev.langchain4j.data.image.Image;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.openai.OpenAiImageModel;
import dev.langchain4j.model.openai.OpenAiImageModelName;
import dev.langchain4j.model.output.Response;
import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@AutoService(Op.class)
@Command(name = "gen-image", description = "Ask a question about an image.")
public class GenImage extends AiOp<GenImage>
{
  @Parameters(index = "0", arity = "1..1",
      description = "The image description.")
  private @Getter
  @Setter String description = "Darth vader hugging C3P0 and R2D2.";

  public GenImage()
  {
    super("gen-image");
    getLifecycle().willProvide(PhaseType.INITIALIZE);
    setDefaultView("DEFAULT.AI");
  }

  public GenImage initialize() throws OpsException
  {
    super.initialize();
    String key = fallback(getKey(), config().getString("key"));

    debug("CONFIG ", config());

    ImageModel model;
    
    model = OpenAiImageModel.builder().apiKey(key)
        .modelName(OpenAiImageModelName.DALL_E_3).build();

    Response<Image> response = model.generate(getDescription());

    System.out.println(response.content().url());

    return this;
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new GenImage(), args);
  }
}
