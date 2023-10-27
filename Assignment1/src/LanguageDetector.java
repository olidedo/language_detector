import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class LanguageDetector {

    private Utils utils = new Utils();

    public static void main(String[] args) {
        try {
            LanguageDetector app = new LanguageDetector();
            String localFolder = args[0];
            int n;
            if (args[1] != null) {
                n = Integer.parseInt(args[1]);
            } else {
                n = 2;
            }
            app.go(localFolder, n);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void go(String localFolder, int n) throws IOException, InterruptedException {
        Path path = Paths.get(localFolder);
        //Get the mystery text in the local folder
        List<Path> mystery = Files.list(path)
                .filter(f -> f.getFileName().toString().endsWith(".txt"))
                .collect(Collectors.toList());
        //Gets all folders of languages
        List<Path> languageFolders = Files.list(path)
                .filter(Files::isDirectory)
                .collect(Collectors.toList());

        //Creates a language model for each language folder
        List<FolderReader> folderReaders = languageFolders.stream()
                .map(f -> new FolderReader(f, n, new LanguageModel()))
                .collect(Collectors.toList());
        //Creates a language model for the mystery text file
        folderReaders.add(new FolderReader(mystery.get(0), n, new LanguageModel()));

        ExecutorService executorService = Executors.newCachedThreadPool();

        //Processes the language folders concurrently
        folderReaders.stream()
                .forEach(executorService::execute);

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        //Get language models after creating them
        List<LanguageModel> languageModels = folderReaders.stream()
                .map(folderReader -> folderReader.getLanguageModel())
                .collect(Collectors.toList());

        //Get the mystery language model
        LanguageModel mysteryModel = languageModels.get(languageModels.size() - 1);
        mysteryModel.setLanguageName("Mystery File");

        //Remove it from the list so it isn't compared to itself
        languageModels.remove(languageModels.size() - 1);

        final Map<String, Double> similarities = new HashMap<>();

        //Calculate similarity of mystery text file with each language model
        languageModels.stream()
                .forEach(i -> similarities.put(i.getLanguageName(), mysteryModel.calculateSimilarity(i)));

        List<Map.Entry<String, Double>> results = similarities.entrySet()
                .stream()
                .collect(Collectors.toList());
        SimilarityComparator similarityComparator = new SimilarityComparator();

        //Declare in class as comparator
        results = results.stream()
                .sorted((t1, t2) -> similarityComparator.compare(t1, t2))
                .collect(Collectors.toList());

        utils.printResults(results);
    }
}
