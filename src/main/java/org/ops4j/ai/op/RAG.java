package org.ops4j.ai.op;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;

import java.util.List;

import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;

import com.google.auto.service.AutoService;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.ops4j.ai.Assistant;
import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@AutoService(Op.class) @Command(name = "rag", description = "Ask a question "
    + "answered with the aid of the supplied documents..")
public class RAG extends AiOp<RAG>
{
  @Parameters(index = "0", arity = "1..1",
      description = "The question to ask.  DEFAULT='${DEFAULT-VALUE}'")
  public @Getter @Setter String  question = "What is the meaning of life?";

  @Option(names = { "-d", "--data" },
      description = "Data to include for consideration in the answer.")
  private @Getter @Setter String data;

  @Option(names = { "-o", "--out" },
      description = "The name of the RAG store to generate.")
  private @Getter @Setter String out;

  @Option(names = { "-i", "--in" },
      description = "The location of the serialized RAG.")
  private @Getter @Setter String in;

  public RAG()
  {
    super("rag");
    getLifecycle().willProvide(PhaseType.INITIALIZE);
    setDefaultView("DEFAULT.AI");
  }

  public RAG initialize() throws OpsException
  {
    super.initialize();
    String key = fallback(getKey(), config().getString("key"));
    debug("CONFIG ", config());

    ChatLanguageModel CHAT_MODEL = OpenAiChatModel.builder().apiKey(key)
        .modelName(GPT_4_O_MINI).build();

    List<Document> documents = FileSystemDocumentLoader
        .loadDocuments(getData());

    System.out.println("Documents loaded...");
    Assistant assistant = AiServices.builder(Assistant.class)
        .chatLanguageModel(CHAT_MODEL)
        .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
        .contentRetriever(createContentRetriever(documents)).build();

    System.out.println("Asking: '" + getQuestion() + "'");
    System.out.println("Answer: '" + assistant.answer(getQuestion()) + "'");

    return this;
  }

  private ContentRetriever createContentRetriever(List<Document> documents)
  {
    // Here, we create and empty in-memory store for our documents and their
    // embeddings.
    InMemoryEmbeddingStore<TextSegment> embeddingStore;

    if (getIn() != null)
    {
      embeddingStore = InMemoryEmbeddingStore.fromFile(getIn());
    }
    else
    {
      embeddingStore = new InMemoryEmbeddingStore<>();
      // Here, we are ingesting our documents into the store.
      // Under the hood, a lot of "magic" is happening, but we can ignore it for
      // now.
      EmbeddingStoreIngestor.ingest(documents, embeddingStore);
    }

    if (getOut() != null)
    {
      embeddingStore.serializeToFile(getOut());
    }

    // Lastly, let's create a content retriever from an embedding store.
    return EmbeddingStoreContentRetriever.from(embeddingStore);
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new RAG(), args);
  }
}
