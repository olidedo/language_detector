import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class FolderReader implements Runnable {
    //The path of the language folder that holds the text files
    private Path path;
    //The number of N-Gram
    private int n;
    //The language model
    private LanguageModel languageModel;
    //Creates a Language processor
    private LanguageProcessor languageProcessor = new LanguageProcessor();

    public FolderReader(Path path, int n, LanguageModel languageModel) {
        this.path = path;
        this.n = n;
        this.languageModel = languageModel;
    }

    @Override
    public void run() {
        try {
            //Get a list of the text files inside the folder
            List<Path> files = Files.walk(path)
                    .filter(f -> f.getFileName().toString().endsWith(".txt"))
                    .collect(Collectors.toList());

            List<LanguageProcessor> languageProcessors = files.stream()
                    .map(f -> new LanguageProcessor(f, n, languageModel))
                    .collect(Collectors.toList());

            ExecutorService executorService = Executors.newCachedThreadPool();

            //Processes the files concurrently
            languageProcessors.stream()
                    .forEach(executorService::execute);

            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.MINUTES);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public LanguageModel getLanguageModel() {
        return languageModel;
    }

    @Override
    public String toString() {
        return "FolderReader{" +
                "path=" + path +
                ", n=" + n +
                ", languageProcessor=" + languageProcessor +
                '}';
    }
}
